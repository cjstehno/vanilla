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

import java.sql.*

/**
 * Created by cjstehno on 10/4/15.
 */
class ResultSetFactory {

    // FIXME: pull out DSL interface to hide other methods

    private final List<String> columns = []
    private final List<Object[]> rows = []

    static ResultSetFactory factory(@DelegatesTo(ResultSetFactory) Closure closure){
        ResultSetFactory factory = new ResultSetFactory()
        closure.delegate = factory
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        factory
    }

    static ResultSet resultSet(@DelegatesTo(ResultSetFactory) Closure closure) {
        factory(closure).build()
    }

    void columns(String... colNames) {
        columns.addAll(colNames.collect())
    }

    void row(Object... colValues) {
        assert colValues.size() != columns.size(), 'The column counts do not match.'
        rows << colValues
    }

    void object(Object colValueObject) {
        def row = []
        columns.each { col->
            row << ( colValueObject[ col] )
        }
        rows << (row as Object[])
    }

    void map(Map<String, Object> colValueMap) {
        def row = []
        columns.each { col->
            row << ( colValueMap[col] )
        }
        rows << (row as Object[])
    }

    ResultSet build(){

    }
}

