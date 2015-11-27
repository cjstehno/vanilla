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
import com.stehno.vanilla.util.Strings
import groovy.transform.ToString

import java.sql.ResultSet

import static com.stehno.vanilla.Affirmations.affirm

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
class RuntimeFieldMapping implements FieldMapping {

    final String propertyName
    private Closure extractor
    private Closure converter

    RuntimeFieldMapping(final String propertyName) {
        this.propertyName = propertyName

        from Strings.camelCaseToUnderscore(propertyName)
    }

    Object getExtractor() {
        extractor
    }

    Object getConverter() {
        converter
    }

    @Override
    FieldMapping from(nameOrPosition) {
        fromObject nameOrPosition
    }

    @Override
    FieldMapping fromObject(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getObject(nameOrPosition) }
    }

    @Override
    FieldMapping fromString(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getString(nameOrPosition) }
    }

    @Override
    FieldMapping fromBoolean(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getBoolean(nameOrPosition) }
    }

    @Override
    FieldMapping fromByte(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getByte(nameOrPosition) }
    }

    @Override
    FieldMapping fromShort(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getShort(nameOrPosition) }
    }

    @Override
    FieldMapping fromInt(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getInt(nameOrPosition) }
    }

    @Override
    FieldMapping fromLong(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getLong(nameOrPosition) }
    }

    @Override
    FieldMapping fromFloat(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getFloat(nameOrPosition) }
    }

    @Override
    FieldMapping fromDouble(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getDouble(nameOrPosition) }
    }

    @Override
    FieldMapping fromBytes(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getBytes(nameOrPosition) }
    }

    @Override
    FieldMapping fromDate(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getDate(nameOrPosition) }
    }

    @Override
    FieldMapping fromTime(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getTime(nameOrPosition) }
    }

    @Override
    FieldMapping fromTimestamp(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getTimestamp(nameOrPosition) }
    }

    @Override
    FieldMapping fromAsciiStream(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getAsciiStream(nameOrPosition) }
    }

    @Override
    FieldMapping fromUnicodeStream(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getUnicodeStream(nameOrPosition) }
    }

    @Override
    FieldMapping fromBinaryStream(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getBinaryStream(nameOrPosition) }
    }

    @Override
    FieldMapping fromCharacterStream(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getCharacterStream(nameOrPosition) }
    }

    @Override
    FieldMapping fromBigDecimal(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getBigDecimal(nameOrPosition) }
    }

    @Override
    FieldMapping fromRef(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getRef(nameOrPosition) }
    }

    @Override
    FieldMapping fromBlob(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getBlob(nameOrPosition) }
    }

    @Override
    FieldMapping fromClob(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getClob(nameOrPosition) }
    }

    @Override
    FieldMapping fromArray(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getArray(nameOrPosition) }
    }

    @Override
    FieldMapping fromURL(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getURL(nameOrPosition) }
    }

    @Override
    void using(closure) {
        affirm closure instanceof Closure, 'Only Closures are supported as field converters.'
        converter = closure
    }

    protected FieldMapping extract2(final nameOrPosition, final String getterName) {
        affirm nameOrPosition instanceof String || nameOrPosition instanceof Integer
        extractor = { ResultSet rs -> rs."$getterName"(nameOrPosition) }
        this
    }

    protected FieldMapping extract(nameOrPosition, Closure closure) {
        affirm nameOrPosition instanceof String || nameOrPosition instanceof Integer
        extractor = closure
        this
    }
}
