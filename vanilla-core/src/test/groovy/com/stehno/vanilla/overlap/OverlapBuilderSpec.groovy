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
