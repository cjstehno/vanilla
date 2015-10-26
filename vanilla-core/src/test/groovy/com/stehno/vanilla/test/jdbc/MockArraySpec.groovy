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

import spock.lang.Specification

import java.sql.Types

class MockArraySpec extends Specification {

    private static final String[] ARRAY = ['a', 'b', 'c', 'd', 'e'] as String[]
    private MockArray array = new MockArray('test', Types.VARCHAR, ARRAY)

    def 'length'() {
        when:
        long len = array.length()

        then:
        len == 5
    }

    def 'getArray(Map)'() {
        when:
        def result = array.getArray([alpha: 'bravo'])

        then:
        result == ARRAY
    }

    def 'getArray(long, int)'() {
        when:
        def result = array.getArray(2, 3)

        then:
        result == ['b', 'c', 'd']
    }

    def 'getArray(long, int, Map)'() {
        when:
        def result = array.getArray(3, 2, [one: String])

        then:
        result == ['c', 'd']
    }

    def 'getResultSet()'() {
        when:
        def rs = array.resultSet

        then:
        rs.next()
        rs.getObject(1) == 'a'
        rs.getObject('2') == 'b'
        rs.getObject(3) == 'c'
        rs.getObject('4') == 'd'
        rs.getObject(5) == 'e'
    }

    def 'getResultSet(Map)'() {
        when:
        def rs = array.getResultSet([foo:Date])

        then:
        rs.next()
        rs.getObject(1) == 'a'
        rs.getObject('2') == 'b'
        rs.getObject(3) == 'c'
        rs.getObject('4') == 'd'
        rs.getObject(5) == 'e'
    }

    def 'getResultSet(long, int)'() {
        when:
        def rs = array.getResultSet(2, 3)

        then:
        rs.next()
        rs.getObject('1') == 'b'
        rs.getObject(2) == 'c'
        rs.getObject('3') == 'd'
    }

    def 'getResultSet(long, int, Map)'() {
        when:
        def rs = array.getResultSet(2, 3, [blah:Long])

        then:
        rs.next()
        rs.getObject('1') == 'b'
        rs.getObject(2) == 'c'
        rs.getObject('3') == 'd'
    }
}
