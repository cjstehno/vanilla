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

import groovy.transform.ToString

import static MappingStyle.IMPLICIT

/**
 * FIXME: document me
 */
@ToString(includeFields = true, includeNames = true)
class ResultSetMapperBuilder implements ResultSetMapperDsl {

    final Class mappedType
    final MappingStyle style
    private final Collection<String> ignoredNames = []
    protected final Map<String, FieldMapping> mappings = [:]

    ResultSetMapperBuilder(final Class mappedType, final MappingStyle style) {
        this.mappedType = mappedType
        this.style = style
    }

    static ResultSetMapper mapper(Class mappedType, MappingStyle style = IMPLICIT, @DelegatesTo(ResultSetMapperDsl) Closure closure) {
        ResultSetMapperBuilder builder = new ResultSetMapperBuilder(mappedType, style)

        closure.delegate = builder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()

        builder.build()
    }

    FieldMapping findMapping(String propertyName) {
        mappings[propertyName]
    }

    Collection<String> ignored() {
        ignoredNames.asImmutable()
    }

    boolean isIgnored(String propertyName) {
        propertyName in ignoredNames
    }

    @SuppressWarnings('ConfusingMethodName')
    Collection<FieldMapping> mappings() {
        mappings.values().asImmutable()
    }

    ResultSetMapper build() {
        new RuntimeResultSetMapper(this)
    }

    FieldMapping map(String propertyName) {
        FieldMapping mapping = new RuntimeFieldMapping(propertyName)
        mappings[propertyName] = mapping
        mapping
    }

    void ignore(String... propertyNames) {
        this.ignoredNames.addAll(propertyNames)
    }
}