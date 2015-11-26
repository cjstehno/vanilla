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

import com.stehno.vanilla.Affirmations
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression

import static com.stehno.vanilla.util.Strings.camelCaseToUnderscore
import static org.codehaus.groovy.ast.tools.GeneralUtils.*

/**
 * Created by cjstehno on 11/26/15.
 */
class CompiledFieldMapping implements FieldMapping {

    final String propertyName
    private Expression extractor
    private Expression converter

    CompiledFieldMapping(String propertyName) {
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
        extract(nameOrPosition, callResultSetGetter('getObject', nameOrPosition as ConstantExpression))
    }

    FieldMapping fromString(nameOrPosition) {
        extract nameOrPosition, callResultSetGetter('getString', nameOrPosition as ConstantExpression)
    }

    FieldMapping fromBoolean(nameOrPosition) {
        extract nameOrPosition, callResultSetGetter('getBoolean', nameOrPosition as ConstantExpression)
    }

    FieldMapping fromByte(nameOrPosition) {
        extract nameOrPosition, callResultSetGetter('getByte', nameOrPosition as ConstantExpression)
    }

    FieldMapping fromShort(nameOrPosition) {
        extract nameOrPosition, callResultSetGetter('getShort', nameOrPosition as ConstantExpression)
    }

    FieldMapping fromInt(nameOrPosition) {
        extract nameOrPosition, callResultSetGetter('getInt', nameOrPosition as ConstantExpression)
    }

    FieldMapping fromLong(nameOrPosition) {
        extract nameOrPosition, callResultSetGetter('getLong', nameOrPosition as ConstantExpression)
    }

    FieldMapping fromFloat(nameOrPosition) {
        extract nameOrPosition, callResultSetGetter('getFloat', nameOrPosition as ConstantExpression)
    }

    FieldMapping fromDouble(nameOrPosition) {
        extract nameOrPosition, callResultSetGetter('getDouble', nameOrPosition as ConstantExpression)
    }

    FieldMapping fromBytes(nameOrPosition) {
        extract nameOrPosition, callResultSetGetter('getBytes', nameOrPosition as ConstantExpression)
    }

    FieldMapping fromDate(nameOrPosition) {
        extract nameOrPosition, callResultSetGetter('getDate', nameOrPosition as ConstantExpression)
    }

    FieldMapping fromTime(nameOrPosition) {
        extract nameOrPosition, callResultSetGetter('getTime', nameOrPosition as ConstantExpression)
    }

    FieldMapping fromTimestamp(nameOrPosition) {
        extract nameOrPosition, callResultSetGetter('getTimestamp', nameOrPosition as ConstantExpression)
    }

    FieldMapping fromAsciiStream(nameOrPosition) {
        extract nameOrPosition, callResultSetGetter('getAsciiStream', nameOrPosition as ConstantExpression)
    }

    FieldMapping fromUnicodeStream(nameOrPosition) {
        extract nameOrPosition, callResultSetGetter('getUnicodeStream', nameOrPosition as ConstantExpression)
    }

    FieldMapping fromBinaryStream(nameOrPosition) {
        extract nameOrPosition, callResultSetGetter('getBinaryStream', nameOrPosition as ConstantExpression)
    }

    FieldMapping fromCharacterStream(nameOrPosition) {
        extract nameOrPosition, callResultSetGetter('getCharacterStream', nameOrPosition as ConstantExpression)
    }

    FieldMapping fromBigDecimal(nameOrPosition) {
        extract nameOrPosition, callResultSetGetter('getBigDecimal', nameOrPosition as ConstantExpression)
    }

    FieldMapping fromRef(nameOrPosition) {
        extract nameOrPosition, callResultSetGetter('getRef', nameOrPosition as ConstantExpression)
    }

    FieldMapping fromBlob(nameOrPosition) {
        extract nameOrPosition, callResultSetGetter('getBlob', nameOrPosition as ConstantExpression)
    }

    FieldMapping fromClob(nameOrPosition) {
        extract nameOrPosition, callResultSetGetter('getClob', nameOrPosition as ConstantExpression)
    }

    FieldMapping fromArray(nameOrPosition) {
        extract nameOrPosition, callResultSetGetter('getArray', nameOrPosition as ConstantExpression)
    }

    FieldMapping fromURL(nameOrPosition) {
        extract nameOrPosition, callResultSetGetter('getURL', nameOrPosition as ConstantExpression)
    }

    @Override
    void using(converter) {
        this.converter = converter
    }

    // FIXME: this should be collapsed more now that I dont have a closure
    protected FieldMapping extract(nameOrPosition, Expression expression) {
        Affirmations.affirm nameOrPosition instanceof ConstantExpression
        extractor = expression
        this
    }

    private static MethodCallExpression callResultSetGetter(final String getterName, final ConstantExpression argX) {
        return callX(varX('rs'), getterName, args(argX))
    }
}
