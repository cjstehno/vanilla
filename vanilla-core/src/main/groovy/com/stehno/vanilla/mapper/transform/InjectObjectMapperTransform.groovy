/*
 * Copyright (C) 2016 Christopher J. Stehno <chris@stehno.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stehno.vanilla.mapper.transform

import com.stehno.vanilla.mapper.AbstractObjectMapper
import com.stehno.vanilla.mapper.ObjectMapper
import com.stehno.vanilla.mapper.ObjectMapperConfig
import com.stehno.vanilla.mapper.PropertyMapping
import groovy.transform.TypeChecked
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import static java.lang.reflect.Modifier.*
import static org.codehaus.groovy.ast.ClassHelper.*
import static org.codehaus.groovy.ast.tools.GeneralUtils.*
import static org.codehaus.groovy.ast.tools.GenericsUtils.newClass
import static org.codehaus.groovy.control.CompilePhase.CANONICALIZATION

/**
 * AST transformer used to process the Mapper annotated methods, properties and fields.
 */
@GroovyASTTransformation(phase = CANONICALIZATION) @TypeChecked
class InjectObjectMapperTransform extends AbstractASTTransformation {

    public static final String DESTINATION = 'destination'
    public static final String SOURCE = 'source'

    @Override @SuppressWarnings(['CatchException', 'PrintStackTrace'])
    void visit(final ASTNode[] nodes, final SourceUnit source) {
        AnnotationNode annotationNode = nodes[0] as AnnotationNode
        AnnotatedNode targetNode = nodes[1] as AnnotatedNode

        if (targetNode instanceof FieldNode || targetNode instanceof PropertyNode || targetNode instanceof MethodNode) {
            try {
                ClosureExpression dslClosureX = annotationNode.getMember('value') as ClosureExpression
                String mapperName = annotationNode.getMember('name')?.text?.capitalize() ?: ''

                ObjectMapperConfig mapperConfig = extractMapperConfig(dslClosureX)

                // generate the static object mapper to be used
                ClassNode mapperClassNode = createObjectMapperClass(targetNode.declaringClass, mapperName, mapperConfig)
                source.AST.addClass(mapperClassNode)

                if (targetNode instanceof MethodNode) {
                    transformMethodNode mapperName, targetNode, mapperClassNode

                } else if (targetNode instanceof FieldNode) {
                    transformFieldNode targetNode, mapperClassNode

                } else if (targetNode instanceof PropertyNode) {
                    transformPropertyNode targetNode, mapperClassNode

                } else {
                    addError "Unsupported application of Mapper annotation for ${targetNode}", targetNode
                }

            } catch (Exception ex) {
                ex.printStackTrace()
                addError "Problem creating ObjectMapper for ${targetNode}: ${ex.message}", targetNode
            }

        } else {
            addError "Invalid member type for object mapper (${targetNode}) - only Fields, Properties and Methods are supported.", targetNode
        }
    }

    private void transformMethodNode(String mapperName, AnnotatedNode targetNode, ClassNode mapperClassNode) {
        String fieldName = "_mapper${mapperName ? '_' + mapperName : ''}"

        targetNode.declaringClass.addField(createFieldNode(fieldName, targetNode, mapperClassNode))

        MethodNode methodNode = targetNode as MethodNode
        methodNode.modifiers = PUBLIC | STATIC | FINAL
        methodNode.code = returnS(varX(fieldName))
    }

    private void transformFieldNode(AnnotatedNode targetNode, ClassNode mapperClassNode) {
        FieldNode fieldNode = targetNode as FieldNode
        fieldNode.modifiers = STATIC | FINAL | PUBLIC
        fieldNode.initialValueExpression = ctorX(newClass(mapperClassNode))
        fieldNode.type = make(ObjectMapper)
    }

    private void transformPropertyNode(AnnotatedNode targetNode, ClassNode mapperClassNode) {
        PropertyNode propertyNode = targetNode as PropertyNode
        propertyNode.field = createFieldNode(propertyNode.name, targetNode, mapperClassNode)
    }

    private FieldNode createFieldNode(String fieldName, AnnotatedNode targetNode, ClassNode mapperClassNode) {
        return new FieldNode(
            fieldName,
            STATIC | FINAL | PRIVATE,
            make(ObjectMapper),
            targetNode.declaringClass,
            ctorX(newClass(mapperClassNode))
        )
    }

    private ObjectMapperConfig extractMapperConfig(final ClosureExpression dslClosureX) {
        ObjectMapperConfig mapperConfig = new ObjectMapperConfig()

        BlockStatement block = dslClosureX.code as BlockStatement
        block.statements.findAll { st -> st instanceof ExpressionStatement }.each { es ->
            Expression expression = (es as ExpressionStatement).expression

            if (expression instanceof MethodCallExpression) {
                def methodXs = [:]
                def node = expression

                while (node && node instanceof MethodCallExpression) {
                    def mex = node as MethodCallExpression

                    String methodName = (mex.method as ConstantExpression).text
                    Object argument = (mex.arguments as ArgumentListExpression)[0]
                    methodXs[methodName] = argument

                    node = (node as MethodCallExpression).objectExpression
                }

                if (!methodXs.containsKey('map')) {
                    throw new IllegalArgumentException('The static mapper DSL commands must at least contain \'map\' calls.')
                }

                PropertyMapping propConfig = mapperConfig.map((methodXs.map as ConstantExpression).text)

                if (methodXs.containsKey('into')) {
                    propConfig.into((methodXs.into as ConstantExpression).text)
                }

                if (methodXs.containsKey('using')) {
                    propConfig.using(methodXs.using)
                }
            }
        }

        mapperConfig
    }

    private static ClassNode createObjectMapperClass(final ClassNode classNode, final String mapperName, final ObjectMapperConfig config) {
        ClassNode mapperClass = new ClassNode(
            "${classNode.packageName}.${mapperName ?: classNode.nameWithoutPackage + 'Mapper'}",
            PUBLIC,
            make(AbstractObjectMapper),
            [] as ClassNode[],
            [] as MixinNode[]
        )

        def code = block()

        config.mappings().each { pm ->
            def sourceGetter = callX(varX(SOURCE), "get${pm.sourceName.capitalize()}")

            if (pm.converter) {
                Expression convertX
                if (pm.converter instanceof ClosureExpression) {
                    ClosureExpression convertClosureX = pm.converter as ClosureExpression

                    def closureArgs = new ArgumentListExpression()

                    int paramCount = convertClosureX.parameters.size()

                    if (paramCount >= 0) {
                        closureArgs.addExpression(sourceGetter)
                    }

                    if (paramCount >= 2) {
                        closureArgs.addExpression(varX(SOURCE))
                    }

                    if (paramCount >= 3) {
                        closureArgs.addExpression(varX(DESTINATION))
                    }

                    convertX = new MethodCallExpression(convertClosureX, 'call', closureArgs)

                } else {
                    throw new IllegalArgumentException('The static mapper DSL only supports Closure-based converters.')
                }

                code.addStatement(
                    stmt(
                        callX(varX(DESTINATION), "set${pm.destinationName.capitalize()}", convertX)
                    )
                )

            } else {
                code.addStatement(
                    stmt(
                        callX(varX(DESTINATION), "set${pm.destinationName.capitalize()}", sourceGetter)
                    )
                )
            }
        }

        mapperClass.addMethod(new MethodNode(
            'copy',
            PUBLIC,
            VOID_TYPE,
            params(param(OBJECT_TYPE, SOURCE), param(OBJECT_TYPE, DESTINATION)),
            [] as ClassNode[],
            code
        ))

        mapperClass
    }
}
