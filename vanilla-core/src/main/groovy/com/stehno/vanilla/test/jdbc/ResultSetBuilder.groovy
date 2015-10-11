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

package com.stehno.vanilla.test.jdbc

import java.sql.ResultSet

import static com.stehno.vanilla.util.Strings.underscoreToCamelCase
import static groovy.lang.Closure.DELEGATE_FIRST

/**
 * Created by cjstehno on 10/4/15.
 */
class ResultSetBuilder implements ResultSetDsl {

    private final List<String> columns = []
    private final List<Object[]> rows = []

    static ResultSetBuilder factory(@DelegatesTo(value = ResultSetDsl, strategy = DELEGATE_FIRST) Closure closure) {
        ResultSetBuilder factory = new ResultSetBuilder()
        closure.delegate = factory
        closure.resolveStrategy = DELEGATE_FIRST
        closure.call()
        factory
    }

    static ResultSet resultSet(@DelegatesTo(value = ResultSetDsl, strategy = DELEGATE_FIRST) Closure closure) {
        factory(closure).build()
    }

    void columns(String... colNames) {
        columns.addAll(colNames.collect())
    }

    void row(Object... colValues) {
        assert colValues.size() == columns.size(), "The column counts do not match."
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

    ResultSet build() {
        new MockResultSet(columns, rows)
    }
}

interface ResultSetDsl {
    void columns(String... colNames)

    void row(Object... colValues)

    void object(Object objValues)

    void map(Map<String, Object> mapValues)
}