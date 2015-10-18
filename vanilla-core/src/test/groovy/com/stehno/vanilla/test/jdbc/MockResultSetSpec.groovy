package com.stehno.vanilla.test.jdbc

import spock.lang.Specification
import spock.lang.Unroll

import java.sql.Date
import java.sql.ResultSet
import java.sql.Time
import java.sql.Timestamp

import static com.stehno.vanilla.test.PropertyRandomizer.randomize
import static com.stehno.vanilla.test.Randomizers.*

class MockResultSetSpec extends Specification {

    private static final List COL_IDS = [1, 'b'].asImmutable()
    private static final int ROW_COUNT = 3

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
    def 'update: #method'() {
        setup:
        def rando = randomize(type)
        def rs = twoColumns(rando * 6)
        def updates = rando * 2

        when:
        updateRows rs, method, updates

        then:
        assertUpdates rs, updates

        where:
        method          | type
        'updateString'  | String
        'updateNString' | String
        'updateBoolean' | Boolean
        'updateFloat'   | Float
        'updateInt'     | Integer
        'updateDouble'  | Double
        'updateShort'   | Short
        'updateByte'    | Byte
        'updateLong'    | Long
        'updateObject'  | String
    }

    @Unroll
    def 'update (dates): #method'() {
        setup:
        def rando = randomize(Long)
        def rs = twoColumns(rando * 6)
        def updates = (rando * 2).collect { u -> type.newInstance(u) }

        when:
        updateRows rs, method, updates

        then:
        assertUpdates rs, updates*.time

        where:
        method            | type
        'updateDate'      | Date
        'updateTime'      | Time
        'updateTimestamp' | Timestamp
    }

    def 'updateNull'() {
        setup:
        def rando = randomize(String)
        def rs = twoColumns(rando * 6)

        when:
        rs.next()
        rs.updateNull(1)
        rs.updateNull('b')

        then:
        !rs.getObject(1)
        !rs.getObject('b')
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
            'rowUpdated', 'rowInserted', 'rowDeleted', 'getMetaData'
        ]
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

    def 'updateBigDecimal'() {
        setup:
        def rando = randomize(BigDecimal) { typeRandomizer BigDecimal, forBigDecimal() }
        def rs = twoColumns(rando * 6)
        def updates = rando * 2

        when:
        updateRows rs, 'updateBigDecimal', updates

        then:
        assertUpdates rs, updates
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
