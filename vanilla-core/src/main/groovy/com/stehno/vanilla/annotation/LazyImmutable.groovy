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

package com.stehno.vanilla.annotation

import com.stehno.vanilla.transform.LazyImmutableTransform
import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.*

/**
 * FIXME: document me
 *
 * - this annotation provides a simple means of converting to/from immutable/mutable object
 * - only immutable objects are allowed as fields, see rules for Immutable annotation.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Documented
@GroovyASTTransformationClass(classes = [LazyImmutableTransform])
@interface LazyImmutable {

    // FIXME: let's rename this to LazyImmutable

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