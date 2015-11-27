/*
 * Copyright (C) 2015 Christopher J. Stehno <chris@stehno.com>
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
 * ObjectMapper implementation used by the runtime ObjectMapper DSL to contain the configuration
 * and execute the mappings.
 */
@TypeChecked
class RuntimeObjectMapper extends ObjectMapperConfig implements ObjectMapperSupport {

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

    private callConverter(Closure closure, prop, src, dest) {
        if (closure.maximumNumberOfParameters == 0) {
            return closure.call()

        } else if (closure.maximumNumberOfParameters == 1) {
            return closure.call(prop)

        } else if (closure.maximumNumberOfParameters == 2) {
            return closure.call(prop, src)

        }

        return closure.call(prop, src, dest)
    }
}
