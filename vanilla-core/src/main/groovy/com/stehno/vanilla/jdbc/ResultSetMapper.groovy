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
package com.stehno.vanilla.jdbc

import groovy.transform.TypeChecked

import java.sql.ResultSet

import static com.stehno.vanilla.jdbc.MappingStyle.IMPLICIT

/**
 * FIXME: document me
 */
@TypeChecked
class ResultSetMapper {

    // FIXME: using should be for transforming or extracting
    // FIXME: having from and using should be considered

    private static final Collection<String> DEFAULT_IGNORED = ['class'].asImmutable()
    private final ResultSetMapperBuilder builder

    ResultSetMapper(ResultSetMapperBuilder builder) {
        this.builder = builder
    }

    // FIXME: document and test this "as RowMapper" - similar for ResultSetExtractor?
    def mapRow(ResultSet rs, int rownum){
        call rs
    }

    def call(ResultSet rs) {
        def instanceProps = [:]
        MetaClass mappedMeta = builder.mappedType.metaClass

        if (builder.style == IMPLICIT) {
            def ignored = DEFAULT_IGNORED + builder.ignored()

            mappedMeta.properties.findAll { MetaProperty mp ->
                !(mp.name in ignored) && isWritable(mappedMeta, mp.name, mp.type)
            }.each { MetaProperty mp ->
                FieldMapping mapping = builder.findMapping(mp.name)
                if (mapping) {
                    instanceProps[mp.name] = mapping.extractor.call(rs)
                } else {
                    throw new IllegalArgumentException("Missing mapping for field (${mp.name}).")
                }
            }

        } else {
            // loop through mappings and map data
            builder.mappings().each { FieldMapping mapping ->
                instanceProps[mapping.propertyName] = mapping.extractor.call(rs)
            }
        }

        builder.mappedType.newInstance(instanceProps)
    }

    private static boolean isWritable(MetaClass meta, String name, Class argType) {
        return meta.getMetaMethod(MetaProperty.getSetterName(name), [argType] as Object[])
    }
}
