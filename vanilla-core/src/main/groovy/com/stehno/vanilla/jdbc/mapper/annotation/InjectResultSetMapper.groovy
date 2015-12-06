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
package com.stehno.vanilla.jdbc.mapper.annotation

import com.stehno.vanilla.jdbc.mapper.MappingStyle
import com.stehno.vanilla.jdbc.mapper.transform.InjectResultSetMapperTransform
import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.*

/**
 * Injects a configured ResultSetMapper into a method or field to provide access to the mapper. Having the mapper created/injected in this manner
 * allows for a slight performance boost since there is less logic code centered around figuring out how to build the mapper - this is done at compile
 * time.
 *
 * The @InjectResultSetMapper annotation may be placed on methods, fields or properties.
 *
 * When used to annotate a method: the method will be made public, static and final and it will use a shared private field instance of the mapper so
 * that it is not created with every method call.
 *
 * When used to annotate a field: the field will be made static, final and public, and its value will be the mapper instance.
 *
 * When used to annotate a property: the property will be based on a private, static and final field instance of the mapper.
 */
@Target(value = [ElementType.FIELD, ElementType.METHOD]) @Retention(RetentionPolicy.SOURCE)
@Documented @GroovyASTTransformationClass(classes = [InjectResultSetMapperTransform])
@interface InjectResultSetMapper {

    /**
     * The type of object to be mapped.
     */
    Class value()

    /**
     * The ResultSetMapper DSL closure.
     */
    @SuppressWarnings(['SpaceAfterOpeningBrace', 'SpaceBeforeClosingBrace'])
    Class config() default {}

    /**
     * The style of mapping to be performed. The default is IMPLICIT if not specified.
     */
    MappingStyle style() default MappingStyle.IMPLICIT

    /**
     * An optional name to be used for the generated mapper. If not specified, a name will be generated based on the object being mapped.
     */
    String name() default ''
}