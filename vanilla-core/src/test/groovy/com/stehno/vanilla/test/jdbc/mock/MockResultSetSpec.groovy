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

import spock.lang.Specification
import spock.lang.Unroll

import java.sql.*

import static com.stehno.vanilla.test.PropertyRandomizer.randomize
import static com.stehno.vanilla.test.Randomizers.*
import static java.lang.System.currentTimeMillis

class MockResultSetSpec extends Specification {

    private static final List COL_IDS = [1, 'b'].asImmutable()
    private static final int ROW_COUNT = 3

    def 'closed'(){
        setup:
        def rs = twoColumns(randomize(String) * 6)

        when:
        rs.close()

        then:
        rs.closed

        when:
        rs.next()

        then:
        def ex = thrown(SQLException)
        ex.message == 'ResultSet-Closed'
    }

    def 'positions:beforeFirst'() {
        setup:
        def rs = twoColumns(randomize(String) * 6)

        when:
        rs.next()
        rs.beforeFirst()

        then:
        rs.beforeFirst
        !rs.afterLast
        !rs.first
        !rs.last
        rs.row == 0
    }

    def 'positions:first'() {
        setup:
        def rs = twoColumns(randomize(String) * 6)

        when:
        rs.next()
        rs.next()
        rs.first()

        then:
        !rs.beforeFirst
        !rs.afterLast
        rs.first
        !rs.last
        rs.row == 1
    }

    def 'positions:last'() {
        setup:
        def rs = twoColumns(randomize(String) * 6)

        when:
        rs.next()
        rs.last()

        then:
        !rs.beforeFirst
        !rs.afterLast
        !rs.first
        rs.last
        rs.row == 3
    }

    def 'positions:afterLast'() {
        setup:
        def rs = twoColumns(randomize(String) * 6)

        when:
        rs.next()
        rs.afterLast()

        then:
        !rs.beforeFirst
        rs.afterLast
        !rs.first
        !rs.last
        rs.row == 4
    }

    @Unroll
    def 'positions:absolute(#position)'() {
        setup:
        def rs = twoColumns(randomize(String) * 6)

        when:
        boolean status = rs.absolute(position)

        then:
        status == result
        rs.row == expected

        where:
        position | result | expected
        -3       | false  | 0
        0        | false  | 0
        1        | true   | 1
        2        | true   | 2
        3        | true   | 3
        4        | false  | 4
    }

    def 'positions:relative'() {
        setup:
        def rs = twoColumns(randomize(String) * 6)

        when:
        boolean status = rs.relative(2)

        then:
        status
        rs.row == 2

        when:
        status = rs.relative(1)

        then:
        status
        rs.row == 3

        when:
        status = rs.relative(-2)

        then:
        status
        rs.row == 1

        when:
        status = rs.relative(4)

        then:
        !status
        rs.row == 4
    }

    def 'previous'() {
        setup:
        def rs = twoColumns(randomize(String) * 6)
        rs.absolute(4)

        when:
        boolean status = rs.previous()

        then:
        status
        rs.row == 3

        when:
        status = rs.previous()

        then:
        status
        rs.row == 2

        when:
        status = rs.previous()

        then:
        status
        rs.row == 1

        when:
        status = rs.previous()

        then:
        !status
        rs.row == 0
    }

    def 'findColumn'() {
        setup:
        def rs = twoColumns(randomize(String) * 6)

        when:
        int idxA = rs.findColumn('a')
        int idxB = rs.findColumn('b')

        then:
        idxA == 1
        idxB == 2
    }

    def 'size'() {
        setup:
        def rs = twoColumns(randomize(String) * 6)

        when:
        int size = rs.size()

        then:
        size == 3
    }

    @Unroll
    def 'extract: #method'() {
        setup:
        def items = randomize(type) * 6
        def rs = twoColumns(items)

        when:
        def rows = extractRows(rs, rs.&"$method")

        then:
        assertRows rows, items

        where:
        method         | type
        'getString'    | String
        'getNString'   | String
        'getBoolean'   | Boolean
        'getInt'       | Integer
        'getFloat'     | Float
        'getDouble'    | Double
        'getShort'     | Short
        'getByte'      | Byte
        'getLong'      | Long
        'getDate'      | java.util.Date
        'getTime'      | java.util.Date
        'getTimestamp' | java.util.Date
        'getObject'    | String
    }

    @Unroll
    def 'extract: #method (with calendar)'() {
        setup:
        def items = randomize(type) * 6
        def rs = twoColumns(items)
        def cal = Calendar.getInstance()

        when:
        def rows = extractRows(rs, (rs.&"$method").rcurry(cal))

        then:
        assertRows rows, items

        where:
        method         | type
        'getDate'      | java.util.Date
        'getTime'      | java.util.Date
        'getTimestamp' | java.util.Date
    }

