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

import com.stehno.vanilla.jdbc.mapper.FieldMapping
import com.stehno.vanilla.jdbc.mapper.MappingStyle
import com.stehno.vanilla.jdbc.mapper.ResultSetMapper
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

    private static final Collection<String> DEFAULT_IGNORED = ['class'].asImmutable()

    private final Class mappedType
    private final MappingStyle mappingStyle
    private final Map<String, FieldMapping> mappings
    private final Collection<String> ignored = []

    /**
     * Creates an instance of the mapper with the provided configuration information. Generally, the <code>ResultSetMapperBuilder</code> is used to
     * create instances of this mapper; however, using this constructor directly is acceptable.
     *
     * @param mappedType the type being mapped
     * @param mappingStyle the mapping style to be used
     * @param mappings the map of object property name to field mappings
     * @param ignored the collection of ignored object properties (may be omitted)
     */
    RuntimeResultSetMapper(Class mappedType, MappingStyle mappingStyle, Map<String, FieldMapping> mappings, Collection<String> ignored = []) {
        this.mappedType = mappedType
        this.mappingStyle = mappingStyle
        this.mappings = mappings.asImmutable()

        if (ignored) {
            this.ignored.addAll(ignored)
        }
    }

    @SuppressWarnings('UnusedMethodParameter')
    def mapRow(ResultSet rs, int rownum) {
        call rs
    }

    def call(ResultSet rs) {
        def instanceProps = [:]
        MetaClass mappedMeta = mappedType.metaClass

        if (mappingStyle == IMPLICIT) {
            def ignoredProperties = DEFAULT_IGNORED + ignored

            mappedMeta.properties.findAll { MetaProperty mp ->
                !(mp.name in ignoredProperties) && isWritable(mappedMeta, mp.name, mp.type)
            }.each { MetaProperty mp ->

                // FIXME: there is no support for actual implicit behavior!
                fixing here

                FieldMapping mapping = mappings[mp.name]
                if (mapping) {
                    applyMapping rs, mapping, instanceProps

                } else {
                    throw new IllegalArgumentException("Missing mapping for field (${mp.name}).")
                }
            }

        } else {
            // loop through mappings and map data
            mappings.each { String prop, FieldMapping mapping ->
                applyMapping rs, mapping, instanceProps
            }
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

    private static boolean isWritable(final MetaClass meta, final String name, final Class argType) {
        return meta.getMetaMethod(MetaProperty.getSetterName(name), [argType] as Object[])
    }
}
