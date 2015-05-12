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

import static com.stehno.vanilla.test.FixtureBuilder.define

@SuppressWarnings('GroovyPointlessBoolean')
class FixtureBuilderSpec extends Specification {

    @Shared private Fixture fixture = define {
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

    def 'maps: non-existent key'(){
        when:
        define {}.map('blah')

        then:
        thrown(AssertionError)
    }

    def 'maps: empty call on default fix'(){
        when:
        define {}.map()

        then:
        thrown(AssertionError)
    }

    def 'objects'() {
        expect:
        fixture.object(Comedians, fix) == value

        where:
        fix     || value
        null    || new Comedians(name: 'Larry', stooge: true)
        'alpha' || new Comedians(name: 'Larry', stooge: true)
        'bravo' || new Comedians(name: 'Joe', stooge: false)
    }

    def 'verifications'() {
        expect:
        fixture.verify(actual, fix) == value

        where:
        actual                                     | fix     || value
        new Comedians(name: 'Larry', stooge: true) | null    || true
        new Comedians(name: 'Larry', stooge: true) | 'alpha' || true
        new Comedians(name: 'Joe', stooge: false)  | 'bravo' || true
        new Comedians(name: 'Moe', stooge: true)  | 'bravo' || false
    }
}

@Canonical
class Comedians {
    String name
    boolean stooge
}