    def 'extract: getURL'() {
        setup:
        def items = randomize(URL) {
            typeRandomizer URL, { rng ->
                "http://${forString(6..25).call(rng)}.com".toURL()
            }
        } * 6
        def rs = twoColumns(items)

        when:
        def rows = extractRows(rs, rs.&getURL)

        then:
        assertRows rows, items
    }

    @Unroll
    def 'unsupported: #method'() {
        setup:
        def rs = twoColumns(randomize(String) * 6)

        when:
        rs."$method"()

        then:
        thrown(UnsupportedOperationException)

        where:
        method << [
            'rowUpdated', 'rowInserted', 'rowDeleted', 'getMetaData', 'wasNull', 'updateRow', 'deleteRow', 'insertRow',
            'refreshRow', 'cancelRowUpdates', 'moveToInsertRow', 'moveToCurrentRow', 'getStatement'
        ]
    }

    @Unroll
    def 'unsupported(1-arg): #method'() {
        setup:
        def rs = twoColumns(randomize(String) * 6)

        when:
        rs."$method"(arg)

        then:
        thrown(UnsupportedOperationException)

        where:
        method       | arg
        'updateNull' | 42
        'getSQLXML' | 42

        'updateNull' | 'foo'
        'getSQLXML' | 'foo'
    }

    @Unroll
    def 'unsupported(2-arg): #method'() {
        setup:
        def rs = twoColumns(randomize(String) * 6)

        when:
        rs."$method"(arg1, arg2)

        then:
        thrown(UnsupportedOperationException)

        where:
        method                   | arg1  | arg2
        'updateBoolean'          | 42    | true
        'updateByte'             | 42    | 33 as byte
        'updateShort'            | 42    | 44 as short
        'updateInt'              | 42    | 66
        'updateLong'             | 42    | 99 as long
        'updateFloat'            | 42    | 88f
        'updateDouble'           | 42    | 3.14d
        'updateBigDecimal'       | 42    | new BigDecimal('234234.2345')
        'updateString'           | 42    | 'asdfasdf'
        'updateNString'          | 42    | 'asdfasdf'
        'updateBytes'            | 42    | 'asdfasdf'.bytes
        'updateDate'             | 42    | new Date(currentTimeMillis())
        'updateTime'             | 42    | new Time(currentTimeMillis())
        'updateTimestamp'        | 42    | new Timestamp(currentTimeMillis())
        'updateObject'           | 42    | new Object()
        'getObject'              | 42    | [:]
        'updateRef'              | 42    | {} as Ref
        'updateBlob'             | 42    | {} as Blob
        'updateClob'             | 42    | {} as Clob
        'updateNClob'            | 42    | {} as NClob
        'updateArray'            | 42    | {} as Array
        'updateRowId'            | 42    | {} as RowId
        'updateSQLXML'           | 42    | {} as SQLXML
        'updateCharacterStream'  | 42    | {} as Reader
        'updateNCharacterStream' | 42    | {} as Reader
        'updateAsciiStream'      | 42    | {} as InputStream
        'updateBinaryStream'     | 42    | {} as InputStream
        'updateBlob'             | 42    | {} as InputStream
        'updateClob'             | 42    | {} as Reader
        'updateNClob'            | 42    | {} as Reader

        'updateBoolean'          | 'foo' | true
        'updateByte'             | 'foo' | 33 as byte
        'updateShort'            | 'foo' | 44 as short
        'updateInt'              | 'foo' | 66
        'updateLong'             | 'foo' | 99 as long
        'updateFloat'            | 'foo' | 88f
        'updateDouble'           | 'foo' | 3.14d
        'updateBigDecimal'       | 'foo' | new BigDecimal('234234.2345')
        'updateString'           | 'foo' | 'asdfasdf'
        'updateNString'          | 'foo' | 'asdfasdf'
        'updateBytes'            | 'foo' | 'asdfasdf'.bytes
        'updateDate'             | 'foo' | new Date(currentTimeMillis())
        'updateTime'             | 'foo' | new Time(currentTimeMillis())
        'updateTimestamp'        | 'foo' | new Timestamp(currentTimeMillis())
        'updateObject'           | 'foo' | new Object()
        'getObject'              | 'foo' | [:]
        'updateRef'              | 'foo' | {} as Ref
        'updateBlob'             | 'foo' | {} as Blob
        'updateClob'             | 'foo' | {} as Clob
        'updateNClob'            | 'foo' | {} as NClob
        'updateArray'            | 'foo' | {} as Array
        'updateRowId'            | 'foo' | {} as RowId
        'updateSQLXML'           | 'foo' | {} as SQLXML
        'updateCharacterStream'  | 'foo' | {} as Reader
        'updateNCharacterStream' | 'foo' | {} as Reader
        'updateAsciiStream'      | 'foo' | {} as InputStream
        'updateBinaryStream'     | 'foo' | {} as InputStream
        'updateBlob'             | 'foo' | {} as InputStream
        'updateClob'             | 'foo' | {} as Reader
        'updateNClob'            | 'foo' | {} as Reader
    }

