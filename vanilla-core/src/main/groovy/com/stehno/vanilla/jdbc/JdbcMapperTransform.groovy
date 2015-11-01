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

import com.stehno.vanilla.mapper.ObjectMapperConfig
import com.stehno.vanilla.mapper.PropertyMappingConfig
import groovy.transform.TypeChecked
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.PropertyNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import static org.codehaus.groovy.control.CompilePhase.CANONICALIZATION

/**
 * FIXME: document me
 */
@GroovyASTTransformation(phase = CANONICALIZATION) @TypeChecked
class JdbcMapperTransform extends AbstractASTTransformation {

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        AnnotationNode annotationNode = nodes[0] as AnnotationNode
        AnnotatedNode targetNode = nodes[1] as AnnotatedNode

        if (targetNode instanceof FieldNode || targetNode instanceof PropertyNode || targetNode instanceof MethodNode) {
            try {
                ClassNode mappingType = annotationNode.getMember('value') as ClassNode
                ClosureExpression dslClosureX = annotationNode.getMember('config') as ClosureExpression
                // FIXME: style

                ObjectMapperConfig mapperConfig = extractMapperConfig(dslClosureX)

                // generate the static object mapper to be used
//                ClassNode mapperClassNode = createObjectMapperClass(targetNode.declaringClass, mapperName, mapperConfig)
//                source.AST.addClass(mapperClassNode)
//
//                if (targetNode instanceof MethodNode) {
//                    transformMethodNode mapperName, targetNode, mapperClassNode
//
//                } else if (targetNode instanceof FieldNode) {
//                    transformFieldNode targetNode, mapperClassNode
//
//                } else if (targetNode instanceof PropertyNode) {
//                    transformPropertyNode targetNode, mapperClassNode
//
//                } else {
//                    addError "Unsupported application of Mapper annotation for ${targetNode}", targetNode
//                }

            } catch (Exception ex) {
                ex.printStackTrace()
                addError "Problem creating mapper for ${targetNode}: ${ex.message}", targetNode
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
                    throw new IllegalArgumentException('The static mapper DSL commands must at least contain \'map\' calls.')
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

}
