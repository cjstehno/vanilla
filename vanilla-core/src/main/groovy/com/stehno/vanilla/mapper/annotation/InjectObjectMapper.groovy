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
package com.stehno.vanilla.mapper.annotation

import com.stehno.vanilla.mapper.transform.InjectObjectMapperTransform
import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.*

/**
 * Annotation used to inject a static ObjectMapper instance into a class using the ObjectMapper DSL. Having the
 * ObjectMapper created/injected in this manner allows for a slight performance boost since there is less logic code
 * centered around figuring out how to build the mapper - this is done at compile time.
 *
 * The @InjectObjectMapper annotation may be placed on methods, fields or properties.
 *
 * When used to annotate a method: the method will be made public, static and final and it will use a shared private
 * field instance of the mapper so that it is not created with every method call.
 *
 * When used to annotate a field: the field will be made static, final and public, and its value will be the mapper instance.
 *
 * When used to annotate a property: the property will be based on a private, static and final field instance of the mapper.
 *
 * The compiled version of the ObjectMapper does not support the Map to Map style of mapping - as the runtime object mapper does.
 *
 * Note: the static version of the ObjectMapper DSL only supports Closure-style converters. Another mapper may be wrapped
 * inside a closure call; however, they are not directly supported as converters as they are in the runtime mapper.
 */
@Target(value = [ElementType.FIELD, ElementType.METHOD])
@Retention(RetentionPolicy.SOURCE)
@Documented
@GroovyASTTransformationClass(classes = [InjectObjectMapperTransform])
@interface InjectObjectMapper {

    /**
     * The ObjectMapper DSL closure.
     */
    Class value()

    /**
     * Optional name used to name the mapper class. If this property is not specified, the name of the containing class
     * will be used. If there is more than one mapper defined in a class, this field is required to distinguish between
     * them.
     */
    String name() default ''
}