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
package com.stehno.vanilla.overlap

import spock.lang.Specification
import spock.lang.Unroll

class OverlapBuilderSpec extends Specification {

    def 'overlap without lobes'() {
        when:
        def over = new OverlapBuilder().overlaps()

        then:
        assert !over
    }

    def 'overlap with nulls'() {
        when:
        new OverlapBuilder().appendLobe(null, null).overlaps()

        then:
        thrown(IllegalArgumentException)
    }

    @Unroll
    def 'overlap: appendComparable'() {
        expect:
        new OverlapBuilder().appendComparable(a, b).overlaps() == overlap

        where:
        a       | b         || overlap
        10..100 | 15        || true
        15      | (10..100) || true
        25..100 | 15        || false
    }

    @Unroll
    def 'overlap: appendLobe'() {
        expect:
        new OverlapBuilder().appendLobe(new ComparableLobe(a), new ComparableLobe(b)).overlaps() == overlap

        where:
        a            | b              || overlap
        [10..100, 2] | [2, 105]       || true
        [2, 105]     | [10..100, 2]   || true
        [2, 105]     | [10..100, 200] || false
    }

    def 'overlap: multiple lobes'() {
        expect:
        new OverlapBuilder()
            .appendLobe(a, b)
            .appendComparable(c, d)
            .appendComparable(e, f)
            .overlaps() == overlap

        where:
        a                       | b                       | c      | d  | e        | f        | overlap
        new ComparableLobe('M') | new ComparableLobe('M') | 18..36 | 25 | 100..200 | 150..175 | true
        new ComparableLobe('M') | new ComparableLobe('M') | 18..36 | 25 | 100..200 | 300..400 | false
        new ComparableLobe('M') | new ComparableLobe('M') | 18..36 | 40 | 100..200 | 150..175 | false
    }
}
