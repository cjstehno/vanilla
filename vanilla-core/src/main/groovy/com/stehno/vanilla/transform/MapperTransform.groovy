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
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import static groovy.transform.TypeCheckingMode.SKIP
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
                // FIXME: experimenting here with the code (rather than text) approach

                ObjectMapperConfig mapperConfig = new ObjectMapperConfig() // TODO: probably dont need this construct (?)

                ClosureExpression dslClosureX = annotationNode.getMember('value') as ClosureExpression
                BlockStatement block = dslClosureX.code as BlockStatement
                block.statements.each { st->
                    Expression expression = (st as ExpressionStatement).expression
                    println "${expression.text} --> $expression"

                    if( expression instanceof  MethodCallExpression){
                        MethodCallExpression mex = expression as MethodCallExpression
                        if( mex.objectExpression instanceof VariableExpression ){
                            // simple mapping
                            ArgumentListExpression args = mex.arguments as ArgumentListExpression
                            mapperConfig.map(args[0].text)

                        } else if( mex.objectExpression instanceof MethodCallExpression ){
                            // FIXME: has into or using

                            // map
                            ArgumentListExpression mapArgs = (mex.objectExpression as MethodCallExpression).arguments as ArgumentListExpression
                            PropertyMappingConfig pmc = mapperConfig.map(mapArgs[0].text)

                            if( mex.method.text == 'into'){
                                println '---> into'
                                ArgumentListExpression intoArgs = mex.arguments as ArgumentListExpression
                                pmc.into(intoArgs[0].text)

                            } else if( mex.method.text == 'using'){
                                println '---> using'
                                ArgumentListExpression usingArgs = mex.arguments as ArgumentListExpression
                                pmc.using(usingArgs[0])

                                if( mex.objectExpression instanceof MethodCallExpression){
                                    MethodCallExpression mex2 = mex.objectExpression as MethodCallExpression
                                    if( mex2.method.text == 'into'){
                                        this is still a bit off but on the right track - diagram this out and come up with more elegant way to traverse
                                        ArgumentListExpression intoArgs = (mex2.objectExpression as MethodCallExpression).arguments as ArgumentListExpression
                                        pmc.into(intoArgs[0].text)
                                    }
                                }

                                /*
                                org.codehaus.groovy.ast.expr.MethodCallExpression@6fdbe764[
                                    object: org.codehaus.groovy.ast.expr.MethodCallExpression@51c668e3[
                                        object: org.codehaus.groovy.ast.expr.MethodCallExpression@2e6a8155[
                                            object: org.codehaus.groovy.ast.expr.VariableExpression@b9b00e0[variable: this]
                                            method: ConstantExpression[map]
                                            arguments: org.codehaus.groovy.ast.expr.ArgumentListExpression@6221a451[ConstantExpression[birthDate]]
                                        ]
                                        method: ConstantExpression[into]
                                        arguments: org.codehaus.groovy.ast.expr.ArgumentListExpression@52719fb6[ConstantExpression[birthday]]
                                    ]
                                    method: ConstantExpression[using]
                                    arguments: org.codehaus.groovy.ast.expr.ArgumentListExpression@3012646b[org.codehaus.groovy.ast.expr.ClosureExpression@4a883b15[org.codehaus.groovy.ast.Parameter@25641d39[name:d type: java.lang.Object, hasDefaultValue: false]]{ org.codehaus.groovy.ast.stmt.BlockStatement@7b36aa0c[org.codehaus.groovy.ast.stmt.ExpressionStatement@5824a83d[expression:org.codehaus.groovy.ast.expr.MethodCallExpression@537f60bf[object: org.codehaus.groovy.ast.expr.VariableExpression@5677323c[variable: d] method: ConstantExpression[format] arguments: org.codehaus.groovy.ast.expr.ArgumentListExpression@18df8434[org.codehaus.groovy.ast.expr.VariableExpression@65c7a252[variable: BASIC_ISO_DATE]]]]] }]
                                ]
                                */
                            }

                        } else {
                            // FIXME: error
                        }
                    }
                }

                mapperConfig.mappings().each { m->
                    println m.dump()
                }

                return

                ////////////////

                String configDsl = annotationNode.getMember('value').text
                String mapperName = annotationNode.getMember('name')?.text ?: ''

                // load the DSL and generate the configuration
                ObjectMapperConfig config = configure(configDsl)

                // generate the static object mapper to be used
                ClassNode mapperClassNode = createObjectMapperClass(targetNode.declaringClass, mapperName, config)
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

        // FIXME: other mapper operations
        config.mappings().each { pm ->
            def sourceGetter = callX(varX(SOURCE), "get${pm.sourceName.capitalize()}")

            if (pm.converter) {
                // FIXME: need to figure out how to handle conversion closure
                code.addStatement(
                    stmt(
                        callX(varX(DESTINATION), "set${pm.destinationName.capitalize()}", sourceGetter)
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

    @TypeChecked(SKIP)
    private static ObjectMapperConfig configure(final String config) {
        Script script = new GroovyShell(new CompilerConfiguration(scriptBaseClass: DelegatingScript.class.name)).parse(config)

        ObjectMapperConfig mapper = new ObjectMapperConfig()
        script.setDelegate(mapper)
        script.run()

        mapper
    }
}
