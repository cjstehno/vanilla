package com.stehno.vanilla.test

import groovy.transform.ToString
import org.junit.Test

import static com.stehno.vanilla.test.PropertyRandomizer.randomize
import static com.stehno.vanilla.test.Randomizers.forStringArray

class PropertyRandomizerTest {

    @Test void 'randomize: simple'() {
        def rando = randomize(Pet)

        def one = rando.one()
        println one
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

    static void assertPopulated(Object obj, List<String> ignoredProperties = [], List<Class> ignoredTypes = [Class]) {
        obj.metaClass.properties.each { p ->
            if (!(p.type in ignoredTypes) && !(p.name in ignoredProperties)) {
                assert obj[p.name] != null
            }
        }
    }
}

@ToString
class Person {
    String name
    int age
    Date birthDate
    Pet pet
    String bankPin

    String[] children

    int getChildCount(){
        children?.size() ?: 0
    }
}

@ToString
class Pet {
    String name
    int age
}