    @Unroll
    def 'unsupported(3-arg): #method'() {
        setup:
        def rs = twoColumns(randomize(String) * 6)

        when:
        rs."$method"(arg1, arg2, arg3)

        then:
        thrown(UnsupportedOperationException)

        where:
        method                   | arg1  | arg2              | arg3
        'updateAsciiStream'      | 42    | {} as InputStream | 10
        'updateAsciiStream'      | 42    | {} as InputStream | 10L
        'updateBinaryStream'     | 42    | {} as InputStream | 10
        'updateBinaryStream'     | 42    | {} as InputStream | 10L
        'updateCharacterStream'  | 42    | {} as Reader      | 10
        'updateCharacterStream'  | 42    | {} as Reader      | 10L
        'updateNCharacterStream' | 42    | {} as Reader      | 10L
        'updateObject'           | 42    | new Object()      | 10
        'updateBlob'             | 42    | {} as InputStream | 10L
        'updateClob'             | 42    | {} as Reader      | 10L
        'updateNClob'            | 42    | {} as Reader      | 10L

        'updateAsciiStream'      | 'foo' | {} as InputStream | 10
        'updateAsciiStream'      | 'foo' | {} as InputStream | 10L
        'updateBinaryStream'     | 'foo' | {} as InputStream | 10
        'updateBinaryStream'     | 'foo' | {} as InputStream | 10L
        'updateCharacterStream'  | 'foo' | {} as Reader      | 10
        'updateCharacterStream'  | 'foo' | {} as Reader      | 10L
        'updateNCharacterStream' | 'foo' | {} as Reader      | 10L
        'updateObject'           | 'foo' | new Object()      | 10
        'updateBlob'             | 'foo' | {} as InputStream | 10L
        'updateClob'             | 'foo' | {} as Reader      | 10L
        'updateNClob'            | 'foo' | {} as Reader      | 10L
    }

    def 'unsupported: unwrap'() {
        setup:
        def rs = twoColumns(randomize(String) * 6)

        when:
        rs.unwrap(Map)

        then:
        thrown(UnsupportedOperationException)
    }

    def 'unsupported: isWrapperFor'() {
        setup:
        def rs = twoColumns(randomize(String) * 6)

        when:
        rs.isWrapperFor(Map)

        then:
        thrown(UnsupportedOperationException)
    }

    def 'warnings'() {
        setup:
        def rs = twoColumns(randomize(String) * 6)
        rs.warnings = new SQLWarning('test warning')

        when:
        rs.clearWarnings()

        then:
        !rs.warnings
    }

    private static void updateRows(ResultSet rs, String method, List updates) {
        rs.next()
        rs."$method"('a', updates[0])
        rs."$method"(2, updates[1])
    }

    private static boolean assertUpdates(ResultSet rs, List updates) {
        rs.getObject(1) == updates[0]
        rs.getObject('b') == updates[1]
    }

    def 'extract: getDate (as long)'() {
        setup:
        def items = randomize(Long) * 6
        def rs = twoColumns(items)

        when:
        def rows = extractRows(rs, rs.&getDate)

        then:
        assertRows rows, items.collect { new Date(it) }
    }

    def 'extract: getTime (as long)'() {
        setup:
        def items = randomize(Long) * 6
        def rs = twoColumns(items)

        when:
        def rows = extractRows(rs, rs.&getTime)

        then:
        assertRows rows, items.collect { new Time(it) }
    }

    def 'extract: getTimestamp (as long)'() {
        setup:
        def items = randomize(Long) * 6
        def rs = twoColumns(items)

        when:
        def rows = extractRows(rs, rs.&getTimestamp)

        then:
        assertRows rows, items.collect { new Timestamp(it) }
    }

    def 'extract: getBigDecimal'() {
        setup:
        def items = randomize(BigDecimal) {
            typeRandomizer BigDecimal, forBigDecimal()
        } * 6

        def rs = twoColumns(items)

        when:
        def rows = extractRows(rs, rs.&getBigDecimal)

        then:
        assertRows rows, items
    }

