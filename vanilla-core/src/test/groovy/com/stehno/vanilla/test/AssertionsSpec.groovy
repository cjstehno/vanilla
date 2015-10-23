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
package com.stehno.vanilla.test

import groovy.transform.Canonical
import spock.lang.Shared
import spock.lang.Specification

import static com.stehno.vanilla.test.Assertions.assertMatches
import static com.stehno.vanilla.test.Assertions.assertToday
import static com.stehno.vanilla.test.Assertions.assertValidEqualsAndHashcode
import static com.stehno.vanilla.test.PropertyRandomizer.randomize

/**
 * Assumption: the Groovy @Canonical annotation generates "good" equals and hashCode methods.
 */
class AssertionsSpec extends Specification {

    @Shared
    private PropertyRandomizer randomSomething = randomize(Something)

    @Shared
    private Fixture fixture = FixtureBuilder.define {
        fix 'A', [fieldA: 'A', fieldB: 42]
    }

    def 'equals & hashCode: passing'() {
        expect:
        assertValidEqualsAndHashcode(a, b, c, diff)

        where:
        a                         | b                         | c                         || diff
        fixture.object(Something) | fixture.object(Something) | fixture.object(Something) || new Something('B', 24)
        fixture.object(Something) | fixture.object(Something) | fixture.object(Something) || new Something('A', 24)
        fixture.object(Something) | fixture.object(Something) | fixture.object(Something) || new Something('X', 42)
    }

    def 'equals & hashCode: failing'() {
        when:
        def (a, b, c, d) = randomSomething * 4
        assertValidEqualsAndHashcode(a, b, c, d)

        then:
        thrown(AssertionError)
    }

    def 'assertToday: passing'() {
        when:
        assertToday(new Date())

        then:
        notThrown(AssertionError)
    }

    def 'assertToday: failing'() {
        when:
        assertToday(new Date() - 2)

        then:
        thrown(AssertionError)
    }

    def 'assertMatches: passing'() {
        when:
        assertMatches(
            fixture.object(Something),
            fieldA: { a -> a == 'A' },
            fieldB: { b -> b > 40 && b < 50 }
        )

        then:
        notThrown(AssertionError)
    }

    def 'assertMatches: failing'() {
        when:
        assertMatches(
            fixture.object(Something),
            fieldA: { a -> a == 'C' },
            fieldB: { b -> b > 100 }
        )

        then:
        thrown(AssertionError)
    }
}

@Canonical
class Something {
    String fieldA
    int fieldB
}
