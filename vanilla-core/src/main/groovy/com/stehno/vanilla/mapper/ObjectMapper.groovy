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

import groovy.transform.TypeChecked

interface ObjectMapper {

    /**
     * Performs the mapping/copy operation from the source object to the destination object.
     *
     * @param src the source object instance
     * @param dest the destination object instance
     */
    void copy(final Object src, final Object dest)
}

class RuntimeObjectMapper extends ObjectMapperConfig implements ObjectMapper {

    /**
     * Creates an ObjectMapper configured by the given Closure.
     *
     * @param closure the configuration closure
     * @return the configured ObjectMapper
     */
    static ObjectMapper mapper(@DelegatesTo(ObjectMapperConfig) final Closure closure) {
        RuntimeObjectMapper mapper = new RuntimeObjectMapper()
        closure.setDelegate(mapper)
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        mapper
    }

    @Override
    void copy(final Object src, final Object dest) {
        mappings().each { PropertyMappingConfig pmc ->
            if (pmc.nestedMapper) {
                def instance = dest.metaClass.getMetaProperty(pmc.destinationName).type.newInstance()
                pmc.nestedMapper.copy(src[pmc.sourceName], instance)
                dest[pmc.destinationName] = instance

            } else {
                dest[pmc.destinationName] = pmc.converter ? pmc.converter.call(src[pmc.sourceName]) : src[pmc.sourceName]
            }
        }
    }
}

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
class ObjectMapperConfig {

    private final List<PropertyMappingConfig> mappings = []

    Collection<PropertyMappingConfig> mappings() {
        mappings.asImmutable()
    }

    /**
     * Configures a mapping for the given source object property.
     *
     * @param propertyName the name of the source object property.
     * @return the PropertyMapping instance
     */
    PropertyMappingConfig map(final String propertyName) {
        def propertyMapping = new PropertyMappingConfig(propertyName)
        mappings << propertyMapping
        propertyMapping
    }
}

/**
 * The DSL object representation of a property mapping.
 */
@TypeChecked
class PropertyMappingConfig {

    final String sourceName
    private String destinationName
    private Closure converter
    private ObjectMapper nestedMapper

    PropertyMappingConfig(final String sourceName) {
        this.sourceName = sourceName
    }

    String getDestinationName() {
        destinationName ?: sourceName
    }

    Closure getConverter() {
        converter
    }

    ObjectMapper getNestedMapper() {
        nestedMapper
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
}
