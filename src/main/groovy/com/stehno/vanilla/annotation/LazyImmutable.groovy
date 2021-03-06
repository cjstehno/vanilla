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
package com.stehno.vanilla.annotation

import com.stehno.vanilla.transform.LazyImmutableTransform
import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.*

/**
 * When this annotation is applied to a class, it will add a method <code>asImmutable()</code> which will generate an immutable instance of the
 * object. The original instance will be unchanged. The generated immutable instance is annotated with the <code>@Immutable</code> annotation and the
 * class annotated with <code>@LazyImmutable</code> should follow all of the rules described in the documentation of the <code>@Immutable</code>
 * annotation (specifically the restriction to property values which are immutable or can be guaranteed immutable).
 *
 * The immutable version of the object will have a method <code>asMutable()</code> added to it, which will provide a new instance of the original
 * mutable version of the object (a new instance, not a reference).
 *
 * The primary purpose of this annotation is to allow for the creation of immutable objects in a lazy manner, by setting or updating properties on the
 * object and then generating the immutable version by calling the <code>asImmutable()</code> method.
 *
 * The generated immutable object is an extension of the original object, so it may be used interchangeably.
 *
 * For example, you could use the <code>@Builder</code> annotation as follows:
 *
 * <pre>
 * @Builder @LazyImmutable
 * class Name {
 *     String firstName
 *     String middleName
 *     String lastName
 * }
 *
 * def name = Name.builder()
 *     .firstName('John')
 *     .middleName('Q')
 *     .lastName('Public')
 *     .build()
 *     .asImmutable()
 * </pre>
 *
 * The bottom line being that, unlike a standard immutable, the lazy immutable may be built up over the course of a method rather than all at once.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Documented
@GroovyASTTransformationClass(classes = [LazyImmutableTransform])
@interface LazyImmutable {

    /**
     * Allows you to provide the generated Immutable class with a list of classes which are deemed immutable. By supplying a class in this list,
     * you are vouching for its immutability and the Immutable will do no further checks.
     */
    Class[] knownImmutableClasses() default []

    /**
     * Allows you to provide the generated Immutable with a list of property names which are deemed immutable. By supplying a property's name in
     * this list, you are vouching for its immutability the Immutable will do no further checks.
     */
    String[] knownImmutables() default []

    /**
     * If `true`, this adds a method `copyWith(Map)` which takes a Map of new property values and returns a new instance of
     * the generated Immutable class with these values set. This method will be available on both the mutable and immutable
     * version of the object, though in the later case, the resulting object will itself be a new instance of the mutable version
     * of the object.
     */
    boolean copyWith() default false
}