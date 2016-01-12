/*
 * Copyright (C) 2016 Christopher J. Stehno <chris@stehno.com>
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
/**
 * Defines the methods available to the ResultSetMapper DSL.
 */
interface ResultSetMapperDsl {

    /**
     * Maps an object property and encapsulates it in a FieldMapping object for further configuration.
     *
     * @param propertyName the name of the object property
     * @return the populated FieldMapping object
     */
    FieldMapping map(String propertyName)

    /**
     * Specifies one or more object properties to be ignored by the mapper. This operation is only meaningful when using the IMPLICIT style of
     * mapping.
     *
     * @param propertyNames one or more object property names to be ignored during mapping
     */
    void ignore(String... propertyNames)
}

abstract trait ResultSetMapperDslSupport implements ResultSetMapperDsl {

    final Collection<String> ignoredNames = []
    final Map<String, FieldMapping> mappings = [:]

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
     * Retrieves a collection containing all the configured mappings, with any ignored property mappings removed.
     *
     * @return a Collection of the field mappings
     */
    @SuppressWarnings('ConfusingMethodName')
    Collection<FieldMapping> mappings() {
        mappings.findAll { String fname, FieldMapping fm ->
            !(fname in ignored())
        }.values().asImmutable()
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
     * Maps the specified object property name and encapsulates it in a <code>FieldMapping</code>. The <code>FieldMapping</code> object is stored
     * internally and a reference is returned.
     *
     * @param propertyName the object property being mapped
     * @return a reference to the created FieldMapping object
     */
    FieldMapping map(String propertyName) {
        FieldMapping mapping = createFieldMapping(propertyName)
        mappings[propertyName] = mapping
        mapping
    }

    abstract FieldMapping createFieldMapping(final String propertyName)

    /**
     * Configures the specified object properties to be ignored by the mapping.
     *
     * @param propertyNames one or more object property names to be ignored
     */
    void ignore(String... propertyNames) {
        this.ignoredNames.addAll(propertyNames)
    }
}