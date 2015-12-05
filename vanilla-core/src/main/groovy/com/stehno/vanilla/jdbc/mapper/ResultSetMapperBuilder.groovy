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
class ResultSetMapperBuilder implements ResultSetMapperDsl {

    /**
     * The type of object being mapped.
     */
    final Class mappedType

    /**
     * The mapping style being used.
     */
    final MappingStyle style

    private final Collection<String> ignoredNames = []
    private final Map<String, FieldMapping> mappings = [:]

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
     * Used to find the field mapping for the specified object property.
     *
     * @param propertyName the name of the object property
     * @return the FieldMapping for the specified property, or null
     */
    FieldMapping findMapping(String propertyName) {
        mappings[propertyName]
    }

    /**
     * Retrieves a collection containing the names of all ignored object properties.
     *
     * @return a Collection of ignored object property names
     */
    Collection<String> ignored() {
        ignoredNames.asImmutable()
    }

    /**
     * Retrieves a collection containing all the configured mappings.
     *
     * @return a Collection of the field mappings
     */
    @SuppressWarnings('ConfusingMethodName')
    Collection<FieldMapping> mappings() {
        mappings.values().asImmutable()
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

    /**
     * Maps the specified object property name and encapsulates it in a <code>FieldMapping</code>. The <code>FieldMapping</code> object is stored
     * internally and a reference is returned.
     *
     * @param propertyName the object property being mapped
     * @return a reference to the created FieldMapping object
     */
    FieldMapping map(String propertyName) {
        FieldMapping mapping = createMapping(propertyName)
        mappings[propertyName] = mapping
        mapping
    }

    protected FieldMapping createMapping(final String propertyName) {
        new RuntimeFieldMapping(propertyName)
    }

    /**
     * Configures the specified object properties to be ignored by the mapping.
     *
     * @param propertyNames one or more object property names to be ignored
     */
    void ignore(String... propertyNames) {
        this.ignoredNames.addAll(propertyNames)
    }

    private void applyImpliedMappings() {
        MetaClass mappedMeta = mappedType.metaClass
        def ignoredProperties = ['class'] + ignoredNames

        mappedMeta.properties
            .findAll { MetaProperty mp -> isWritable(mappedMeta, mp.name, mp.type) }
            .findAll { MetaProperty mp -> !(mp.name in ignoredProperties) }
            .findAll { MetaProperty mp -> !mappings.containsKey(mp.name) }
            .each { MetaProperty mp -> map mp.name }
    }

    private static boolean isWritable(final MetaClass meta, final String name, final Class argType) {
        return meta.getMetaMethod(MetaProperty.getSetterName(name), [argType] as Object[])
    }
}
