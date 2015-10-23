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
package com.stehno.vanilla.mapper

import groovy.transform.ToString
import groovy.transform.TypeChecked

/**
 * The DSL object representation of a property mapping.
 */
@TypeChecked @ToString(includeNames = true, includeFields = true)
class PropertyMappingConfig {

    final String sourceName
    private String destinationName
    private Object converter

    PropertyMappingConfig(final String sourceName) {
        this.sourceName = sourceName
    }

    String getDestinationName() {
        destinationName ?: sourceName
    }

    Object getConverter() {
        converter
    }

    /**
     * Maps the source property into the destination property with the provided name.
     *
     * @param propertyName the destination property name
     * @return this PropertyMapping instance
     */
    PropertyMappingConfig into(final String propertyName) {
        destinationName = propertyName
        this
    }

    /**
     * Applies the provided converter when copying the source property to the destination property.
     *
     * @param converter the converter
     */
    void using(final Object converter) {
        this.converter = converter
    }
}
