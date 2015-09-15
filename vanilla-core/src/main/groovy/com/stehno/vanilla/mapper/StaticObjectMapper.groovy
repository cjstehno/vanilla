package com.stehno.vanilla.mapper

/**
 * Static (compile-time) base class for ObjectMappers created using the @Mapper annotation (and transformation).
 * Generally, this class should not be only used internally.
 */
abstract class StaticObjectMapper implements ObjectMapper {

    // TODO: should this just be the base class for mappers?

    @Override
    Object create(final Object src, final Class destClass) {
        def dest = destClass.newInstance()
        copy src, dest
        dest
    }

    @Override
    Closure collector(final Class destClass) {
        return { o ->
            create o, destClass
        }
    }
}
