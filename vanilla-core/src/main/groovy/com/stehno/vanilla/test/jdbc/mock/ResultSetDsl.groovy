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

/**
 * Defines the operations available to the ResultSet DSL - used to define MockResultSet configurations.
 */
interface ResultSetDsl {

    /**
     * Provides the column names used in the ResultSet. This must be called before any rows are added to the result set.
     *
     * @param colNames the column names
     */
    void columns(String... colNames)

    /**
     * Provides the column names used in the ResultSet. This must be called before any rows are added to the result set.
     *
     * @param colNames the column names
     */
    void columns(List<String> colNames)

    /**
     * Provides a row of data to the result set, specified by the given objects in the same order as the column definition.
     *
     * @param colValues the column values
     */
    void data(Object... colValues)

    /**
     * Provides a row of data to the result set, specified by the given objects in the same order as the column definition.
     *
     * @param colValues the column values
     */
    void data(List<Object> colValues)

    /**
     * Provides a row of data to the result set by extracting the properties whose names match the column names (converted
     * from underscore-style to camel-case.
     *
     * @param objValues the object containing the row values
     */
    void object(Object objValues)

    /**
     * Provides a row of data to the result set by extracting the map values whose names match the column names (converted
     * from underscore-style to camel-case.
     *
     * @param mapValues the map containing the row values
     */
    void map(Map<String, Object> mapValues)
}
