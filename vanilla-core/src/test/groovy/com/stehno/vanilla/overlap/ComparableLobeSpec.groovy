/*
 * Copyright (c) 2015 Christopher J. Stehno
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
