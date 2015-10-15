package com.stehno.vanilla.jdbc

/**
 * FIXME: document me
 */
enum MappingStyle {
    /**
     * Mapping is implied - All properties of the bean are mapped and must be "ignored" to skip.
     */
    IMPLICIT,

    /**
     * Mapping is explicit - only mentioned properties mapped from bean.
     */
    EXPLICIT
}
