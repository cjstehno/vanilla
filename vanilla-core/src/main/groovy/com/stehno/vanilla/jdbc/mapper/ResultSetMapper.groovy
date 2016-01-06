/*
 * Copyright (C) 2016 Christopher J. Stehno <chris@stehno.com>
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

import java.sql.ResultSet

/**
 * Defines a mapper used to map fields of a ResultSet into Java or Groovy objects.
 */
interface ResultSetMapper {

    /**
     * An alias to the <code>call(ResultSet)</code> method used to facilitate simple inter-operation with the SpringFramework RowMapper interface.
     * A <code>ResultSetMapper</code> should be usable as a <code>RowMapper</code> by simply casting it (in Groovy).
     *
     * <pre><code>
     *     RowMapper rowMapper = myResultSetMapper as RowMapper
     * </code></pre>
     *
     * @param rs the ResultSet being operated on
     * @param row the current row (not used)
     * @return the object value of the mapped row
     */
    def mapRow(ResultSet rs, int row)

    /**
     * Executes the mapper on the current row of the provided <code>ResultSet</code>.
     *
     * @param rs the ResultSet being operated on
     * @return the mapped data extracted from the ResultSet row
     */
    def call(ResultSet rs)
}
