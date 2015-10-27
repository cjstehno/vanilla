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
package com.stehno.vanilla.jdbc

import com.stehno.vanilla.util.Strings

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
class FieldMapping {

    final String propertyName
    private Closure extractor
    private Closure converter

    Closure getExtractor() {
        extractor
    }

    Closure getConverter() {
        converter
    }

    FieldMapping(String propertyName) {
        this.propertyName = propertyName

        from Strings.camelCaseToUnderscore(propertyName)
    }

    FieldMapping from(nameOrPosition) {
        fromObject nameOrPosition
    }

    FieldMapping fromObject(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getObject(nameOrPosition) }
    }

    private FieldMapping extract(nameOrPosition, Closure closure) {
        affirm nameOrPosition instanceof String || nameOrPosition instanceof Integer
        extractor = closure
        this
    }

    FieldMapping fromString(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getString(nameOrPosition) }
    }

    FieldMapping fromBoolean(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getBoolean(nameOrPosition) }
    }

    FieldMapping fromByte(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getByte(nameOrPosition) }
    }

    FieldMapping fromShort(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getShort(nameOrPosition) }
    }

    FieldMapping fromInt(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getInt(nameOrPosition) }
    }

    FieldMapping fromLong(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getLong(nameOrPosition) }
    }

    FieldMapping fromFloat(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getFloat(nameOrPosition) }
    }

    FieldMapping fromDouble(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getDouble(nameOrPosition) }
    }

    FieldMapping fromBytes(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getBytes(nameOrPosition) }
    }

    FieldMapping fromDate(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getDate(nameOrPosition) }
    }

    FieldMapping fromTime(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getTime(nameOrPosition) }
    }

    FieldMapping fromTimestamp(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getTimestamp(nameOrPosition) }
    }

    FieldMapping fromAsciiStream(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getAsciiStream(nameOrPosition) }
    }

    FieldMapping fromUnicodeStream(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getUnicodeStream(nameOrPosition) }
    }

    FieldMapping fromBinaryStream(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getBinaryStream(nameOrPosition) }
    }

    FieldMapping fromCharacterStream(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getCharacterStream(nameOrPosition) }
    }

    FieldMapping fromBigDecimal(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getBigDecimal(nameOrPosition) }
    }

    FieldMapping fromRef(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getRef(nameOrPosition) }
    }

    FieldMapping fromBlob(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getBlob(nameOrPosition) }
    }

    FieldMapping fromClob(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getClob(nameOrPosition) }
    }

    FieldMapping fromArray(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getArray(nameOrPosition) }
    }

    FieldMapping fromURL(nameOrPosition) {
        extract(nameOrPosition) { ResultSet rs -> rs.getURL(nameOrPosition) }
    }

    void using(Closure closure) {
        converter = closure
    }
}
