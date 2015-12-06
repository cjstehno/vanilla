/*
 * Copyright (C) 2015 Christopher J. Stehno <chris@stehno.com>
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
package com.stehno.vanilla.mapper

import com.stehno.vanilla.test.PropertyRandomizer
import groovy.transform.ToString
import spock.lang.Specification

import java.time.LocalDate

import static com.stehno.vanilla.mapper.runtime.RuntimeObjectMapper.mapper
import static com.stehno.vanilla.test.PropertyRandomizer.randomize
import static com.stehno.vanilla.test.Randomizers.forDate
import static java.time.format.DateTimeFormatter.BASIC_ISO_DATE

class ObjectMapperSpec extends Specification {

    private final BarObject bar = new BarObject()
    private final PropertyRandomizer rando = randomize(FooObject) {
        propertyRandomizer 'startDate', { new Date().format('MM/dd/yyyy') }
        propertyRandomizer 'birthDate', { LocalDate.now() }
        ignoringProperties 'child'
    }

    def 'simple usage'() {
        setup:
        def om = mapper {
            map 'name'
            map 'age' into 'years'
            map 'startDate' using { Date.parse('MM/dd/yyyy', it) }
            map 'birthDate' into 'birthday' using { LocalDate d -> d.format(BASIC_ISO_DATE) }
        }

        FooObject foo = rando.one()

        when:
        om.copy(foo, bar)

        then:
        bar.name == foo.name
        bar.years == foo.age
        bar.startDate.format('MM/dd/yyyy') == foo.startDate
        bar.birthday == foo.birthDate.format(BASIC_ISO_DATE)

        foo.percentage
        !bar.rate
    }

    def 'simple collector usage'() {
        setup:
        def om = mapper {
            map 'name'
            map 'age' into 'years'
            map 'startDate' using { Date.parse('MM/dd/yyyy', it) }
            map 'birthDate' into 'birthday' using { LocalDate d -> d.format(BASIC_ISO_DATE) }
        }

        Collection<FooObject> foos = rando * 3

        when:
        Collection<BarObject> bars = foos.collect om.collector(BarObject)

        then:
        bars.size() == 3

        bars.eachWithIndex { bar, idx ->
            assert bar.name == foos[idx].name
            assert bar.years == foos[idx].age
            assert bar.startDate.format('MM/dd/yyyy') == foos[idx].startDate
            assert bar.birthday == foos[idx].birthDate.format(BASIC_ISO_DATE)
        }
    }

    def 'simple usage (with nulls)'() {
        setup:
        def om = mapper {
            map 'name'
            map 'age' into 'years'
            map 'startDate' using { String d -> d ? Date.parse('MM/dd/yyyy', d) : null }
            map 'birthDate' into 'birthday' using { LocalDate d -> d?.format(BASIC_ISO_DATE) }
        }

        FooObject foo = new FooObject()

        when:
        om.copy(foo, bar)

        then:
        !bar.name
        !bar.years
        !bar.startDate
        !bar.birthday
        !bar.rate
    }

    def 'simple usage (closure args)'() {
        setup:
        def om = mapper {
            map 'name'
            map 'age' into 'years'
            map 'startDate' using { Date.parse('MM/dd/yyyy', '12/21/2012') }
            map 'birthDate' into 'birthday' using { d, src, dst -> dst.startDate.format('MM/dd/yyyy') }
        }

        FooObject foo = rando.one()

        when:
        om.copy(foo, bar)

        then:
        bar.name == foo.name
        bar.years == foo.age
        bar.startDate.format('MM/dd/yyyy') == '12/21/2012'
        bar.birthday == '12/21/2012'

        foo.percentage
        !bar.rate
    }

    def 'nested usage'() {
        setup:
        def om = mapper {
            map 'child' into 'descendent' using mapper {
                map 'name'
                map 'age' into 'years'
                map 'startDate' using { String d -> Date.parse('MM/dd/yyyy', d) }
                map 'birthDate' into 'birthday' using { LocalDate d -> d.format(BASIC_ISO_DATE) }
            }
        }

        FooObject foo = rando.one()
        foo.child = rando.one()

        when:
        om.copy(foo, bar)

        then:
        !bar.name
        !bar.years
        !bar.startDate
        !bar.birthday

        bar.descendent.name == foo.child.name
        bar.descendent.years == foo.child.age
        bar.descendent.startDate.format('MM/dd/yyyy') == foo.child.startDate
        bar.descendent.birthday == foo.child.birthDate.format(BASIC_ISO_DATE)
    }

    def 'mapped source property does not exist'() {
        setup:
        def om = mapper {
            map 'goober'
        }

        when:
        om.copy(rando.one(), bar)

        then:
        def ex = thrown(MissingPropertyException)
        ex.message == 'No such property: goober for class: com.stehno.vanilla.mapper.FooObject'
    }

    def 'mapped destination property does not exist'() {
        setup:
        def om = mapper {
            map 'name' into 'nombre'
        }

        when:
        om.copy(rando.one(), bar)

        then:
        def ex = thrown(MissingPropertyException)
        ex.message == 'No such property: nombre for class: com.stehno.vanilla.mapper.BarObject\nPossible solutions: name'
    }

    def 'simple usage (POGO to POJO)'() {
        setup:
        def om = mapper {
            map 'name' into 'label'
            map 'age'
            map 'startDate' using { String d -> Date.parse('MM/dd/yyyy', d) }
            map 'birthDate' into 'birthday' using { LocalDate d -> d.format(BASIC_ISO_DATE) }
            map 'percentage' into 'pct'
        }

        FooObject foo = rando.one()
        BazObject baz = new BazObject()

        when:
        om.copy(foo, baz)

        then:
        baz.label == foo.name
        baz.age == foo.age
        baz.startDate.format('MM/dd/yyyy') == foo.startDate
        baz.birthday == foo.birthDate.format(BASIC_ISO_DATE)
        baz.pct == foo.percentage

        foo.percentage
        !bar.rate
    }

    def 'simple usage (POJO to POJO)'() {
        setup:
        def om = mapper {
            map 'label'
            map 'age'
            map 'startDate'
            map 'birthday'
            map 'pct'
        }

        BazObject src = randomize(BazObject) {
            propertyRandomizer 'startDate', forDate()
        }.one()

        BazObject dest = new BazObject()

        when:
        om.copy(src, dest)

        then:
        src == dest
    }
}

@ToString(includeNames = true)
class FooObject {
    String name
    int age
    String startDate
    LocalDate birthDate
    float percentage
    FooObject child
}

@ToString(includeNames = true)
class BarObject {
    String name
    int years
    Date startDate
    String birthday
    Float rate
    BarObject descendent
}
