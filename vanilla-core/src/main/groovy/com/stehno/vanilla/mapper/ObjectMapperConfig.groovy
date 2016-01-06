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
package com.stehno.vanilla.mapper

import groovy.transform.TypeChecked

/**
 * Provides a DSL for creating mappers to copy the contents of one object into another.
 *
 * The mappings are meant to be explicit; only properties described in the mapping closure will be mapped, others will be ignored. The supported
 * mappings are as follows:
 *
 * <ul>
 *     <li>"map 'foo'" - maps the property named "foo" from the source object to a property named "foo" in the destination object.</li>
 *     <li>"map 'foo' into 'bar'" - maps the property named "foo" from the source object to a property named "bar" in the destination object.</li>
 *     <li>"map 'foo' using { x-> x * 3 }" - maps the property named "foo" from the source object to a property named "foo" in the destination object
 *          where the source property value is transformed by the provided closure when passed into the destination property.</li>
 *     <li>"map 'foo' into 'bar' using { x-> x * 3 }" - maps the property named "foo" from the source object to a property named "bar" in the
 *          destination object where the source property value is transformed by the provided closure when passed into the destination property.</li>
 * </ul>
 *
 * The conversion closure may take 0-3 arguments, where the first is the value of the source property being converted, the second is the instance of
 * the source object itself, and the third is the instance of the destination object itself.
 */
@TypeChecked
class ObjectMapperConfig implements ObjectMapperDsl {

    private final Map<String, PropertyMapping> mappings = [:]

    @SuppressWarnings('ConfusingMethodName')
    Collection<PropertyMapping> mappings() {
        mappings.values().asImmutable()
    }

    /**
     * Configures a mapping for the given source object property.
     *
     * @param propertyName the name of the source object property.
     * @return the PropertyMapping instance
     */
    PropertyMapping map(final String propertyName) {
        def propertyMapping = new PropertyMappingConfig(propertyName)
        mappings[propertyName] = propertyMapping
        propertyMapping
    }
}
