package com.stehno.vanilla.mapper

import groovy.transform.TypeChecked

/**
 * Static (compile-time) base class for ObjectMappers created using the @Mapper annotation (and transformation).
 * Generally, this class should be only used internally.
 */
@TypeChecked
abstract class StaticObjectMapper implements ObjectMapperSupport {
    // TODO: this should be able to go away if I can figure out how to apply traits to ClassNodes
}

