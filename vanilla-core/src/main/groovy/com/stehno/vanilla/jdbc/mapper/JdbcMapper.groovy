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
package com.stehno.vanilla.jdbc.mapper

import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.*

/**
 * FIXME: document me
 */
@Target(value = [ElementType.FIELD, ElementType.METHOD])
@Retention(RetentionPolicy.SOURCE)
@Documented
@GroovyASTTransformationClass(classes = [JdbcMapperTransform])
@interface JdbcMapper {

    /**
     *
     * @return
     */
    Class value()

    /**
     * The ResultSetMapper DSL closure.
     */
    @SuppressWarnings(['SpaceAfterOpeningBrace', 'SpaceBeforeClosingBrace'])
    Class config() default {}

    /**
     * FIXME: Document
     */
    MappingStyle style() default MappingStyle.IMPLICIT

    String name() default ''
}