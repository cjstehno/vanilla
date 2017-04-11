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
package com.stehno.vanilla.jdbc.mapper

import com.stehno.vanilla.jdbc.mapper.runtime.RuntimeFieldMapping
import com.stehno.vanilla.jdbc.mapper.runtime.RuntimeResultSetMapper
import groovy.transform.ToString
import groovy.transform.TypeChecked

import static com.stehno.vanilla.jdbc.mapper.MappingStyle.IMPLICIT

/**
 * Configuration model implementation of the <code>ResultSetMapperDsl</code> interface, used for configuration and creation of a
 * <code>ResultSetMapper</code>.
 */
@TypeChecked @ToString(includeFields = true, includeNames = true)
class ResultSetMapperBuilder implements ResultSetMapperDslSupport {

    /**
     * The type of object being mapped.
     */
    final Class mappedType

    /**
     * The mapping style being used.
     */
    final MappingStyle style

    /**
     * Creates a new mapper builder for the given mapped type and mapping style.
     *
     * @param mappedType the type of object being mapped
     * @param style the mapping style to be used (defaults to IMPLICIT if not specified)
     */
    ResultSetMapperBuilder(final Class mappedType, final MappingStyle style = IMPLICIT) {
        this.mappedType = mappedType
        this.style = style
    }

    /**
     * Creates a <code>RuntimeResultSetMapper</code> based on the configured mappings.
     *
     * @return a configured ResultSetMapper
     */
    ResultSetMapper build() {
        if (style == IMPLICIT) {
            applyImpliedMappings()
        }

        new RuntimeResultSetMapper(mappedType, mappings, ignoredNames)
    }

    @Override
    FieldMapping createFieldMapping(final String propertyName) {
        new RuntimeFieldMapping(propertyName)
    }

    private void applyImpliedMappings() {
        MetaClass mappedMeta = mappedType.metaClass
        def ignoredProperties = ['class'] + ignoredNames

        mappedMeta.properties.findAll { MetaProperty mp -> isWritable(mappedMeta, mp.name, mp.type) }
            .findAll { MetaProperty mp -> !(mp.name in ignoredProperties) }
            .findAll { MetaProperty mp -> !mappings.containsKey(mp.name) }
            .each { MetaProperty mp -> map mp.name }
    }

    private static boolean isWritable(final MetaClass meta, final String name, final Class argType) {
        return meta.getMetaMethod(MetaProperty.getSetterName(name), [argType] as Object[])
    }
}
