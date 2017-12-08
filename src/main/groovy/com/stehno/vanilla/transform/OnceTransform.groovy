/*
 * Copyright (C) 2017 Christopher J. Stehno <chris@stehno.com>
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

import groovy.transform.TypeChecked
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.messages.SyntaxErrorMessage
import org.codehaus.groovy.syntax.SyntaxException
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import java.util.concurrent.atomic.AtomicBoolean

import static java.lang.reflect.Modifier.FINAL
import static java.lang.reflect.Modifier.PRIVATE
import static java.lang.reflect.Modifier.PUBLIC
import static org.codehaus.groovy.ast.ClassHelper.Boolean_TYPE
import static org.codehaus.groovy.ast.ClassHelper.VOID_TYPE
import static org.codehaus.groovy.ast.ClassHelper.make
import static org.codehaus.groovy.ast.tools.GeneralUtils.args
import static org.codehaus.groovy.ast.tools.GeneralUtils.block
import static org.codehaus.groovy.ast.tools.GeneralUtils.callX
import static org.codehaus.groovy.ast.tools.GeneralUtils.constX
import static org.codehaus.groovy.ast.tools.GeneralUtils.ctorX
import static org.codehaus.groovy.ast.tools.GeneralUtils.ifElseS
import static org.codehaus.groovy.ast.tools.GeneralUtils.ifS
import static org.codehaus.groovy.ast.tools.GeneralUtils.notX
import static org.codehaus.groovy.ast.tools.GeneralUtils.params
import static org.codehaus.groovy.ast.tools.GeneralUtils.returnS
import static org.codehaus.groovy.ast.tools.GeneralUtils.throwS
import static org.codehaus.groovy.ast.tools.GeneralUtils.varX
import static org.codehaus.groovy.control.CompilePhase.CANONICALIZATION

/**
 * AST Transformation support for the @Once annotation.
 */
@GroovyASTTransformation(phase = CANONICALIZATION) @TypeChecked
class OnceTransform extends AbstractASTTransformation {

    @Override
    void visit(final ASTNode[] nodes, final SourceUnit source) {
        AnnotationNode onceNode = nodes[0] as AnnotationNode
        MethodNode methNode = nodes[1] as MethodNode
        try {
            if (methNode.returnType == VOID_TYPE) {
                ClassNode statusFlagClassNode = make(AtomicBoolean)

                // add boolean status field
                String fieldName = onceNode.getMember('fieldName')?.text ?: onceNode.getMember('value')?.text ?: "${methNode.name}Called"

                ClassNode ownerClass = methNode.declaringClass
                FieldNode statusField = new FieldNode(
                    fieldName,
                    FINAL | PRIVATE,
                    statusFlagClassNode,
                    ownerClass,
                    ctorX(statusFlagClassNode, args(constX(false)))
                )

                ownerClass.addField(statusField)
                ownerClass.addMethod(new MethodNode(
                    "is${fieldName.capitalize()}",
                    PUBLIC,
                    Boolean_TYPE,
                    params(),
                    [] as ClassNode[],
                    returnS(callX(varX(fieldName), 'get')))
                )

                // wrap the original method body
                Statement originalCode = methNode.code

                BlockStatement code = block()

                Expression condition = notX(callX(varX(fieldName), 'getAndSet', args(constX(true))))

                if (ignoreMultipleCalls(onceNode)) {
                    code.addStatement(ifS(condition, originalCode))

                } else {
                    code.addStatement(ifElseS(
                        condition,
                        originalCode,
                        throwS(ctorX(make(IllegalStateException), args(constX("Method ${methNode.name} was called more than once." as String))))
                    ))
                }

                methNode.code = code

            } else {
                addError "Methods annotated with @Once must have a void return type: $methNode.name returns $methNode.returnType.name", methNode
            }
        } catch (Exception ex) {
            addError "Problem applying once transform tomethod ($methNode.name): $ex.message", methNode
        }
    }

    private static boolean ignoreMultipleCalls(AnnotationNode onceNode) {
        Expression attr = onceNode.getMember('ignoreMultipleCalls')
        !attr || attr?.text == 'true'
    }

    void addError(String msg, ASTNode expr) {
        sourceUnit.errorCollector.addErrorAndContinue(
            new SyntaxErrorMessage(new SyntaxException(
                msg + '\n',
                expr.lineNumber,
                expr.columnNumber,
                expr.lastLineNumber,
                expr.lastColumnNumber
            ), sourceUnit)
        )
    }
}
