package com.stehno.vanilla.overlap

import spock.lang.Specification
import spock.lang.Unroll

import static com.stehno.vanilla.overlap.Lobe.ANY

class ComparableLobeSpec extends Specification {

    @Unroll
    def 'overlap: simple'(lhs, rhs, boolean overlaps) {
        expect:
        new ComparableLobe(lhs).overlaps(new ComparableLobe(rhs)) == overlaps

        where:
        lhs             | rhs        | overlaps
        100             | 100        | true
        [100, 200]      | [100, 200] | true
        [100, 200]      | [200, 100] | true
        100..200        | [150, 300] | true
        100             | 200        | false
        [100..110, 301] | 150..300   | false
        'C'             | 'C'        | true
        'C'             | 'D'        | false
        ['C', 'D']      | ['C', 'D'] | true
        ['C', 'D']      | ['D', 'C'] | true
        'C'..'E'        | ['D', 'G'] | true
    }

    def 'overlap: with any'() {
        setup:
        ComparableLobe lobeA = new ComparableLobe('A')
        lobeA << 'C'

        when:
        def overLobe = ANY.overlaps(lobeA)
        def overSelf = ANY.overlaps(ANY)

        then:
        assert overLobe
        assert overSelf
    }
}
