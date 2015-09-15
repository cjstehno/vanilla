package com.stehno.vanilla.mapper

import groovy.transform.TypeChecked

/**
 * Provides implementations for the common methods of the ObjectMapper interface.
 */
@TypeChecked
abstract trait ObjectMapperSupport implements ObjectMapper {

    Object create(final Object src, final Class destClass) {
        def dest = destClass.newInstance()
        copy src, dest
        dest
    }

    Closure collector(final Class destClass) {
        return { o ->
            create o, destClass
        }
    }
}