    def 'extract: getBigDecimal (with scale)'() {
        setup:
        def items = randomize(BigDecimal) {
            typeRandomizer BigDecimal, forBigDecimal(3)
        } * 6

        def rs = twoColumns(items)

        when:
        def rows = extractRows(rs, rs.&getBigDecimal.rcurry(3))

        then:
        assertRows rows, items
    }

    def 'extract: getBytes'() {
        setup:
        Class type = ([] as byte[]).class
        def items = randomize(type) {
            typeRandomizer type, forByteArray()
        } * 6

        def rs = twoColumns(items)

        when:
        def rows = extractRows(rs, rs.&getBytes)

        then:
        assertRows rows, items
    }

    @Unroll
    def 'extract (InputStream): #method'() {
        setup:
        def items = randomize(InputStream) {
            typeRandomizer InputStream, { rng ->
                new ByteArrayInputStream(forByteArray(3).call(rng) as byte[])
            }
        } * 6

        def rs = twoColumns(items)

        when:
        def rows = extractRows(rs, rs.&"$method")

        then:
        assertRows rows, items

        where:
        method << ['getAsciiStream', 'getUnicodeStream', 'getBinaryStream']
    }

    @Unroll
    def 'extract (Reader): #method'() {
        setup:
        def items = randomize(Reader) {
            typeRandomizer Reader, { Random rng ->
                new CharArrayReader(forString(3..6).call(rng) as char[])
            }
        } * 6

        def rs = twoColumns(items)

        when:
        def rows = extractRows(rs, rs.&"$method")

        then:
        assertRows rows, items

        where:
        method << ['getCharacterStream', 'getNCharacterStream']
    }

    def 'extract: getClob'() {
        setup:
        def items = randomize(Clob) {
            typeRandomizer Clob, { rng ->
                new MockClob(forString().call(rng))
            }
        } * 6
        def rs = twoColumns(items)

        when:
        def rows = extractRows(rs, rs.&getClob)

        then:
        assertRows rows, items
    }

    def 'extract: getRef'() {
        setup:
        def items = randomize(Ref) {
            typeRandomizer Ref, { rng ->
                new MockRef(
                    forString().call(rng),
                    forString().call(rng)
                )
            }
        } * 6
        def rs = twoColumns(items)

        when:
        def rows = extractRows(rs, rs.&getRef)

        then:
        assertRows rows, items
    }

    def 'extract: getRowId'() {
        setup:
        def items = randomize(RowId) {
            typeRandomizer RowId, { rng ->
                new MockRowId(forByteArray(10).call(rng))
            }
        } * 6
        def rs = twoColumns(items)

        when:
        def rows = extractRows(rs, rs.&getRowId)

        then:
        assertRows rows, items
    }

    def 'extract: getBlob'() {
        setup:
        def items = randomize(Blob) {
            typeRandomizer Blob, { rng ->
                new MockBlob(forByteArray(10).call(rng))
            }
        } * 6
        def rs = twoColumns(items)

        when:
        def rows = extractRows(rs, rs.&getBlob)

        then:
        assertRows rows, items
    }

    def 'extract: getNClob'() {
        setup:
        def items = randomize(NClob) {
            typeRandomizer NClob, { rng ->
                new MockClob(forString().call(rng)) as NClob
            }
        } * 6
        def rs = twoColumns(items)

        when:
        def rows = extractRows(rs, rs.&getNClob)

        then:
        assertRows rows, items
    }

    def 'extract: getArray'() {
        setup:
        def items = randomize(MockArray) {
            propertyRandomizer 'array', forLongArray(6)
        } * 6
        def rs = twoColumns(items)

        when:
        def rows = extractRows(rs, rs.&getArray)

        then:
        assertRows rows, items
    }

    private static boolean assertRows(final List<Map<Object, Object>> rows, final List items) {
        (0..<ROW_COUNT).every { row ->
            COL_IDS.every { col ->
                rows[row][col] == items.remove(0)
            }
        }
    }

    private static List<Map<Object, Object>> extractRows(final ResultSet rs, final Closure method) {
        def rows = []

        while (rs.next()) {
            rows << COL_IDS.collectEntries { col ->
                [col, method.call(col)]
            }
        }

        rows
    }

    private static MockResultSet twoColumns(items) {
        new MockResultSet(
            ['a', 'b'],
            [items[0..1] as Object[], items[2..3] as Object[], items[4..5] as Object[]]
        )
    }
}
