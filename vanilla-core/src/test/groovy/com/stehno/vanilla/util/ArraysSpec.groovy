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
