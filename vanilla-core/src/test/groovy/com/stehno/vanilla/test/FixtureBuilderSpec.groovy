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
package com.stehno.vanilla.test

import groovy.transform.Canonical
import spock.lang.Shared
import spock.lang.Specification

import static com.stehno.vanilla.test.FixtureBuilder.define
import static com.stehno.vanilla.test.PropertyRandomizer.randomize

@SuppressWarnings('GroovyPointlessBoolean')
class FixtureBuilderSpec extends Specification {

    @Shared private final Fixture fixture = define {
        fix 'alpha', [name: 'Larry', stooge: true]
        fix 'bravo', [name: 'Joe', stooge: false]
    }

    def 'fields'() {
        expect:
        fixture.field(name, fix) == value

        where:
        name     | fix     || value
        'name'   | 'alpha' || 'Larry'
        'name'   | 'bravo' || 'Joe'
        'name'   | null    || 'Larry'
        'stooge' | 'alpha' || true
        'stooge' | 'bravo' || false
        'stooge' | null    || true
    }

    def 'maps'() {
        expect:
        fixture.map(fix) == value

        where:
        fix     || value
        null    || [name: 'Larry', stooge: true]
        'alpha' || [name: 'Larry', stooge: true]
        'bravo' || [name: 'Joe', stooge: false]
    }

    def 'maps: with extra attrs'() {
        expect:
        fixture.map(attrs, fix) == value

        where:
        fix     | attrs          || value
        null    | [age: 42]      || [name: 'Larry', stooge: true, age: 42]
        'alpha' | [:]            || [name: 'Larry', stooge: true]
        'bravo' | [stooge: true] || [name: 'Joe', stooge: true]
    }

    def 'maps: non-existent key'() {
        when:
        define {}.map('blah')

        then:
        thrown(AssertionError)
    }

    def 'maps: empty call on default fix'() {
        when:
        define {}.map()

        then:
        thrown(AssertionError)
    }

    def 'objects'() {
        expect:
        fixture.object(Comedian, fix) == value

        where:
        fix     || value
        null    || new Comedian(name: 'Larry', stooge: true)
        'alpha' || new Comedian(name: 'Larry', stooge: true)
        'bravo' || new Comedian(name: 'Joe', stooge: false)
    }

    def 'objects: with extra attrs'() {
        expect:
        fixture.object(attrs, Comedian, fix) == value

        where:
        fix     | attrs          || value
        null    | [:]            || new Comedian(name: 'Larry', stooge: true)
        'alpha' | [name: 'Moe']  || new Comedian(name: 'Moe', stooge: true)
        'bravo' | [stooge: true] || new Comedian(name: 'Joe', stooge: true)
    }

    def 'verifications'() {
        expect:
        fixture.verify(actual, fix) == value

        where:
        actual                      | fix     || value
        new Comedian('Larry', true) | null    || true
        new Comedian('Larry', true) | 'alpha' || true
        new Comedian('Joe', false)  | 'bravo' || true
        new Comedian('Moe', true)   | 'bravo' || false
    }

    def 'verifications: with extra attrs'() {
        expect:
        fixture.verify(attrs, actual, fix) == value

        where:
        actual                      | fix     | attrs          || value
        new Comedian('Larry', true) | null    | [:]            || true
        new Comedian('Bob', true)   | 'alpha' | [name: 'Bob']  || true
        new Comedian('Joe', true)   | 'bravo' | [stooge: true] || true
    }

    def 'fix with PropertyRandomizer'() {
        when:
        def randomFixture = define {
            fix 'rand-A', randomize(Comedian)
            fix 'rand-B', randomize(Comedian).one() as Map
        }

        def a = randomFixture.map('rand-A')
        def b = randomFixture.map('rand-B')

        then:
        a.size() == 2
        a.every { k, v -> v != null }
        b.size() == 2
        b.every { k, v -> v != null }
    }
}

@Canonical
class Comedian {
    String name
    boolean stooge
}
