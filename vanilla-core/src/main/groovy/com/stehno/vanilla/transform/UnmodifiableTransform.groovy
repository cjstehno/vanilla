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
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.transform.ImmutableASTTransformation

import static java.lang.reflect.Modifier.PUBLIC
import static org.codehaus.groovy.ast.ClassHelper.make
import static org.codehaus.groovy.ast.tools.GeneralUtils.*
import static org.codehaus.groovy.ast.tools.GenericsUtils.newClass
import static org.codehaus.groovy.control.CompilePhase.CANONICALIZATION

/**
 * FIXME: document me
 */
@GroovyASTTransformation(phase = CANONICALIZATION)
class UnmodifiableTransform implements ASTTransformation {

    @Override
    void visit(final ASTNode[] nodes, final SourceUnit source) {
        try {
            ClassNode classNode = nodes[1] as ClassNode

            // TODO: immutable object checks

            ClassNode immutableClassNode = createImmutableClass(classNode, source)

            addAsImmutableMethod(classNode, immutableClassNode)


        } catch (Exception ex) {
            ex.printStackTrace()
        }
    }

    private static ClassNode createImmutableClass(ClassNode classNode, SourceUnit source) {
        // TODO: would like this as a static InnerClassNode, but there were issues
        ClassNode immutableClassNode = new ClassNode(
            "${classNode.packageName}.Immutable${classNode.nameWithoutPackage}",
            PUBLIC, // TODO: might be better as protected?
            classNode
        )

        classNode.properties.each { PropertyNode propNode ->
            def field = new FieldNode(
                propNode.field.name,
                propNode.field.modifiers,
                propNode.field.type,
                immutableClassNode,
                new EmptyExpression()
            )

            //            immutableClassNode.addField(field)
            immutableClassNode.addProperty(new PropertyNode(field, propNode.modifiers, null, null))
        }

        def annotationNode = new AnnotationNode(make(Immutable))
        immutableClassNode.addAnnotation(annotationNode)

        // TODO: add asMutable method

        source.AST.addClass(immutableClassNode)

        println "Added immutable class node to AST."

        new ImmutableASTTransformation().visit([annotationNode, immutableClassNode] as ASTNode[], source)

        println "Applied Immutable AST Transformation."

        return immutableClassNode
    }

    private static void addAsImmutableMethod(ClassNode classNode, ClassNode immutableClassNode) {
        classNode.addMethod(new MethodNode(
            "asImmutable",
            PUBLIC,
            newClass(classNode),
            params(),
            [] as ClassNode[],
            block(returnS(ctorX(immutableClassNode, args(
                classNode.properties.collect { PropertyNode pn -> varX(pn.name) }
            ))))
        ))

        println "Added asImmutable() method to the outer class."
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


----
import groovy.transform.Immutable
import groovy.transform.Canonical

@Canonical
class Person {
    String name
    int age
    List<String> pets

    Person asImmutable(){
        new ImmutablePerson(name, age, pets)
    }

    @Immutable
    static class ImmutablePerson extends Person {
        String name
        int age
        List<String> pets

        Person asMutable(){ new Person(name,age, [] + pets) }
    }
}

def person = new Person('Chris',42, ['Fido'])
def immutable = person.asImmutable()
def mutable = immutable.asMutable()

println person
println immutable
println mutable

person.age = person.age+1
person.pets << 'Rover'

immutable.age = immutable.age * 2
//immutable.pets << 'Fluffy'

mutable.age = mutable.age + 5
mutable.pets << 'Duke'

println '--'
println person
println immutable
println mutable
 */