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

import groovy.transform.TypeChecked

import java.sql.ResultSet

/**
 * FIXME: document me
 */
@TypeChecked
class RuntimeResultSetMapper implements ResultSetMapper {

    private static final Collection<String> DEFAULT_IGNORED = ['class'].asImmutable()
    private final ResultSetMapperBuilder builder

    RuntimeResultSetMapper(ResultSetMapperBuilder builder) {
        this.builder = builder
    }

    @SuppressWarnings('UnusedMethodParameter')
    def mapRow(ResultSet rs, int rownum) {
        call rs
    }

    def call(ResultSet rs) {
        def instanceProps = [:]
        MetaClass mappedMeta = builder.mappedType.metaClass

        if (builder.style == MappingStyle.IMPLICIT) {
            def ignored = DEFAULT_IGNORED + builder.ignored()

            mappedMeta.properties.findAll { MetaProperty mp ->
                !(mp.name in ignored) && isWritable(mappedMeta, mp.name, mp.type)
            }.each { MetaProperty mp ->
                FieldMapping mapping = builder.findMapping(mp.name)
                if (mapping) {
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

                    instanceProps[mp.name] = mappedValue

                } else {
                    throw new IllegalArgumentException("Missing mapping for field (${mp.name}).")
                }
            }

        } else {
            // loop through mappings and map data
            builder.mappings().each { FieldMapping mapping ->
                def value = (mapping.extractor as Closure).call(rs)
                Closure converter = mapping.converter as Closure
                if (converter) {
                    int argCount = converter.maximumNumberOfParameters
                    if (argCount > 0) {
                        value = converter.call(value)
                    } else {
                        value = converter.call()
                    }
                }

                instanceProps[mapping.propertyName] = value
            }
        }

        builder.mappedType.newInstance(instanceProps)
    }

    private static boolean isWritable(MetaClass meta, String name, Class argType) {
        return meta.getMetaMethod(MetaProperty.getSetterName(name), [argType] as Object[])
    }
}
