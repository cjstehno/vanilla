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

import groovy.transform.ToString

/**
 * ResultSet mapper DSL model class representation of an object property mapping to a field in the database.
 */
@ToString(includeNames = true, includeFields = true)
abstract class FieldMapping {

    /**
     * The name of the mapped object property.
     */
    final String propertyName

    private Object extractor
    private Object converter

    protected FieldMapping(String propertyName) {
        this.propertyName = propertyName
    }

    /**
     * Retrieves the object used to extract the property value from the database result set.
     *
     * @return the extraction object
     */
    Object getExtractor() { extractor }

    /**
     * Retrieves the object used (if any) to convert the database field value into the desired value required by the mapped property.
     *
     * @return the converter object
     */
    Object getConverter() { converter }

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as an Object.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping from(columnIdentifier) {
        fromObject columnIdentifier
    }

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as an Object.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromObject(columnIdentifier) {
        extract columnIdentifier, 'getObject'
    }

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a String.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromString(columnIdentifier) {
        extract columnIdentifier, 'getString'
    }

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a Boolean.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromBoolean(columnIdentifier) {
        extract columnIdentifier, 'getBoolean'
    }

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a byte.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromByte(columnIdentifier) {
        extract columnIdentifier, 'getByte'
    }

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a short.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromShort(columnIdentifier) {
        extract columnIdentifier, 'getShort'
    }

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as an int.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromInt(columnIdentifier) {
        extract columnIdentifier, 'getInt'
    }

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a long.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromLong(columnIdentifier) {
        extract columnIdentifier, 'getLong'
    }

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a float.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromFloat(columnIdentifier) {
        extract columnIdentifier, 'getFloat'
    }

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a double.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromDouble(columnIdentifier) {
        extract columnIdentifier, 'getDouble'
    }

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as an array of bytes.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromBytes(columnIdentifier) {
        extract columnIdentifier, 'getBytes'
    }

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a SQL Date.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromDate(columnIdentifier) {
        extract columnIdentifier, 'getDate'
    }

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a Time instance.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromTime(columnIdentifier) {
        extract columnIdentifier, 'getTime'
    }

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a Timestamp instance.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromTimestamp(columnIdentifier) {
        extract columnIdentifier, 'getTimestamp'
    }

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as an ASCII stream.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromAsciiStream(columnIdentifier) {
        extract columnIdentifier, 'getAsciiStream'
    }

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a Unicode stream.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromUnicodeStream(columnIdentifier) {
        extract columnIdentifier, 'getUnicodeStream'
    }

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a binary stream.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromBinaryStream(columnIdentifier) {
        extract columnIdentifier, 'getBinaryStream'
    }

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a character stream.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromCharacterStream(columnIdentifier) {
        extract columnIdentifier, 'getCharacterStream'
    }

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a BigDecimal.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromBigDecimal(columnIdentifier) {
        extract columnIdentifier, 'getBigDecimal'
    }

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a Ref.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromRef(columnIdentifier) {
        extract columnIdentifier, 'getRef'
    }

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a Blob.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromBlob(columnIdentifier) {
        extract columnIdentifier, 'getBlob'
    }

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a Clob.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromClob(columnIdentifier) {
        extract columnIdentifier, 'getClob'
    }

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as an Array.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromArray(columnIdentifier) {
        extract columnIdentifier, 'getArray'
    }

    /**
     * Configures the FieldMapping to extract the database field value with the specified identifier as a URL.
     *
     * @param columnIdentifier the column identifier
     * @return a reference to this FieldMapping object
     */
    FieldMapping fromURL(columnIdentifier) {
        extract columnIdentifier, 'getURL'
    }

    /**
     * DSL method used to apply a converter object to the field mapping.
     *
     * @param converter the converter object to be used
     */
    void using(converter) {
        this.converter = converter
    }

    protected abstract FieldMapping extract(nameOrPosition, String getterName)

    protected void setExtractor(Object extractor) {
        this.extractor = extractor
    }
}
