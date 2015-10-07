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

package com.stehno.vanilla.jdbc

import java.sql.ResultSet

import static com.stehno.vanilla.util.Strings.camelCaseToUnderscore
import static groovy.lang.MetaProperty.getSetterName

/**
 * Created by cjstehno on 10/1/15.
 */
class ResultSetMapperFactory {

    static ResultSetMapper dsl(Class mappedType, MappingStyle style = MappingStyle.IMPLICIT, @DelegatesTo(JdbcMapperConfig) Closure closure) {
        JdbcMapperConfig mapperConfig = new JdbcMapperConfig(style)

        closure.delegate = mapperConfig
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()

        new ResultSetMapper(mappedType, mapperConfig)
    }
}

enum MappingStyle {
    // all properties mapped from bean, must be ignored to skip
    IMPLICIT,
    // only mentioned properties mapped from bean
        EXPLICIT
}

class ResultSetMapper {

    // FIXME: would be nice to support immutable mapped objects

    private final Class mappedType
    private final JdbcMapperConfig config

    ResultSetMapper(Class mappedType, JdbcMapperConfig config) {
        this.mappedType = mappedType
        this.config = config
    }

    // FIXME: using should be for transforming or extracting
    // FIXME: having from and using should be considered

    def call(ResultSet rs) {
        def instance = mappedType.newInstance()

        if (config.style == MappingStyle.IMPLICIT) {
            def ignored = ['class'] + config.ignored()

            MetaClass mappedMeta = mappedType.metaClass

            mappedMeta.properties.findAll { MetaProperty mp ->
                !(mp.name in ignored) && mappedMeta.getMetaMethod(getSetterName(mp.name), [mp.type] as Object[])
            }.each { MetaProperty mp ->
                println "mapping: ${mp.name}"

                FieldMapping mapping = config.findMapping(mp.name)
                if (mapping) {
                    instance[mp.name] = mapping.extractor(rs)
                } else {
                    throw new IllegalArgumentException("Missing mapping for field (${mp.name}).")
                }
            }

        } else {
            // loop through mappings and map data
            config.mappings().each { FieldMapping mapping ->
                instance[mapping.propertyName] = mapping.extractor(rs)
            }
        }

        instance
    }
}

class JdbcMapperConfig {

    final MappingStyle style
    private final Collection<String> ignoredNames = []
    private final Map<String, FieldMapping> mappings = [:]

    FieldMapping findMapping(String propertyName) {
        mappings[propertyName]
    }

    Collection<String> ignored() {
        ignoredNames.asImmutable()
    }

    boolean isIgnored(String propertyName) {
        propertyName in ignoredNames
    }

    Collection<FieldMapping> mappings() {
        mappings.values().asImmutable()
    }

    JdbcMapperConfig(MappingStyle style) {
        this.style = style
    }

    FieldMapping map(String propertyName) {
        FieldMapping mapping = new FieldMapping(propertyName)
        mappings[propertyName] = mapping
        mapping
    }

    def ignore(String... propertyNames) {
        this.ignoredNames.addAll(propertyNames)
    }
}

class FieldMapping {

    final String propertyName
    private Closure extractor

    Closure getExtractor() {
        extractor
    }

    FieldMapping(String propertyName) {
        this.propertyName = propertyName

        from camelCaseToUnderscore(propertyName)
    }

    FieldMapping from(final String fieldName) {
        extractor = { ResultSet rs -> rs.getObject(fieldName) }
        this
    }

    FieldMapping from(final int fieldIndex) {
        extractor = { ResultSet rs -> rs.getObject(fieldIndex) }
        this
    }

    FieldMapping fromDate(final String fieldName) {
        extractor = { ResultSet rs -> rs.getDate(fieldName) }
        this
    }

    FieldMapping fromString(final String fieldName) {
        extractor = { ResultSet rs -> rs.getString(fieldName) }
        this
    }

    /* TODO: support most if not all of these
    BigDecimal
    BinaryStream
    AsciiStream
    Array
    blob
    boolean
    byte
    bytes
    characterstream
    clob
    date
    double
    float
    int
    long
    short
    string
    time
    timestamp
    url
     */

    void using(Closure closure) {

    }
}