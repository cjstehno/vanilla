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
import spock.lang.Specification

import java.sql.ResultSet

import static ResultSetBuilder.resultSet

class ResultSetFactorySpec extends Specification {

    def 'usage'() {
        when:
        ResultSet rs = resultSet {
            columns 'a', 'b', 'c'
            row 10, 20, 30
            map([a: 11, b: 21, c: 31])
            object new Abc(a: 12, b: 22, c: 32)
        }

        then:
        rs
    }
}

@Canonical
class Abc {

    Object a
    Object b
    Object c
}
