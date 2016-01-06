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
package com.stehno.vanilla.test.jdbc.mock

import spock.lang.Specification

import java.sql.ResultSetMetaData

import static com.stehno.vanilla.test.PropertyRandomizer.randomize
import static com.stehno.vanilla.test.jdbc.mock.ResultSetBuilder.resultSet

class ResultSetBuilderSpec extends Specification {

    def 'usage'() {
        setup:
        def items = randomize(String) * 8

        when:
        def rs = resultSet {
            columns 'a', 'b'
            data(items[0], items[1])
            data([items[2], items[3]])
            map(a: items[4], b: items[5])
            object(new FourPartObject(items[6], items[7]))
        }

        then:
        ResultSetMetaData rsmd = rs.metaData
        rsmd.columnCount == 2
        rsmd.getColumnName(1) == 'a'
        rsmd.getColumnName(2) == 'b'

        rs.rowCount == 4
    }

    def 'usage: no cols'() {
        setup:
        def items = randomize(String) * 8

        when:
        resultSet {
            data(items[0], items[1])
        }

        then:
        thrown(IllegalArgumentException)
    }
}