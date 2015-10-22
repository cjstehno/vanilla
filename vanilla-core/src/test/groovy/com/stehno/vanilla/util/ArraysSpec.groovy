package com.stehno.vanilla.util

import spock.lang.Specification

import static com.stehno.vanilla.util.Arrays.insert

class ArraysSpec extends Specification {

    def 'insert'() {
        expect:
        insert(target, index, chars, offset, len) == result

        where:
        target        | index | chars     | offset | len || result
        ch('abcdefg') | 2     | ch('123') | 1      | 2   || ch('ab23cdefg')
    }

    private static char[] ch(String str) {
        str.toCharArray()
    }
}
