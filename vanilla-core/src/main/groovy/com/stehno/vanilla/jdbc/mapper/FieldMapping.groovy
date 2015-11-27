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

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as an Object.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping from(columnIdentifier)

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as an Object.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromObject(columnIdentifier)

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a String.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromString(columnIdentifier)

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a Boolean.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromBoolean(columnIdentifier)

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a byte.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromByte(columnIdentifier)

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a short.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromShort(columnIdentifier)

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as an int.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromInt(columnIdentifier)

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a long.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromLong(columnIdentifier)

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a float.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromFloat(columnIdentifier)

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a double.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromDouble(columnIdentifier)

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as an array of bytes.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromBytes(columnIdentifier)

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a SQL Date.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromDate(columnIdentifier)

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a Time instance.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromTime(columnIdentifier)

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a Timestamp instance.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromTimestamp(columnIdentifier)

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as an ASCII stream.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromAsciiStream(columnIdentifier)

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a Unicode stream.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromUnicodeStream(columnIdentifier)

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a binary stream.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromBinaryStream(columnIdentifier)

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a character stream.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromCharacterStream(columnIdentifier)

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a BigDecimal.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromBigDecimal(columnIdentifier)

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a Ref.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromRef(columnIdentifier)

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a Blob.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromBlob(columnIdentifier)

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a Clob.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromClob(columnIdentifier)

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as an Array.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromArray(columnIdentifier)

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a URL.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromURL(columnIdentifier)

    /**
     * DSL method used to apply a converter object to the field mapping.
     *
     * @param converter the converter object to be used
     */
    void using(converter)
}
