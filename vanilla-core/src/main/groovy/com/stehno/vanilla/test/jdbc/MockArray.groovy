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

import groovy.transform.Canonical

import java.sql.Array
import java.sql.ResultSet
import java.sql.SQLException

/**
 * FIXME: document me
 */
@Canonical
class MockArray implements Array {

    String baseTypeName
    int baseType
    Object array

    @Override
    Object getArray(Map<String, Class<?>> map) throws SQLException {
        return null
    }

    @Override
    Object getArray(long index, int count) throws SQLException {
        return null
    }

    @Override
    Object getArray(long index, int count, Map<String, Class<?>> map) throws SQLException {
        return null
    }

    @Override
    ResultSet getResultSet() throws SQLException {
        int len  = java.lang.reflect.Array.getLength(array)

        new MockResultSet(
            ,
            []
        )
    }

    @Override
    ResultSet getResultSet(Map<String, Class<?>> map) throws SQLException {
        return null
    }

    @Override
    ResultSet getResultSet(long index, int count) throws SQLException {
        return null
    }

    @Override
    ResultSet getResultSet(long index, int count, Map<String, Class<?>> map) throws SQLException {
        return null
    }

    @Override
    void free() throws SQLException {

    }
}
