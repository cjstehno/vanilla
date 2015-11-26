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
package com.stehno.vanilla.jdbc.mapper

interface FieldMapping {

    String getPropertyName()

    Object getExtractor()

    Object getConverter()

    FieldMapping from(nameOrPosition)

    FieldMapping fromObject(nameOrPosition)

    FieldMapping fromString(nameOrPosition)

    FieldMapping fromBoolean(nameOrPosition)

    FieldMapping fromByte(nameOrPosition)

    FieldMapping fromShort(nameOrPosition)

    FieldMapping fromInt(nameOrPosition)

    FieldMapping fromLong(nameOrPosition)

    FieldMapping fromFloat(nameOrPosition)

    FieldMapping fromDouble(nameOrPosition)

    FieldMapping fromBytes(nameOrPosition)

    FieldMapping fromDate(nameOrPosition)

    FieldMapping fromTime(nameOrPosition)

    FieldMapping fromTimestamp(nameOrPosition)

    FieldMapping fromAsciiStream(nameOrPosition)

    FieldMapping fromUnicodeStream(nameOrPosition)

    FieldMapping fromBinaryStream(nameOrPosition)

    FieldMapping fromCharacterStream(nameOrPosition)

    FieldMapping fromBigDecimal(nameOrPosition)

    FieldMapping fromRef(nameOrPosition)

    FieldMapping fromBlob(nameOrPosition)

    FieldMapping fromClob(nameOrPosition)

    FieldMapping fromArray(nameOrPosition)

    FieldMapping fromURL(nameOrPosition)

    void using(converter)
}


