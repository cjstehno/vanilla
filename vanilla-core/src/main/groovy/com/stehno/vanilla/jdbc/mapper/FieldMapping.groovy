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

/**
 * ResultSet mapper DSL model class representation of an object property mapping to a field in the database.
 */
interface FieldMapping {

    /**
     * Retrieves the name of the property being mapped.
     *
     * @return the name of the property being mapped
     */
    String getPropertyName()

    /**
     * Retrieves the object used to extract the property value from the database result set.
     *
     * @return the extraction object
     */
    Object getExtractor()

    /**
     * Retrieves the object used (if any) to convert the database field value into the desired value required by the mapped property.
     *
     * @return the converter object
     */
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

    /**
     * DSL method used to apply a converter object to the field mapping.
     *
     * @param converter the converter object to be used
     */
    void using(converter)
}
