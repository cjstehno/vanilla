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

import org.junit.Test

import static com.stehno.vanilla.test.PropertyRandomizer.randomize
import static com.stehno.vanilla.test.Randomizers.forStringArray

class PropertyRandomizerTest {

    @Test void 'randomize: simple'() {
        def rando = randomize(Pet)

        def one = rando.one()
        assert one instanceof Pet
        assertPopulated one

        def three = rando.times(3)
        assert three.size() == 3
        three.each {
            assertPopulated it
        }

        def four = rando * 4
        assert four.size() == 4
        four.each {
            assertPopulated it
        }
    }

    @Test void 'randomize: complex'() {
        def rando = randomize(Person)
            .ignoringProperties('bankPin')
            .typeRandomizers(
                (Date): { new Date() },
                (Pet): { randomize(Pet).one() },
                (String[]): forStringArray()
            )
            .propertyRandomizers(name: { 'FixedValue' })

        def one = rando.one()
        assert one instanceof Person
        assertPopulated one, ['bankPin']
        assert !one.bankPin

        def three = rando.times(3)
        assert three.size() == 3
        three.each {
            assertPopulated it, ['bankPin']
        }

        def four = rando * 4
        assert four.size() == 4
        four.each {
            assertPopulated it, ['bankPin']
        }
    }

    @Test void 'randomize: complex (DSL)'() {
        def rando = randomize(Person) {
            ignoringProperties 'bankPin'
            typeRandomizers(
                (Date): { new Date() },
                (Pet): { randomize(Pet).one() }
            )
            typeRandomizer String[], forStringArray()
            propertyRandomizer 'name', { 'FixedValue' }
        }

        def one = rando.one()
        assert one instanceof Person
        assertPopulated one, ['bankPin']
        assert !one.bankPin

        def three = rando.times(3)
        assert three.size() == 3
        three.each {
            assertPopulated it, ['bankPin']
        }

        def four = rando * 4
        assert four.size() == 4
        four.each {
            assertPopulated it, ['bankPin']
        }
    }

    @Test void 'randomize: simple type'(){
        def rando = randomize(String)

        def one = rando.one()
        assert one

        def three = rando * 3
        assert three.size() == 3
    }

    @Test void 'conversion to Map'(){
        def rando = randomize(Person) {
            ignoringProperties 'bankPin'
            typeRandomizers(
                (Date): { new Date() },
                (Pet): { randomize(Pet).one() }
            )
            typeRandomizer String[], forStringArray()
            propertyRandomizer 'name', { 'FixedValue' }
        }

        def one = rando.one()
        def map = one as Map

        map.each {k,v->
            assert one[k] == v
        }
    }

    @Test void 'randomizer: using PropertyRandomizer as typeRandomizer'(){
        def rando = randomize(Person) {
            ignoringProperties 'bankPin'
            typeRandomizers(
                (Date): { new Date() },
                (Pet): randomize(Pet)
            )
            typeRandomizer String[], forStringArray()
            propertyRandomizer 'name', { 'FixedValue' }
        }

        def one = rando.one()
        assert one instanceof Person
        assertPopulated one, ['bankPin']
        assert !one.bankPin

        def three = rando.times(3)
        assert three.size() == 3
        three.each {
            assertPopulated it, ['bankPin']
        }

        def four = rando * 4
        assert four.size() == 4
        four.each {
            assertPopulated it, ['bankPin']
        }
    }

    @Test void '(randomizer * 0) should return []'(){
        def rando = randomize(Pet)

        def nothing = rando * 0
        assert nothing != null
        assert nothing instanceof Collection
        assert nothing.empty

        nothing = rando.times(0)
        assert nothing != null
        assert nothing instanceof Collection
        assert nothing.empty
    }

    @Test void 'randomize immutable'(){
        def rando = randomize(Rock)

        Rock rock = rando.one()
        assert rock
        assert rock.description
        assert rock.weight
    }

    @Test void 'randomize immutable (defined)'(){
        def rando = randomize(Rock){
            typeRandomizer Rock, { new Rock('adfasdfasdf', 100.234d)}
        }

        Rock rock = rando.one()
        assert rock
        assert rock.description
        assert rock.weight
    }

    static void assertPopulated(Object obj, List<String> ignoredProperties = [], List<Class> ignoredTypes = [Class]) {
        obj.metaClass.properties.each { p ->
            if (!(p.type in ignoredTypes) && !(p.name in ignoredProperties)) {
                assert obj[p.name] != null
            }
        }
    }
}


