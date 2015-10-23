/*
 * Copyright (c) 2015 Christopher J. Stehno
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

/**
 * FIXME: document me
 */
class FieldMapping {

    final String propertyName
    private Closure extractor

    Closure getExtractor() {
        extractor
    }

    FieldMapping(String propertyName) {
        this.propertyName = propertyName

        from Strings.camelCaseToUnderscore(propertyName)
    }

    FieldMapping from(final String fieldName) {
        extractor = { ResultSet rs -> rs.getObject(fieldName) }
        this
    }

    FieldMapping from(final int fieldIndex) {
        extractor = { ResultSet rs -> rs.getObject(fieldIndex) }
        this
    }

    FieldMapping fromDate(final String fieldName) {
        extractor = { ResultSet rs -> rs.getDate(fieldName) }
        this
    }

    FieldMapping fromString(final String fieldName) {
        extractor = { ResultSet rs -> rs.getString(fieldName) }
        this
    }

    /* TODO: support most if not all of these
    BigDecimal
    BinaryStream
    AsciiStream
    Array
    blob
    boolean
    byte
    bytes
    characterstream
    clob
    date
    double
    float
    int
    long
    short
    string
    time
    timestamp
    url
     */

    void using(Closure closure) {

    }
}
