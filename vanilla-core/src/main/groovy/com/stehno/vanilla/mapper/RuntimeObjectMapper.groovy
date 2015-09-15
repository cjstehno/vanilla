package com.stehno.vanilla.mapper

import groovy.transform.TypeChecked

/**
 * FIXME: document me
 */
@TypeChecked
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
            if (!pmc.converter) {
                dest[pmc.destinationName] = src[pmc.sourceName]

            } else if (pmc.converter instanceof ObjectMapper) {
                def instance = dest.metaClass.getMetaProperty(pmc.destinationName).type.newInstance()
                (pmc.converter as ObjectMapper).copy(src[pmc.sourceName], instance)
                dest[pmc.destinationName] = instance

            } else if (pmc.converter instanceof Closure) {
                dest[pmc.destinationName] = callConverter(pmc.converter as Closure, src[pmc.sourceName], src, dest)

            } else {
                throw new UnsupportedOperationException("Converter type (${pmc.converter.class}) is not supported.")
            }
        }
    }

    @Override
    Object create(final Object src, final Class destClass) {
        def dest = destClass.newInstance()
        copy src, dest
        dest
    }

    @Override
    Closure collector(final Class destClass) {
        return { o->
            create o, destClass
        }
    }

    private callConverter(Closure closure, prop, src, dest) {
        if (closure.maximumNumberOfParameters == 0) {
            return closure.call()

        } else if (closure.maximumNumberOfParameters == 1) {
            return closure.call(prop)

        } else if (closure.maximumNumberOfParameters == 2) {
            return closure.call(prop, src)

        } else {
            return closure.call(prop, src, dest)
        }
    }
}
