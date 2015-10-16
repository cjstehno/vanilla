package com.stehno.vanilla.test.jdbc

import spock.lang.Specification
import spock.lang.Unroll

import java.sql.ResultSet
import java.sql.Time
import java.sql.Timestamp

import static com.stehno.vanilla.test.PropertyRandomizer.randomize
import static com.stehno.vanilla.test.Randomizers.forBigDecimal
import static com.stehno.vanilla.test.Randomizers.forByteArray

class MockResultSetSpec extends Specification {

    private static final List COL_IDS = [1, 'b'].asImmutable()
    private static final int ROW_COUNT = 3

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
