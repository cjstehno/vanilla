/*
 * Copyright (C) 2015 Christopher J. Stehno <chris@stehno.com>
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
package com.stehno.vanilla.jdbc

import groovy.transform.TypeChecked
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import java.sql.ResultSet

import static com.stehno.vanilla.jdbc.MappingStyle.valueOf
import static groovy.transform.TypeCheckingMode.SKIP
import static java.lang.reflect.Modifier.*
import static org.codehaus.groovy.ast.ClassHelper.OBJECT_TYPE
import static org.codehaus.groovy.ast.ClassHelper.make
import static org.codehaus.groovy.ast.tools.GeneralUtils.*
import static org.codehaus.groovy.ast.tools.GenericsUtils.newClass
import static org.codehaus.groovy.control.CompilePhase.CANONICALIZATION

/**
 * FIXME: document me
 */
@GroovyASTTransformation(phase = CANONICALIZATION) @TypeChecked
class JdbcMapperTransform extends AbstractASTTransformation {

    private static final Collection<String> DEFAULT_IGNORED = [
        'class', 'metaClass', '$staticClassInfo', '__$stMC', '$staticClassInfo$', '$callSiteArray'
    ].asImmutable()

    private static final String RS = 'rs'

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        AnnotationNode annotationNode = nodes[0] as AnnotationNode
        AnnotatedNode targetNode = nodes[1] as AnnotatedNode

