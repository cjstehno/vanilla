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
import static com.stehno.vanilla.test.PropertyRandomizer.randomize
import static com.stehno.vanilla.test.Randomizers.forByteArray

class ResultSetBuilderSpec extends Specification {

    def 'getString'() {
        setup:
        def strings = randomize(String) * 12

        when:
        def rs = buildFourColumn(strings)

        then:
        rs.size() == 3

        rs.size().times {
            assert rs.next()
            assertRow strings, rs.getString(1), rs.getString('b'), rs.getString(3), rs.getString('d')
        }

        !rs.next()
    }

    def 'updateString'(){
        setup:
        def strings = randomize(String) * 2

        when:
        def rs = resultSet {
            columns 'a','b'
            data strings[0], strings[1]
        }

        rs.next()

        rs.updateString(1, 'alpha')
        rs.updateString('b', 'bravo')

        then:
        rs.getString('a') == 'alpha'
        rs.getString(2) == 'bravo'
    }

    def 'getBoolean'() {
        setup:
        def items = randomize(Boolean) * 6

        when:
        def rs = buildTwoColumn(items)

        then:
        rs.size() == 3

        rs.size().times {
            assert rs.next()
            assertRow items, rs.getBoolean(1), rs.getBoolean('b')
        }

        !rs.next()
    }

    def 'updateBoolean'(){
        setup:
        when:
        def rs = resultSet {
            columns 'a','b'
            data true, false
        }

        rs.next()

        rs.updateBoolean(1, false)
        rs.updateBoolean('b', true)

        then:
        !rs.getBoolean('a')
        rs.getBoolean(2)
    }

    def 'getByte'() {
        setup:
        def items = randomize(Byte) * 6

        when:
        def rs = buildTwoColumn(items)

        then:
        rs.size() == 3

        rs.size().times {
            assert rs.next()
            assertRow items, rs.getByte(1), rs.getByte('b')
        }

        !rs.next()
    }

    def 'getBytes'() {
        setup:
        Class c = ([] as byte[]).class

        def items = randomize(c) {
            typeRandomizer c, forByteArray()
        } * 6

        when:
        def rs = buildTwoColumn(items)

        then:
        rs.size() == 3

        rs.size().times {
            assert rs.next()
            assertRow items, rs.getBytes(1), rs.getBytes('b')
        }

        !rs.next()
    }

    def 'getShort'() {
        setup:
        def items = randomize(Short) * 6

        when:
        def rs = buildTwoColumn(items)

        then:
        rs.size() == 3

        rs.size().times {
            assert rs.next()
            assertRow items, rs.getShort(1), rs.getShort('b')
        }

        !rs.next()
    }

    def 'getLong'() {
        setup:
        def items = randomize(Long) * 6

        when:
        def rs = buildTwoColumn(items)

        then:
        rs.size() == 3

        rs.size().times {
            assert rs.next()
            assertRow items, rs.getLong(1), rs.getLong('b')
        }

        !rs.next()
    }

    def 'getFloat'() {
        setup:
        def items = randomize(Float) * 6

        when:
        def rs = buildTwoColumn(items)

        then:
        rs.size() == 3

        rs.size().times {
            assert rs.next()
            assertRow items, rs.getFloat(1), rs.getFloat('b')
        }

        !rs.next()
    }

    def 'getDouble'() {
        setup:
        def items = randomize(Double) * 6

        when:
        def rs = buildTwoColumn(items)

        then:
        rs.size() == 3

        rs.size().times {
            assert rs.next()
            assertRow items, rs.getDouble(1), rs.getDouble('b')
        }

        !rs.next()
    }

    def 'getInt'() {
        setup:
        def items = randomize(Integer) * 6

        when:
        def rs = buildTwoColumn(items)

        then:
        rs.size() == 3

        rs.size().times {
            assert rs.next()
            assertRow items, rs.getInt(1), rs.getInt('b')
        }

        !rs.next()
    }

    def 'updateInt'(){
        setup:
        def items = randomize(Integer) * 4

        when:
        def rs = resultSet {
            columns 'a','b'
            data items[0], items[1]
        }

        rs.next()

        rs.updateInt(1, items[2])
        rs.updateInt('b', items[3])

        then:
        rs.getInt('a') == items[2]
        rs.getInt(2) == items[3]
    }

    def 'getDate'() {
        setup:
        def items = randomize(java.util.Date) * 12
        def cal = Calendar.getInstance()

        when:
        def rs = buildFourColumn(items)

        then:
        rs.size() == 3

        rs.size().times {
            assert rs.next()
            assertRow items, rs.getDate(1), rs.getDate('b'), rs.getDate(3, cal), rs.getDate('d', cal)
        }

        !rs.next()
    }

    private void assertRow(final List expecteds, final Object... actuals) {
        assert actuals.every { value ->
            value == expecteds.remove(0)
        }
    }

    private ResultSet buildTwoColumn(items) {
        resultSet {
            columns 'a', 'b'
            data(items[0], items[1])
            map(a: items[2], b: items[3])
            object(new FourPartObject(items[4], items[5]))
        }
    }

    private ResultSet buildFourColumn(items) {
        resultSet {
            columns 'a', 'b', 'c', 'd'
            data(*items[0..3])
            map(a: items[4], b: items[5], c: items[6], d: items[7])
            object(new FourPartObject(items[8], items[9], items[10], items[11]))
        }
    }
}

// FIXME: pull out and collect some common test POJO objects
@Canonical
class FourPartObject {

    Object a
    Object b
    Object c
    Object d
}
