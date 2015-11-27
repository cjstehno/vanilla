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
package com.stehno.vanilla.jdbc.mapper.runtime

import com.stehno.vanilla.jdbc.mapper.FieldMapping
import groovy.transform.ToString

import java.sql.ResultSet

import static com.stehno.vanilla.Affirmations.affirm
import static com.stehno.vanilla.util.Strings.camelCaseToUnderscore

/**
 * FIXME: document me
 *
 * from methods map to the get methods of ResultSet
 * from, fromObject --> getObject
 * fromString --> getString
 * fromDate --> getDate
 * etc (field name or position is valid)
 */
@ToString(includeNames = true, includeFields = true)
class RuntimeFieldMapping extends FieldMapping {

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
        affirm nameOrPosition instanceof String || nameOrPosition instanceof Integer
        extractor = { ResultSet rs -> rs."$getterName"(nameOrPosition) }
        this
    }
}