        if (targetNode instanceof FieldNode || targetNode instanceof PropertyNode || targetNode instanceof MethodNode) {
            try {
                ClassExpression mappedType = annotationNode.getMember('value') as ClassExpression
                ClosureExpression dslClosureX = annotationNode.getMember('config') as ClosureExpression
                PropertyExpression mappingStyle = annotationNode.getMember('style') as PropertyExpression
                ConstantExpression nameX = annotationNode.getMember('name') as ConstantExpression

                CompiledResultSetMapperBuilder mapperConfig = extractMapperConfig(
                    mappedType.type,
                    dslClosureX,
                    mappingStyle ? valueOf(mappingStyle.propertyAsString) : MappingStyle.IMPLICIT
                )

                ClassNode mapperClassNode = createMapperClass(mapperName(mappedType, nameX), mapperConfig)
                source.AST.addClass(mapperClassNode)

                if (targetNode instanceof MethodNode) {
                    transformMethodNode targetNode, mapperClassNode

                } else if (targetNode instanceof FieldNode) {
                    transformFieldNode targetNode, mapperClassNode

                } else if (targetNode instanceof PropertyNode) {
                    transformPropertyNode targetNode, mapperClassNode

                } else {
                    addError "Unsupported application of JdbcMapper annotation for ${targetNode}", targetNode
                }

            } catch (Exception ex) {
                ex.printStackTrace()
                addError "Problem creating mapper for ${targetNode}: ${ex.message}", targetNode
            }

        } else {
            addError "Invalid member type for mapper (${targetNode}) - only Fields, Properties and Methods are supported.", targetNode
        }
    }

    private static String mapperName(ClassExpression mappedType, ConstantExpression nameX) {
        return nameX ? "${mappedType.type.package.name}.${nameX.value}" : "${mappedType.type.name}RowMapper"
    }

    private CompiledResultSetMapperBuilder extractMapperConfig(ClassNode mappedType, ClosureExpression dslClosureX, MappingStyle mappingStyle) {
        CompiledResultSetMapperBuilder mapperConfig = new CompiledResultSetMapperBuilder(mappedType, mappingStyle)

        BlockStatement block = dslClosureX.code as BlockStatement
        block.statements.findAll { st -> st instanceof ExpressionStatement }.each { es ->
            Expression expression = (es as ExpressionStatement).expression

            if (expression instanceof MethodCallExpression) {
                def methodXs = [:]
                def node = expression

                while (node && node instanceof MethodCallExpression) {
                    def mex = node as MethodCallExpression

                    String methodName = (mex.method as ConstantExpression).text
                    methodXs[methodName] = (mex.arguments as ArgumentListExpression).expressions
                    node = (node as MethodCallExpression).objectExpression
                }

                if (methodXs.containsKey('ignore')) {
                    methodXs.ignore.each { mi ->
                        mapperConfig.ignore((mi as ConstantExpression).text)
                    }

                } else if (methodXs.containsKey('map')) {
                    def propertyNameX = (methodXs.map as List<Expression>)[0] as ConstantExpression
                    FieldMapping fieldMapping = mapperConfig.map(propertyNameX.value as String)

                    handleFroms methodXs, fieldMapping

                    if (methodXs.containsKey('using')) {
                        // TODO: refactor the config so I don't need to shoehorn the compiled stuff into closures (?)
                        fieldMapping.using({ (methodXs.using as List<Expression>)[0] as ClosureExpression })
                    }

                } else {
                    throw new IllegalArgumentException('Mapper DSL commands must at least contain \'map\' or \'ignore\' calls.')
                }
            }
        }

        mapperConfig
    }

    @TypeChecked(SKIP)
    private static void handleFroms(Map<String, Expression> methods, FieldMapping mapping) {
        def fromEntry = methods.find { k, v -> k.startsWith('from') }
        if (fromEntry) {
            mapping."${fromEntry.key}"(fromEntry.value[0] as ConstantExpression)
        }
    }

    private static ClassNode createMapperClass(final String mapperName, final CompiledResultSetMapperBuilder config) {
        ClassNode mapperClass = new ClassNode(mapperName, PUBLIC, newClass(make(CompiledResultSetMapper)), [] as ClassNode[], [] as MixinNode[])

        List<MapEntryExpression> mapEntryExpressions = []

        if (config.style == MappingStyle.IMPLICIT) {
            def ignored = DEFAULT_IGNORED + config.ignored()

            config.mappedTypeNode.fields.findAll { FieldNode fn -> !(fn.name in ignored) }.each { FieldNode fn ->
                implementMapping mapEntryExpressions, config.findMapping(fn.name)
            }

        } else {
            config.mappings().each { fieldMapping ->
                implementMapping mapEntryExpressions, fieldMapping
            }
        }

        mapperClass.addMethod(new MethodNode(
            'call',
            PUBLIC,
            OBJECT_TYPE,
            params(param(make(ResultSet), RS),),
            [] as ClassNode[],
            returnS(ctorX(newClass(config.mappedTypeNode), args(new MapExpression(mapEntryExpressions))))
        ))

        mapperClass
    }

    @TypeChecked(SKIP)
    private static void implementMapping(List<MapEntryExpression> mapEntryExpressions, FieldMapping fieldMapping) {
        Expression extractorX = fieldMapping.extractor() as Expression

        if (fieldMapping.converter) {
            Expression convertX
            if (fieldMapping.converter() instanceof ClosureExpression) {
                ClosureExpression convertClosureX = fieldMapping.converter() as ClosureExpression

                convertX = new MethodCallExpression(convertClosureX, 'call', convertClosureX.parameters.size() ? args(extractorX) : args())

            } else {
                throw new IllegalArgumentException('The static mapper DSL only supports Closure-based converters.')
            }

            mapEntryExpressions << new MapEntryExpression(constX(fieldMapping.propertyName), convertX)

        } else {
            mapEntryExpressions << new MapEntryExpression(constX(fieldMapping.propertyName), extractorX)
        }
    }

    private void transformMethodNode(AnnotatedNode targetNode, ClassNode mapperClassNode) {
        String fieldName = "_mapper${mapperClassNode.nameWithoutPackage}"

        targetNode.declaringClass.addField(createFieldNode(fieldName, targetNode, mapperClassNode))

        MethodNode methodNode = targetNode as MethodNode
        methodNode.modifiers = PUBLIC | STATIC | FINAL
        methodNode.code = returnS(varX(fieldName))
    }

    private FieldNode createFieldNode(String fieldName, AnnotatedNode targetNode, ClassNode mapperClassNode) {
        return new FieldNode(
            fieldName,
            STATIC | FINAL | PRIVATE,
            make(ResultSetMapper),
            targetNode.declaringClass,
            ctorX(newClass(mapperClassNode))
        )
    }

    private void transformFieldNode(AnnotatedNode targetNode, ClassNode mapperClassNode) {
        FieldNode fieldNode = targetNode as FieldNode
        fieldNode.modifiers = STATIC | FINAL | PUBLIC
        fieldNode.initialValueExpression = ctorX(newClass(mapperClassNode))
        fieldNode.type = make(ResultSetMapper)
    }

    private void transformPropertyNode(AnnotatedNode targetNode, ClassNode mapperClassNode) {
        PropertyNode propertyNode = targetNode as PropertyNode
        propertyNode.field = createFieldNode(propertyNode.name, targetNode, mapperClassNode)
    }

}
