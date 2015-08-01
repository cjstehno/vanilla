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

import groovy.transform.Immutable
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.EmptyExpression
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.transform.ImmutableASTTransformation

import static java.lang.reflect.Modifier.PROTECTED
import static java.lang.reflect.Modifier.PUBLIC
import static org.codehaus.groovy.ast.ClassHelper.make
import static org.codehaus.groovy.ast.tools.GeneralUtils.*
import static org.codehaus.groovy.ast.tools.GenericsUtils.newClass
import static org.codehaus.groovy.control.CompilePhase.CANONICALIZATION

/**
 * FIXME: document me
 */
@GroovyASTTransformation(phase = CANONICALIZATION)
class UnmodifiableTransform extends AbstractASTTransformation {


    private static final String KNOWN_IMMUTABLE_CLASSES = 'knownImmutableClasses'
    private static final String KNOWN_IMMUTABLES = 'knownImmutables'
    private static final String COPY_WITH = 'copyWith'

    @Override
    void visit(final ASTNode[] nodes, final SourceUnit source) {
        AnnotationNode unmodifiableNode = nodes[0] as AnnotationNode
        ClassNode classNode = nodes[1] as ClassNode
        try {

            // TODO: should I do similar check as the Immutable transformer to pre-verify the object?

            ClassNode immutableClassNode = createImmutableClass(classNode, unmodifiableNode, source)

            addAsImmutableMethod(classNode, immutableClassNode)

        } catch (Exception ex) {
            addError("Problem making ${classNode.name} unmodifiable: ${ex.message}", classNode)
            ex.printStackTrace()
        }
    }

    private static ClassNode createImmutableClass(ClassNode classNode, AnnotationNode unmodifiableNode, SourceUnit source) {
        // TODO: would like this as a static InnerClassNode, but there were issues
        ClassNode immutableClassNode = new ClassNode("${classNode.packageName}.Immutable${classNode.nameWithoutPackage}", PROTECTED, classNode)

        // Immutable transformation needs property reference on itself - the extends should do this, but I think
        // that happens after this compile phase (or at runtime).
        classNode.properties.each { PropertyNode propNode ->
            def field = new FieldNode(
                propNode.field.name,
                propNode.field.modifiers,
                newClass(propNode.field.type),
                newClass(immutableClassNode),
                new EmptyExpression()
            )

            immutableClassNode.addProperty(new PropertyNode(field, propNode.modifiers, null, null))
        }

        def annotationNode = buildImmutableAnnotation(unmodifiableNode)
        immutableClassNode.addAnnotation(annotationNode)

        // TODO: add support for the mutable version of the copyWith() method

        immutableClassNode.addMethod(new MethodNode(
            "asMutable",
            PUBLIC,
            newClass(classNode),
            params(),
            [] as ClassNode[],
            block(returnS(ctorX(newClass(classNode), args(
                classNode.properties.collect { PropertyNode pn -> varX(pn.name) }
            ))))
        ))

        source.AST.addClass(immutableClassNode)

        new ImmutableASTTransformation().visit([annotationNode, immutableClassNode] as ASTNode[], source)

        return immutableClassNode
    }

    private static AnnotationNode buildImmutableAnnotation(AnnotationNode unmodifiableNode) {
        def immutableNode = new AnnotationNode(make(Immutable))

        def knownImmutableClases = unmodifiableNode.getMember(KNOWN_IMMUTABLE_CLASSES)
        if (knownImmutableClases) {
            immutableNode.setMember(KNOWN_IMMUTABLE_CLASSES, knownImmutableClases)
        }

        def knownImmutables = unmodifiableNode.getMember(KNOWN_IMMUTABLES)
        if (knownImmutables) {
            immutableNode.setMember(KNOWN_IMMUTABLES, knownImmutables)
        }

        def copyWith = unmodifiableNode.getMember(COPY_WITH)
        if (copyWith) {
            immutableNode.setMember(COPY_WITH, copyWith)
        }

        immutableNode
    }

    private static void addAsImmutableMethod(ClassNode classNode, ClassNode immutableClassNode) {
        def props = classNode.properties.collect { PropertyNode pn -> varX(pn.name, newClass(pn.type)) }

        // TODO: should call .asImmutable() on collections

        def code = block(returnS(ctorX(newClass(immutableClassNode), args(props))))

        classNode.addMethod(new MethodNode(
            "asImmutable",
            PUBLIC,
            newClass(classNode),
            params(),
            [] as ClassNode[],
            code
        ))
    }
}

/*
FIXME: I should probably check these on the Unmodifiable object since they are checked by Immutable
    get knownImmutableClasses
    get knownImmutables
    ensure not interface
    make class final
    adjustPropertyForImmutability
    check for tuple Annotation
    support equalshashcode anno
    support tostrign anno
    cannonical?

    test equality - should immutable be = to mutable with same props?
    test mutable properties between mutable and immutable
*/