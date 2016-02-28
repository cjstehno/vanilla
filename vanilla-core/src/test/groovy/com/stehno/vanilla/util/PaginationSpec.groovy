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
package com.stehno.vanilla.util

import spock.lang.Specification

class PaginationSpec extends Specification {

    def 'range'() {
        expect:
        Pagination.range(page, 3, 10) == range

        where:
        page || range
        1    || (0..2)
        2    || (3..5)
        3    || (6..8)
        4    || (9..9)
    }

    def 'page: collection'() {
        setup:
        def collection = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h']

        expect:
        Pagination.page(collection, page, 3) == items

        where:
        page || items
        1    || ['a', 'b', 'c']
        2    || ['d', 'e', 'f']
        3    || ['g', 'h']
    }

    def 'page: array'() {
        setup:
        def collection = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'] as String[]

        expect:
        Pagination.page(collection, page, 3) == items

        where:
        page || items
        1    || ['a', 'b', 'c'] as String[]
        2    || ['d', 'e', 'f'] as String[]
        3    || ['g', 'h'] as String[]
    }

    def 'range: no items'() {
        when:
        def range = Pagination.range(1, 3, 0)

        then:
        range == (0..0)
    }

    def 'range: out of bounds'() {
        when:
        Pagination.range(0, 3, 10)

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == 'The page number must be greater than zero.'
    }

    def 'range: invalid page size'() {
        when:
        Pagination.range(1, 0, 10)

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == 'The page size must be greater than zero.'
    }

    def 'countPages'() {
        expect:
        Pagination.countPages(total, pageSize) == count

        where:
        total | pageSize || count
        0     | 3        || 0
        2     | 3        || 1
        4     | 3        || 2
        6     | 3        || 2
        8     | 3        || 3
        9     | 3        || 3
        10    | 3        || 4
        12    | 3        || 4
        24    | 3        || 8
    }
}
