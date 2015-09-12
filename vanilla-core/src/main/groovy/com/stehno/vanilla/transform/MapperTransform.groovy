/*
 * Copyright (c) 2015 Christopher J. Stehno
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

package com.stehno.vanilla.transform
import com.stehno.vanilla.mapper.ObjectMapper
import com.stehno.vanilla.mapper.ObjectMapperConfig
import com.stehno.vanilla.mapper.PropertyMappingConfig
import groovy.transform.TypeChecked
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import static java.lang.reflect.Modifier.PUBLIC
import static org.codehaus.groovy.ast.ClassHelper.*
import static org.codehaus.groovy.ast.tools.GeneralUtils.*
import static org.codehaus.groovy.ast.tools.GenericsUtils.newClass
import static org.codehaus.groovy.control.CompilePhase.CANONICALIZATION
/**
 * FIXME: document
 */
@GroovyASTTransformation(phase = CANONICALIZATION) @TypeChecked
class MapperTransform extends AbstractASTTransformation {

    // FIXME: syntax error handling


    public static final String DESTINATION = 'destination'
    public static final String SOURCE = 'source'

    @Override
    void visit(final ASTNode[] nodes, final SourceUnit source) {
        AnnotationNode annotationNode = nodes[0] as AnnotationNode
        AnnotatedNode targetNode = nodes[1] as AnnotatedNode

        if (targetNode instanceof FieldNode || targetNode instanceof PropertyNode || targetNode instanceof MethodNode) {
            try {
                ClosureExpression dslClosureX = annotationNode.getMember('value') as ClosureExpression
                String mapperName = annotationNode.getMember('name')?.text ?: ''

                ObjectMapperConfig mapperConfig = extractMapperConfig(dslClosureX)

                // generate the static object mapper to be used
                ClassNode mapperClassNode = createObjectMapperClass(targetNode.declaringClass, mapperName, mapperConfig)
                source.AST.addClass(mapperClassNode)

                if (targetNode instanceof MethodNode) {
                    // TODO: consider making this a shared static instance (singleton)
                    (targetNode as MethodNode).code = returnS(ctorX(newClass(mapperClassNode)))

                } else {
                    // FIXME: others!
                }

            } catch (Exception ex) {
                ex.printStackTrace()
                addError "Problem creating ObjectMapper for ${targetNode}: ${ex.message}", targetNode
            }

        } else {
            addError "Invalid member type for object mapper (${targetNode}) - only Fields, Properties and Methods are supported.", targetNode
        }
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
                    // FIXME: error
                }

                PropertyMappingConfig propConfig = mapperConfig.map((methodXs.map as ConstantExpression).text)

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
            newClass(OBJECT_TYPE),
            [] as ClassNode[],
            [] as MixinNode[]
        )
        mapperClass.implementsInterface(make(ObjectMapper))

        def code = block()

        config.mappings().each { pm ->
            def sourceGetter = callX(varX(SOURCE), "get${pm.sourceName.capitalize()}")

            if (pm.converter) {
                // FIXME: add support for object mapper converter

                ClosureExpression convertClosureX = pm.converter as ClosureExpression

                MethodCallExpression convertCall = new MethodCallExpression(convertClosureX, 'call', args(sourceGetter))

                code.addStatement(
                    stmt(
                        callX(varX(DESTINATION), "set${pm.destinationName.capitalize()}", convertCall)
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
