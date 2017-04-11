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
package com.stehno.vanilla.test

import spock.lang.Specification
import spock.lang.Unroll

import java.lang.annotation.RetentionPolicy

import static com.stehno.vanilla.test.Randomizers.*
import static java.lang.Long.MAX_VALUE
import static java.time.Instant.ofEpochMilli
import static java.time.LocalDateTime.now
import static java.time.LocalDateTime.ofInstant
import static java.time.ZoneId.systemDefault

class RandomizersSpec extends Specification {

    private final Random random = new Random()

    def 'constant'() {
        Randomizers.constant(42) == 42
    }

    def 'random'() {
        when:
        String value = Randomizers.random(forString(10..15))

        then:
        (10..15).containsWithinBounds value.length()
    }

    def 'forString'() {
        when:
        String value = forString(3..10).call(random)

        then:
        (3..10).containsWithinBounds(value.length())
        value.each { c -> Randomizers.CHARS.contains(c) }
    }

    def 'forString(alt chars)'() {
        when:
        String value = forString(3..10, 'qwerty').call(random)

        then:
        (3..10).containsWithinBounds(value.length())
        value.each { c -> 'qwerty'.contains(c) }
    }

    def 'forNumberString'() {
        when:
        String value = forNumberString(3..10, true).call(random)

        then:
        (3..10).containsWithinBounds(value.length())
        value.count('.') == 1
        value.each { c ->
            (c as char).isDigit() || c == '.'
        }
    }

    def 'forList()'() {
        when:
        List<String> items = forList(forString(1..25)).call(random, null)

        then:
        (0..10).containsWithinBounds(items.size())

        items.every { item ->
            item && item instanceof String
        }
    }

    def 'forList(5..25)'() {
        when:
        List<String> items = forList(5..25, forString(1..25)).call(random, null)

        then:
        (5..25).containsWithinBounds(items.size())

        items.every { item ->
            item && item instanceof String
        }
    }

    def 'forSet()'() {
        when:
        Set<String> items = forSet(forString(1..25)).call(random, null)

        then:
        (0..10).containsWithinBounds(items.size())

        items.every { item ->
            item && item instanceof String
        }
    }

    def 'forArray()'() {
        when:
        String[] items = forArray(forString(1..25)).call(random, null)

        then:
        (0..10).containsWithinBounds(items.length)

        items.every { item ->
            item && item instanceof String
        }
    }

    @Unroll
    def 'for#method()'() {
        when:
        def value = Randomizers."for$method"(values).call(random)

        then:
        values.containsWithinBounds value

        where:
        method    | values
        'Short'   | (10 as short)..(100 as short)
        'Integer' | 10..100
        'Float'   | 10f..100f
        'Double'  | 10d..100d
        'Long'    | 10l..100l
    }

    def 'forBoolean'() {
        when:
        boolean value = Randomizers.forBoolean().call(random)

        then:
        value || !value
    }

    def 'forByte'() {
        when:
        byte value = forByte().call(random)

        then:
        (Byte.MIN_VALUE..Byte.MAX_VALUE).containsWithinBounds(value)
    }

    def 'forByteArray'() {
        when:
        byte[] bytes = forByteArray(1..10).call(random)

        then:
        bytes.size()
    }

    def 'forEnum'() {
        when:
        def value = forEnum(RetentionPolicy).call(random)

        then:
        RetentionPolicy.values().contains(value)
    }

    def 'forDate'() {
        when:
        def value = forDate().call(random)

        then:
        value
    }

    def 'forDate(range)'() {
        setup:
        Date start = new Date() - 30
        Date end = new Date()

        when:
        def value = forDate(start..end).call(random)

        then:
        value.after(start)
        value.before(end)
    }

    def 'forChar'() {
        when:
        char value = forChar().call(random)

        then:
        Randomizers.CHARS.chars.contains(value)
    }

    @Unroll
    def 'forLocalDateTime(#start, #end)'() {
        when:
        def value = forLocalDateTime(range).call(random)

        then:
        value.isAfter(lower) && value.isBefore(upper)

        where:
        range                                    | lower                                       | upper
        now().minusDays(30)..now().minusHours(3) | now().minusDays(30)                         | now().minusHours(3)
        null                                     | ofInstant(ofEpochMilli(0), systemDefault()) | ofInstant(ofEpochMilli(MAX_VALUE), systemDefault())
    }

    @Unroll
    def 'forCollection(#allowed)'() {
        expect:
        allowed.contains(forCollection(allowed).call(random))

        where:
        allowed << [
            ['one', 'two', 'three'],
            ['four', 'five', 'six'] as Object[],
            [7, 8, 9] as long[]
        ]
    }

    def 'forItems()'() {
        when:
        String item = forItems('a', 'b', 'c').call(random)

        then:
        item in ['a', 'b', 'c']
    }

    @Unroll
    def 'forRange(#range)'() {
        expect:
        range.containsWithinBounds(forRange(range).call(random))

        where:
        range << [
            8..73,
            'm'..'s',
            100L..200L
        ]
    }

    def 'forTemplate'() {
        when:
        String result = forTemplate(
            '($area) ${prefix}-${number}',
            area: forNumberString(3..3),
            prefix: forNumberString(3..3),
            number: forNumberString(4..4)
        ).call(random)

        then:
        result.size() == 14
        result.contains('(') && result.contains(')') && result.contains('-')
    }
}
