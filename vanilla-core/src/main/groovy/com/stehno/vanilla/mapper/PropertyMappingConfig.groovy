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
