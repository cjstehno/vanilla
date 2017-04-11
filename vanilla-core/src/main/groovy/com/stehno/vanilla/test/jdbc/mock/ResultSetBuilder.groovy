/*
 * Copyright (C) 2017 Christopher J. Stehno <chris@stehno.com>
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
package com.stehno.vanilla.test.jdbc.mock

import com.mockrunner.mock.jdbc.MockResultSet

import java.sql.ResultSet

import static com.stehno.vanilla.Affirmations.affirm
import static com.stehno.vanilla.util.Strings.underscoreToCamelCase
import static groovy.lang.Closure.DELEGATE_FIRST

/**
 * Builder used to simplify the creation of MockResultSet objects using the builder pattern or the provided ResultSet
 * DSL.
 */
class ResultSetBuilder implements ResultSetDsl {

    private final List<String> columns = []
    private final List<Object[]> rows = []

    /**
     * Creates a new <code>ResultSetBuilder</code> based on the provided DSL closure.
     *
     * @param closure the DSL closure
     * @return the configured ResultSetBuilder
     */
    static ResultSetBuilder builder(@DelegatesTo(value = ResultSetDsl, strategy = DELEGATE_FIRST) Closure closure) {
        ResultSetBuilder factory = new ResultSetBuilder()
        closure.delegate = factory
        closure.resolveStrategy = DELEGATE_FIRST
        closure.call()
        factory
    }

    /**
     * Creates a new <code>ResultSet</code> based on the provided DSL closure. The underlying implementation is an configured instance of
     * <code>com.mockrunner.mock.jdbc.MockResultSet</code>.
     *
     * @param closure the DSL closure
     * @return the configured ResultSet
     */
    static ResultSet resultSet(@DelegatesTo(value = ResultSetDsl, strategy = DELEGATE_FIRST) Closure closure) {
        builder(closure).build()
    }

    @SuppressWarnings('ConfusingMethodName')
    void columns(List<String> colNames) {
        columns.addAll(colNames)
    }

    @SuppressWarnings('ConfusingMethodName')
    void columns(String... colNames) {
        columns.addAll(colNames.collect())
    }

    void data(List<Object> colValues) {
        checkColumnSizes colValues.size()
        rows << colValues.toArray()
    }

    void data(Object... colValues) {
        checkColumnSizes colValues.size()
        rows << colValues
    }

    void object(Object colValueObject) {
        def row = columns.collect { col ->
            colValueObject[underscoreToCamelCase(col)]
        }
        rows << (row as Object[])
    }

    void map(Map<String, Object> colValueMap) {
        def row = columns.collect { col -> colValueMap[col] }
        rows << (row as Object[])
    }

    /**
     * Builds a <code>ResultSet</code> (implemented by <code>com.mockrunner.mock.jdbc.MockResultSet</code>) based on the provided data.
     *
     * @return a configured ResultSet
     */
    ResultSet build() {
        MockResultSet mrs = new MockResultSet("mock-${System.currentTimeMillis()}")

        columns.each { col ->
            mrs.addColumn(col)
        }

        rows.each { row ->
            mrs.addRow(row as Object[])
        }

        return mrs
    }

    private void checkColumnSizes(int argCount) {
        affirm(
            argCount == columns.size(),
            "The column counts do not match: found ${argCount}, expected ${columns.size()} - did you specify the columns?"
        )
    }
}

