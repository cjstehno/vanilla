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
package com.stehno.vanilla.jdbc.mapper.transform

import com.stehno.vanilla.jdbc.mapper.FieldMapping
import com.stehno.vanilla.jdbc.mapper.MappingStyle
import com.stehno.vanilla.jdbc.mapper.ResultSetMapper
import groovy.transform.TypeChecked
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import java.sql.ResultSet

import static groovy.transform.TypeCheckingMode.SKIP
import static java.lang.reflect.Modifier.*
import static org.codehaus.groovy.ast.ClassHelper.OBJECT_TYPE
import static org.codehaus.groovy.ast.ClassHelper.make
import static org.codehaus.groovy.ast.tools.GeneralUtils.*
import static org.codehaus.groovy.ast.tools.GenericsUtils.newClass
import static org.codehaus.groovy.control.CompilePhase.CANONICALIZATION

/**
 * AST Transformation used to process the <code>InjectResultSetMapper</code> annotation. This class should not be used externally.
 */
@GroovyASTTransformation(phase = CANONICALIZATION) @TypeChecked
class InjectResultSetMapperTransform extends AbstractASTTransformation {

    private static final Collection<String> DEFAULT_IGNORED = ['metaClass', 'property'].asImmutable()

    private static final String RS = 'rs'
    private static final String CALL = 'call'

    @Override @SuppressWarnings(['CatchException', 'PrintStackTrace'])
    void visit(ASTNode[] nodes, SourceUnit source) {
        AnnotationNode annotationNode = nodes[0] as AnnotationNode
        AnnotatedNode targetNode = nodes[1] as AnnotatedNode

        if (targetNode instanceof FieldNode || targetNode instanceof PropertyNode || targetNode instanceof MethodNode) {
            try {
                ClassExpression mappedType = annotationNode.getMember('value') as ClassExpression
                ClosureExpression dslClosureX = annotationNode.getMember('config') as ClosureExpression
                PropertyExpression mappingStyle = annotationNode.getMember('style') as PropertyExpression
                ConstantExpression nameX = annotationNode.getMember('name') as ConstantExpression

                MappingStyle styleEnum = mappingStyle ? MappingStyle.valueOf(mappingStyle.propertyAsString) : MappingStyle.IMPLICIT

                if (!dslClosureX && styleEnum == MappingStyle.EXPLICIT) {
                    throw new IllegalArgumentException('A configuration closure must be provided for EXPLICIT mappers.')
                }

                CompiledResultSetMapperBuilder mapperConfig = extractMapperConfig(mappedType.type, dslClosureX, styleEnum)

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

        BlockStatement block = dslClosureX?.code as BlockStatement

        if (block) {
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
                            fieldMapping.using((methodXs.using as List<Expression>)[0] as ClosureExpression)
                        }

                    } else {
                        throw new IllegalArgumentException(/Mapper DSL commands must at least contain 'map' or 'ignore' calls./)
                    }
                }
            }

        } else {
            // implicit without DSL
            mappedType.methods.findAll { MethodNode mn -> isAcceptedSetter(mn, DEFAULT_IGNORED) }.each { MethodNode mn ->
                mapperConfig.map(propertyName(mn.name))
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

    private static String propertyName(String setterName) {
        String x = setterName[3..(-1)]
        return "${x[0].toLowerCase()}${x[1..(-1)]}"
    }

    private static ClassNode createMapperClass(final String mapperName, final CompiledResultSetMapperBuilder config) {
        ClassNode mapperClass = new ClassNode(mapperName, PUBLIC, newClass(make(CompiledResultSetMapper)), [] as ClassNode[], [] as MixinNode[])

        List<MapEntryExpression> mapEntryExpressions = []

        if (config.style == MappingStyle.IMPLICIT) {
            def ignored = DEFAULT_IGNORED + config.ignored()

            config.mappedTypeNode.methods.findAll { MethodNode mn -> isAcceptedSetter(mn, ignored) }.each { MethodNode mn ->
                implementMapping mapEntryExpressions, config.findMapping(propertyName(mn.name))
            }

        } else {
            config.mappings().each { fieldMapping ->
                implementMapping mapEntryExpressions, fieldMapping
            }
        }

        mapperClass.addMethod(new MethodNode(
            CALL,
            PUBLIC,
            OBJECT_TYPE,
            params(param(make(ResultSet), RS),),
            [] as ClassNode[],
            returnS(ctorX(newClass(config.mappedTypeNode), args(new MapExpression(mapEntryExpressions))))
        ))

        mapperClass
    }

    private static boolean isAcceptedSetter(final MethodNode mn, final Collection ignored) {
        mn.public && !mn.static && mn.name.startsWith('set') && !(propertyName(mn.name) in ignored)
    }

    @TypeChecked(SKIP)
    private static void implementMapping(List<MapEntryExpression> mapEntryExpressions, FieldMapping fieldMapping) {
        Expression extractorX = fieldMapping.extractor as Expression

        if (fieldMapping.converter) {
            Expression convertX
            if (fieldMapping.converter instanceof ClosureExpression) {
                ClosureExpression convertClosureX = fieldMapping.converter as ClosureExpression

                convertX = new MethodCallExpression(convertClosureX, CALL, convertClosureX.parameters.size() ? args(extractorX) : args())

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
            ClassHelper.make(ResultSetMapper),
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
