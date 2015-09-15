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
 * The conversion closure may take 0-3 arguments, where the first is the value of the source property being converted,
 * the second is the instance of the source object itself, and the third is the instance of the destination object itself.
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
