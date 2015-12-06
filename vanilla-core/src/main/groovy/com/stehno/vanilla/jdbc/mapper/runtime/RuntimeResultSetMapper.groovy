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
package com.stehno.vanilla.jdbc.mapper.runtime

import com.stehno.vanilla.jdbc.mapper.*
import groovy.transform.TypeChecked

import java.sql.ResultSet

import static com.stehno.vanilla.jdbc.mapper.MappingStyle.IMPLICIT

/**
 * Dynamic runtime implementation of the <code>ResultSetMapper</code> interface. The mapping information is immutable and the actual mapping
 * and extraction operations occur at runtime.
 *
 * The mapper is reusable and thread-safe.
 */
@TypeChecked
class RuntimeResultSetMapper implements ResultSetMapper {

    private final Class mappedType
    private final Map<String, FieldMapping> mappings
    private final Collection<String> ignored = []

    /**
     * Creates an instance of the mapper with the provided configuration information. Generally, the <code>ResultSetMapperBuilder</code> is used to
     * create instances of this mapper; however, using this constructor directly is acceptable.
     *
     * @param mappedType the type being mapped
     * @param mappings the map of object property name to field mappings
     * @param ignored the collection of ignored object properties (may be omitted)
     */
    RuntimeResultSetMapper(Class mappedType, Map<String, FieldMapping> mappings, Collection<String> ignored = []) {
        this.mappedType = mappedType
        this.mappings = mappings.asImmutable()

        if (ignored) {
            this.ignored.addAll(ignored)
        }
    }

    /**
     * Used to build a <code>ResultSetMapper</code> using the DSL.
     *
     * @param mappedType the type of object being mapped
     * @param style the mapping style to be used (defaults to IMPLICIT if not specified)
     * @param closure the DSL closure
     * @return the configured ResultSetMapper
     */
    static ResultSetMapper mapper(Class mappedType, MappingStyle style = IMPLICIT, @DelegatesTo(ResultSetMapperDsl) Closure closure) {
        ResultSetMapperBuilder builder = new ResultSetMapperBuilder(mappedType, style)

        if (closure) {
            closure.delegate = builder
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.call()
        }

        builder.build()
    }

    static ResultSetMapper mapper(Class mappedType, MappingStyle style = IMPLICIT) {
        mapper(mappedType, style, null)
    }

    @SuppressWarnings('UnusedMethodParameter')
    def mapRow(ResultSet rs, int rownum) {
        call rs
    }

    def call(ResultSet rs) {
        def instanceProps = [:]

        mappings.each { String prop, FieldMapping mapping ->
            applyMapping rs, mapping, instanceProps
        }

        mappedType.newInstance(instanceProps)
    }

    private static void applyMapping(final ResultSet rs, final FieldMapping mapping, final Map<String, Object> instanceProps) {
        def mappedValue = (mapping.extractor as Closure).call(rs)
        Closure converter = mapping.converter as Closure
        if (converter) {
            int argCount = converter.maximumNumberOfParameters
            if (argCount > 0) {
                mappedValue = converter.call(mappedValue)
            } else {
                mappedValue = converter.call()
            }
        }

        instanceProps[mapping.propertyName] = mappedValue
    }
}
