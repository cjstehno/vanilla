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
package com.stehno.vanilla.mapper.runtime

import com.stehno.vanilla.mapper.*
import groovy.transform.TypeChecked

import static com.stehno.vanilla.mapper.ObjectMapperConfig.MappingStyle.IMPLICIT

/**
 * Runtime implementation of ObjectMapper used by the runtime ObjectMapper DSL to contain the configuration and execute the mappings.
 */
@TypeChecked
class RuntimeObjectMapper extends ObjectMapperConfig implements ObjectMapperSupport {

    private MappingStyle mappingStyle

    RuntimeObjectMapper(MappingStyle mappingStyle) {
        this.mappingStyle = mappingStyle
    }

    /**
     * Creates an ObjectMapper configured by the given Closure.
     *
     * @param mappingStyle the optional style of mapping to be used (defaults to IMPLICIT)
     * @param closure the configuration closure
     * @return the configured ObjectMapper
     */
    static ObjectMapper mapper(MappingStyle mappingStyle = IMPLICIT, @DelegatesTo(ObjectMapperDsl) final Closure closure) {
        RuntimeObjectMapper mapper = new RuntimeObjectMapper(mappingStyle)
        closure.setDelegate(mapper)
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        mapper
    }

    @Override
    void copy(final Object src, final Object dest) {
        MetaClass sourceMeta = src.metaClass

        if (mappingStyle == IMPLICIT) {
            //            def ignoredProperties = DEFAULT_IGNORED + ignored()
            //
            //            sourceMeta.properties.findAll { MetaProperty mp ->
            //                !(mp.name in ignoredProperties) && isReadable(sourceMeta, mp.name, mp.type)
            //            }.each { MetaProperty mp ->
            //                PropertyMapping mapping = findMapping(mp.name)
            //                /*
            //                    copy all 1:1 by default unless overrideen by ignore or a mapping
            //                    FIXME: make sure implicit pattern works right in other mapper
            //                 */
            //
            //            }

        } else {
            mappings().each { PropertyMapping pmc ->
                applyMapping pmc, src, dest
            }
        }
    }

    private static void applyMapping(final PropertyMapping mapping, final Object src, final Object dest) {
        if (!mapping.converter) {
            dest[mapping.destinationName] = src[mapping.sourceName]

        } else if (mapping.converter instanceof ObjectMapper) {
            def instance = dest.metaClass.getMetaProperty(mapping.destinationName).type.newInstance()
            (mapping.converter as ObjectMapper).copy(src[mapping.sourceName], instance)
            dest[mapping.destinationName] = instance

        } else if (mapping.converter instanceof Closure) {
            dest[mapping.destinationName] = callConverter(mapping.converter as Closure, src[mapping.sourceName], src, dest)

        } else {
            throw new UnsupportedOperationException("Converter type (${mapping.converter.class}) is not supported.")
        }
    }

    private static boolean isReadable(final MetaClass meta, final String name, final Class argType) {
        return meta.getMetaMethod(MetaProperty.getSetterName(name), [argType] as Object[])
    }

    private static callConverter(Closure closure, prop, src, dest) {
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
