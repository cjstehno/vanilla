/*
 * Copyright (C) 2016 Christopher J. Stehno <chris@stehno.com>
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
package com.stehno.vanilla.jdbc.mapper.runtime

import com.stehno.vanilla.jdbc.mapper.FieldMapping
import com.stehno.vanilla.jdbc.mapper.ResultSetMapper
import groovy.transform.ToString

import java.sql.ResultSet

import static com.stehno.vanilla.Affirmations.affirm
import static com.stehno.vanilla.util.Strings.camelCaseToUnderscore

/**
 * Dynamic runtime implementation of the FieldMapping abstraction. The "from" methods accept a database field name (String) or position index (int).
 */
@ToString(includeNames = true, includeFields = true)
class RuntimeFieldMapping extends FieldMapping {

    /**
     * Creates a new field mapping for the given object property.
     *
     * @param propertyName the object property name
     */
    RuntimeFieldMapping(final String propertyName) {
        super(propertyName)

        from camelCaseToUnderscore(propertyName)
    }

    @Override
    void using(closure) {
        affirm closure instanceof Closure, 'Only Closures are supported as field converters.'
        super.using(closure)
    }

    @Override
    protected FieldMapping extract(final nameOrPosition, final String getterName) {
        affirm nameOrPosition instanceof String || nameOrPosition instanceof GString || nameOrPosition instanceof Integer

        def arg
        if (nameOrPosition instanceof String || nameOrPosition instanceof GString) {
            arg = nameOrPosition as String
        } else {
            arg = nameOrPosition as int
        }

        extractor = { ResultSet rs, String prefix ->
            if (nameOrPosition instanceof String) {
                rs."$getterName"(prefix ? "${prefix}$arg" : arg)
            } else {
                rs."$getterName"(arg)
            }
        }

        this
    }

    @Override
    protected FieldMapping extract(mapper) {
        affirm mapper instanceof ResultSetMapper

        extractor = {ResultSet rs, String prefix->
            mapper.call(rs)
        }

        this
    }
}
