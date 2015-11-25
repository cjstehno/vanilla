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
import groovy.transform.ToString
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression

import static com.stehno.vanilla.Affirmations.affirm
import static com.stehno.vanilla.jdbc.MappingStyle.IMPLICIT
import static org.codehaus.groovy.ast.tools.GeneralUtils.*

/**
 * FIXME: document me
 */
@ToString(includeFields = true, includeNames = true)
class ResultSetMapperBuilder implements ResultSetMapperDsl {

    final Class mappedType
    final MappingStyle style
    private final Collection<String> ignoredNames = []
    protected final Map<String, FieldMapping> mappings = [:]

    ResultSetMapperBuilder(final Class mappedType, final MappingStyle style) {
        this.mappedType = mappedType
        this.style = style
    }

    static ResultSetMapper mapper(Class mappedType, MappingStyle style = IMPLICIT, @DelegatesTo(ResultSetMapperDsl) Closure closure) {
        ResultSetMapperBuilder builder = new ResultSetMapperBuilder(mappedType, style)

        closure.delegate = builder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()

        builder.build()
    }

    FieldMapping findMapping(String propertyName) {
        mappings[propertyName]
    }

    Collection<String> ignored() {
        ignoredNames.asImmutable()
    }

    boolean isIgnored(String propertyName) {
        propertyName in ignoredNames
    }

    Collection<FieldMapping> mappings() {
        mappings.values().asImmutable()
    }

    ResultSetMapper build() {
        new DynamicResultSetMapper(this)
    }

    FieldMapping map(String propertyName) {
        FieldMapping mapping = new FieldMapping(propertyName)
        mappings[propertyName] = mapping
        mapping
    }

    void ignore(String... propertyNames) {
        this.ignoredNames.addAll(propertyNames)
    }
}

class CompiledResultSetMapperBuilder extends ResultSetMapperBuilder {

    final ClassNode mappedTypeNode

    CompiledResultSetMapperBuilder(ClassNode mappedTypeNode, MappingStyle style) {
        super(mappedTypeNode.typeClass, style)
        this.mappedTypeNode = mappedTypeNode
    }

    @Override
    FieldMapping map(String propertyName) {
        CompiledFieldMapping mapping = new CompiledFieldMapping(propertyName)
        mappings[propertyName] = mapping
        mapping
    }
}

class CompiledFieldMapping extends FieldMapping {

    CompiledFieldMapping(String propertyName) {
        super(propertyName, false)

        from constX(Strings.camelCaseToUnderscore(propertyName))
    }

    protected FieldMapping extract(nameOrPosition, Closure closure) {
        affirm nameOrPosition instanceof ConstantExpression
        extractor = closure
        this
    }

    FieldMapping from(nameOrPosition) {
        fromObject nameOrPosition
    }

    FieldMapping fromObject(nameOrPosition) {
        extract(nameOrPosition) { callResultSetGetter('getObject', nameOrPosition as ConstantExpression) }
    }

    private static MethodCallExpression callResultSetGetter(final String getterName, final ConstantExpression argX) {
        return callX(varX('rs'), getterName, args(argX))
    }

    FieldMapping fromString(nameOrPosition) {
        extract(nameOrPosition) { callResultSetGetter('getString', nameOrPosition as ConstantExpression) }
    }

    FieldMapping fromBoolean(nameOrPosition) {
        extract(nameOrPosition) { callResultSetGetter('getBoolean', nameOrPosition as ConstantExpression) }
    }

    FieldMapping fromByte(nameOrPosition) {
        extract(nameOrPosition) { callResultSetGetter('getByte', nameOrPosition as ConstantExpression) }
    }

    FieldMapping fromShort(nameOrPosition) {
        extract(nameOrPosition) { callResultSetGetter('getShort', nameOrPosition as ConstantExpression) }
    }

    FieldMapping fromInt(nameOrPosition) {
        extract(nameOrPosition) { callResultSetGetter('getInt', nameOrPosition as ConstantExpression) }
    }

    FieldMapping fromLong(nameOrPosition) {
        extract(nameOrPosition) { callResultSetGetter('getLong', nameOrPosition as ConstantExpression) }
    }

    FieldMapping fromFloat(nameOrPosition) {
        extract(nameOrPosition) { callResultSetGetter('getFloat', nameOrPosition as ConstantExpression) }
    }

    FieldMapping fromDouble(nameOrPosition) {
        extract(nameOrPosition) { callResultSetGetter('getDouble', nameOrPosition as ConstantExpression) }
    }

    FieldMapping fromBytes(nameOrPosition) {
        extract(nameOrPosition) { callResultSetGetter('getBytes', nameOrPosition as ConstantExpression) }
    }

    FieldMapping fromDate(nameOrPosition) {
        extract(nameOrPosition) { callResultSetGetter('getDate', nameOrPosition as ConstantExpression) }
    }

    FieldMapping fromTime(nameOrPosition) {
        extract(nameOrPosition) { callResultSetGetter('getTime', nameOrPosition as ConstantExpression) }
    }

    FieldMapping fromTimestamp(nameOrPosition) {
        extract(nameOrPosition) { callResultSetGetter('getTimestamp', nameOrPosition as ConstantExpression) }
    }

    FieldMapping fromAsciiStream(nameOrPosition) {
        extract(nameOrPosition) { callResultSetGetter('getAsciiStream', nameOrPosition as ConstantExpression) }
    }

    FieldMapping fromUnicodeStream(nameOrPosition) {
        extract(nameOrPosition) { callResultSetGetter('getUnicodeStream', nameOrPosition as ConstantExpression) }
    }

    FieldMapping fromBinaryStream(nameOrPosition) {
        extract(nameOrPosition) { callResultSetGetter('getBinaryStream', nameOrPosition as ConstantExpression) }
    }

    FieldMapping fromCharacterStream(nameOrPosition) {
        extract(nameOrPosition) { callResultSetGetter('getCharacterStream', nameOrPosition as ConstantExpression) }
    }

    FieldMapping fromBigDecimal(nameOrPosition) {
        extract(nameOrPosition) { callResultSetGetter('getBigDecimal', nameOrPosition as ConstantExpression) }
    }

    FieldMapping fromRef(nameOrPosition) {
        extract(nameOrPosition) { callResultSetGetter('getRef', nameOrPosition as ConstantExpression) }
    }

    FieldMapping fromBlob(nameOrPosition) {
        extract(nameOrPosition) { callResultSetGetter('getBlob', nameOrPosition as ConstantExpression) }
    }

    FieldMapping fromClob(nameOrPosition) {
        extract(nameOrPosition) { callResultSetGetter('getClob', nameOrPosition as ConstantExpression) }
    }

    FieldMapping fromArray(nameOrPosition) {
        extract(nameOrPosition) { callResultSetGetter('getArray', nameOrPosition as ConstantExpression) }
    }

    FieldMapping fromURL(nameOrPosition) {
        extract(nameOrPosition) { callResultSetGetter('getURL', nameOrPosition as ConstantExpression) }
    }
}