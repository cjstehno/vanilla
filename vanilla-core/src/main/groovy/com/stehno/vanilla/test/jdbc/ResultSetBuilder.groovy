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
package com.stehno.vanilla.test.jdbc

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

    static ResultSetBuilder builder(@DelegatesTo(value = ResultSetDsl, strategy = DELEGATE_FIRST) Closure closure) {
        ResultSetBuilder factory = new ResultSetBuilder()
        closure.delegate = factory
        closure.resolveStrategy = DELEGATE_FIRST
        closure.call()
        factory
    }

    static ResultSet resultSet(@DelegatesTo(value = ResultSetDsl, strategy = DELEGATE_FIRST) Closure closure) {
        builder(closure).build()
    }

    void columns(List<String> colNames) {
        columns.addAll(colNames)
    }

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
     * Builds a MockResultSet based on the provided data.
     *
     * @return a configured MockResultSet
     */
    ResultSet build() {
        new MockResultSet(columns, rows)
    }

    private void checkColumnSizes(int argCount) {
        affirm(
            argCount == columns.size(),
            "The column counts do not match: found ${argCount}, expected ${columns.size()} - did you specify the columns?"
        )
    }
}

