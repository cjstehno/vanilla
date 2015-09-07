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

package com.stehno.vanilla.mapper

import groovy.transform.Canonical
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
 */
@TypeChecked
class ObjectMapper {

    private final List<PropertyMapping> mappings = []

    /**
     * Creates an ObjectMapper configured by the given Closure.
     *
     * @param closure the configuration closure
     * @return the configured ObjectMapper
     */
    static ObjectMapper mapper(@DelegatesTo(ObjectMapper) final Closure closure) {
        ObjectMapper mapper = new ObjectMapper()
        closure.setDelegate(mapper)
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        mapper
    }

    /**
     * Configures a mapping for the given source object property.
     *
     * @param propertyName the name of the source object property.
     * @return the PropertyMapping instance
     */
    PropertyMapping map(final String propertyName) {
        def propertyMapping = new PropertyMapping(propertyName)
        mappings << propertyMapping
        propertyMapping
    }

    /**
     * Performs the mapping/copy operation from the source object to the destination object.
     *
     * @param src the source object instance
     * @param dest the destination object instance
     */
    void copy(final Object src, final Object dest) {
        mappings*.copy(src, dest)
    }
}

/**
 * The DSL object representation of a property mapping.
 */
@Canonical @TypeChecked
class PropertyMapping {

    private String sourceName
    private String destinationName
    private Closure converter
    private ObjectMapper nestedMapper

    PropertyMapping(final String sourceName) {
        this.sourceName = sourceName
    }

    /**
     * Maps the source property into the destination property with the provided name.
     *
     * @param propertyName the destination property name
     * @return this PropertyMapping instance
     */
    PropertyMapping into(final String propertyName) {
        destinationName = propertyName
        this
    }

    /**
     * Applies the provided closure when copying the source property to the destination property. The closure will have the source property
     * value passed into it as an argument.
     *
     * @param converter the conversion closure
     */
    void using(final Closure converter) {
        this.converter = converter
    }

    /**
     * Applies the provided ObjectMapper when copying the source property into the destination property. In order to use this method, the destination
     * property must be of a type with a default empty constructor - an instance of the object will be created to be populated by the nested mapper.
     *
     * @param mapper the mapper to be used as a property converter
     */
    void using(final ObjectMapper mapper) {
        this.nestedMapper = mapper
    }

    /**
     * Used to perform the copy operation for this property mapping.
     *
     * @param src the source object
     * @param dest the destination object
     */
    void copy(final Object src, final Object dest) {
        String destName = destinationName ?: sourceName

        if (nestedMapper) {
            def instance = dest.metaClass.getMetaProperty(destName).type.newInstance()

            nestedMapper.copy(
                src[sourceName],
                instance
            )

            dest[destName] = instance

        } else {
            dest[destName] = converter ? converter.call(src[sourceName]) : src[sourceName]
        }
    }
}
