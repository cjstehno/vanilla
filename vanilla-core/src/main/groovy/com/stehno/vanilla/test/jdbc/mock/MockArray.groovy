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
package com.stehno.vanilla.test.jdbc.mock

import groovy.transform.Canonical

import java.sql.Array
import java.sql.ResultSet
import java.sql.SQLException

/**
 * FIXME: document me
 */
@Canonical
class MockArray implements Array, DataObject {

    String baseTypeName
    int baseType
    Object array

    @Override
    long length() {
        array.size()
    }

    @Override
    Object getArray(Map<String, Class<?>> map) throws SQLException {
        checkFree()
        array
    }

    @Override
    Object getArray(long pos, int count) throws SQLException {
        checkFree()

        int index = positionIndex(pos)

        array[index..(index + count - 1)]
    }

    @Override
    Object getArray(long index, int count, Map<String, Class<?>> map) throws SQLException {
        getArray(index, count)
    }

    @Override
    ResultSet getResultSet() throws SQLException {
        new MockResultSet(numberedCols(length()), [array])
    }

    @Override
    ResultSet getResultSet(Map<String, Class<?>> map) throws SQLException {
        resultSet
    }

    @Override
    ResultSet getResultSet(long pos, int count) throws SQLException {
        int index = positionIndex(pos)
        new MockResultSet(numberedCols(count), [array[index..(index + count - 1)]])
    }

    @Override
    ResultSet getResultSet(long index, int count, Map<String, Class<?>> map) throws SQLException {
        getResultSet(index, count)
    }

    private static List<String> numberedCols(count) {
        return (1..count).collect { c -> c as String }
    }
}
