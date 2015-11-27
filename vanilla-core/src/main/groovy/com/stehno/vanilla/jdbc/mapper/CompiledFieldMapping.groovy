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

import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression

import static com.stehno.vanilla.util.Strings.camelCaseToUnderscore
import static org.codehaus.groovy.ast.tools.GeneralUtils.*

/**
 * FieldMapping implementation used by compiled version of the ResultSet Mapper DSL.
 *
 * This class is used in AST operations and should not be used externally.
 */
class CompiledFieldMapping implements FieldMapping {

    /**
     * The name of the object property being mapped.
     */
    final String propertyName

    private static final String RS = 'rs'

    private Expression extractor
    private Expression converter

    /**
     * Creates a property mapping for the specified property.
     *
     * @param propertyName the name of the mapped property
     */
    protected CompiledFieldMapping(String propertyName) {
        this.propertyName = propertyName

        from constX(camelCaseToUnderscore(propertyName))
    }

    @Override
    Object getExtractor() {
        extractor
    }

    @Override
    Object getConverter() {
        converter
    }

    FieldMapping from(nameOrPosition) {
        fromObject nameOrPosition
    }

    FieldMapping fromObject(nameOrPosition) {
        extract(nameOrPosition, 'getObject')
    }

    FieldMapping fromString(nameOrPosition) {
        extract nameOrPosition, 'getString'
    }

    FieldMapping fromBoolean(nameOrPosition) {
        extract nameOrPosition, 'getBoolean'
    }

    FieldMapping fromByte(nameOrPosition) {
        extract nameOrPosition, 'getByte'
    }

    FieldMapping fromShort(nameOrPosition) {
        extract nameOrPosition, 'getShort'
    }

    FieldMapping fromInt(nameOrPosition) {
        extract nameOrPosition, 'getInt'
    }

    FieldMapping fromLong(nameOrPosition) {
        extract nameOrPosition, 'getLong'
    }

    FieldMapping fromFloat(nameOrPosition) {
        extract nameOrPosition, 'getFloat'
    }

    FieldMapping fromDouble(nameOrPosition) {
        extract nameOrPosition, 'getDouble'
    }

    FieldMapping fromBytes(nameOrPosition) {
        extract nameOrPosition, 'getBytes'
    }

    FieldMapping fromDate(nameOrPosition) {
        extract nameOrPosition, 'getDate'
    }

    FieldMapping fromTime(nameOrPosition) {
        extract nameOrPosition, 'getTime'
    }

    FieldMapping fromTimestamp(nameOrPosition) {
        extract nameOrPosition, 'getTimestamp'
    }

    FieldMapping fromAsciiStream(nameOrPosition) {
        extract nameOrPosition, 'getAsciiStream'
    }

    FieldMapping fromUnicodeStream(nameOrPosition) {
        extract nameOrPosition, 'getUnicodeStream'
    }

    FieldMapping fromBinaryStream(nameOrPosition) {
        extract nameOrPosition, 'getBinaryStream'
    }

    FieldMapping fromCharacterStream(nameOrPosition) {
        extract nameOrPosition, 'getCharacterStream'
    }

    FieldMapping fromBigDecimal(nameOrPosition) {
        extract nameOrPosition, 'getBigDecimal'
    }

    FieldMapping fromRef(nameOrPosition) {
        extract nameOrPosition, 'getRef'
    }

    FieldMapping fromBlob(nameOrPosition) {
        extract nameOrPosition, 'getBlob'
    }

    FieldMapping fromClob(nameOrPosition) {
        extract nameOrPosition, 'getClob'
    }

    FieldMapping fromArray(nameOrPosition) {
        extract nameOrPosition, 'getArray'
    }

    FieldMapping fromURL(nameOrPosition) {
        extract nameOrPosition, 'getURL'
    }

    private FieldMapping extract(nameOrPosition, String getterName) {
        extractor = callX(varX(RS), getterName, args(nameOrPosition as ConstantExpression))
        this
    }

    @Override
    void using(converter) {
        this.converter = converter
    }
}
