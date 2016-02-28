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
package com.stehno.vanilla.mapper.transform

import com.stehno.vanilla.mapper.BarObject
import com.stehno.vanilla.mapper.FooObject
import com.stehno.vanilla.test.PropertyRandomizer
import spock.lang.Specification

import java.time.LocalDate

import static com.stehno.vanilla.test.PropertyRandomizer.randomize
import static java.time.format.DateTimeFormatter.BASIC_ISO_DATE

class MapperTransformSpec extends Specification {

    private final GroovyShell shell = new GroovyShell()

    private final BarObject bar = new BarObject()
    private final PropertyRandomizer rando = randomize(FooObject) {
        propertyRandomizer 'startDate', { new Date().format('MM/dd/yyyy') }
        propertyRandomizer 'birthDate', { LocalDate.now() }
        ignoringProperties 'child'
    }

    def 'simple usage as method'() {
        setup:
        FooObject foo = rando.one()

        when:
        def results = shell.evaluate("""
            package testing

            import com.stehno.vanilla.mapper.annotation.InjectObjectMapper
            import com.stehno.vanilla.mapper.ObjectMapper
            import java.time.format.*

            class Foo {
                @InjectObjectMapper({
                    map 'name'
                    map 'age' into 'years'
                    map 'startDate' using { Date.parse('MM/dd/yyyy', it) }
                    map 'birthDate' into 'birthday' using { d -> d.format(DateTimeFormatter.BASIC_ISO_DATE) }
                })
                static ObjectMapper createMapper(){}
            }

            Foo.createMapper()
        """)

        results.copy(foo, bar)

        then:
        bar.name == foo.name
        bar.years == foo.age
        bar.startDate.format('MM/dd/yyyy') == foo.startDate
        bar.birthday
    }

    def 'simple usage as method (to Map)'() {
        setup:
        FooObject foo = rando.one()

        Map<String,Object> map = [:]

        when:
        def results = shell.evaluate("""
            package testing

            import com.stehno.vanilla.mapper.annotation.InjectObjectMapper
            import com.stehno.vanilla.mapper.ObjectMapper
            import java.time.format.*

            class Foo {
                @InjectObjectMapper({
                    map 'name'
                    map 'age' into 'years'
                    map 'startDate' using { Date.parse('MM/dd/yyyy', it) }
                    map 'birthDate' into 'birthday' using { d -> d.format(DateTimeFormatter.BASIC_ISO_DATE) }
                })
                static ObjectMapper createMapper(){}
            }

            Foo.createMapper()
        """)

        results.copy(foo, map)

        then:
        map.name == foo.name
        map.years == foo.age
        map.startDate.format('MM/dd/yyyy') == foo.startDate
        map.birthday
    }

    def 'simple collector usage as method'() {
        setup:
        Collection<FooObject> foos = rando * 3

        when:
        def results = shell.evaluate("""
            package testing

            import com.stehno.vanilla.mapper.annotation.InjectObjectMapper
            import com.stehno.vanilla.mapper.ObjectMapper
            import java.time.format.*

            class Foo {
                @InjectObjectMapper({
                    map 'name'
                    map 'age' into 'years'
                    map 'startDate' using { Date.parse('MM/dd/yyyy', it) }
                    map 'birthDate' into 'birthday' using { d -> d.format(DateTimeFormatter.BASIC_ISO_DATE) }
                })
                static ObjectMapper createMapper(){}
            }

            Foo.createMapper()
        """)

        Collection<BarObject> bars = foos.collect results.collector(BarObject)

        then:
        bars.size() == 3

        bars.eachWithIndex { bar, idx ->
            assert bar.name == foos[idx].name
            assert bar.years == foos[idx].age
            assert bar.startDate.format('MM/dd/yyyy') == foos[idx].startDate
            assert bar.birthday == foos[idx].birthDate.format(BASIC_ISO_DATE)
        }
    }

    def 'simple usage as method (with nulls)'() {
        setup:
        FooObject foo = new FooObject()

        when:
        def results = shell.evaluate("""
            package testing

            import com.stehno.vanilla.mapper.annotation.InjectObjectMapper
            import com.stehno.vanilla.mapper.ObjectMapper
            import java.time.format.*

            class Foo {
                @InjectObjectMapper({
                    map 'name'
                    map 'age' into 'years'
                    map 'startDate' using { it ? Date.parse('MM/dd/yyyy', it) : null }
                    map 'birthDate' into 'birthday' using { d -> d?.format(DateTimeFormatter.BASIC_ISO_DATE) }
                })
                static ObjectMapper createMapper(){}
            }

            Foo.createMapper()
        """)

        results.copy(foo, bar)

        then:
        !bar.name
        !bar.years
        !bar.startDate
        !bar.birthday
    }

    def 'simple usage as method (closure args)'() {
        setup:
        FooObject foo = rando.one()

        when:
        def results = shell.evaluate("""
            package testing

            import com.stehno.vanilla.mapper.annotation.InjectObjectMapper
            import com.stehno.vanilla.mapper.ObjectMapper
            import java.time.format.*

            class Foo {
                @InjectObjectMapper({
                    map 'name'
                    map 'age' into 'years'
                    map 'startDate' using { Date.parse('MM/dd/yyyy','12/21/2012') }
                    map 'birthDate' into 'birthday' using { d, src, dst -> dst.startDate.format('MM/dd/yyyy') }
                })
                static ObjectMapper createMapper(){}
            }

            Foo.createMapper()
        """)

        results.copy(foo, bar)

        then:
        bar.name == foo.name
        bar.years == foo.age
        bar.startDate.format('MM/dd/yyyy') == '12/21/2012'
        bar.birthday == '12/21/2012'
    }

    def 'nested usage (as method)'() {
        setup:
        FooObject foo = rando.one()
        foo.child = rando.one()

        when:
        def om = shell.evaluate("""
            package testing

            import com.stehno.vanilla.mapper.annotation.InjectObjectMapper
            import com.stehno.vanilla.mapper.ObjectMapper
            import com.stehno.vanilla.mapper.BarObject
            import java.time.format.*
            import java.time.*

            class Foo {
                @InjectObjectMapper(
                    name='FooChild',
                    value={
                        map 'name'
                        map 'age' into 'years'
                        map 'startDate' using { String d -> Date.parse('MM/dd/yyyy', d) }
                        map 'birthDate' into 'birthday' using { LocalDate d -> d.format(DateTimeFormatter.BASIC_ISO_DATE) }
                    }
                )
                static ObjectMapper childMapper(){}

                @InjectObjectMapper({
                    map 'child' into 'descendent' using { x->
                        def y = new BarObject()
                        Foo.childMapper().copy(x, y)
                        y
                    }
                })
                static ObjectMapper createMapper(){}
            }

            Foo.createMapper()
        """)

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

    def 'simple usage as field'() {
        setup:
        FooObject foo = rando.one()

        when:
        def results = shell.evaluate("""
            package testing

            import com.stehno.vanilla.mapper.annotation.InjectObjectMapper
            import com.stehno.vanilla.mapper.ObjectMapper
            import java.time.format.*

            class Foo {
                @InjectObjectMapper({
                    map 'name'
                    map 'age' into 'years'
                    map 'startDate' using { String d -> Date.parse('MM/dd/yyyy', d) }
                    map 'birthDate' into 'birthday' using { d -> d.format(DateTimeFormatter.BASIC_ISO_DATE) }
                })
                static final ObjectMapper barMapper
            }

            Foo.barMapper
        """)

        results.copy(foo, bar)

        then:
        bar.name == foo.name
        bar.years == foo.age
        bar.startDate.format('MM/dd/yyyy') == foo.startDate
    }

    def 'simple usage as property'() {
        setup:
        FooObject foo = rando.one()

        when:
        def results = shell.evaluate("""
            package testing

            import com.stehno.vanilla.mapper.annotation.InjectObjectMapper
            import com.stehno.vanilla.mapper.ObjectMapper
            import java.time.format.*

            class Foo {
                @InjectObjectMapper({
                    map 'name'
                    map 'age' into 'years'
                    map 'startDate' using { String d -> Date.parse('MM/dd/yyyy', d) }
                    map 'birthDate' into 'birthday' using { d -> d.format(DateTimeFormatter.BASIC_ISO_DATE) }
                })
                final ObjectMapper barMapper
            }

            new Foo().barMapper
        """)

        results.copy(foo, bar)

        then:
        bar.name == foo.name
        bar.years == foo.age
        bar.startDate.format('MM/dd/yyyy') == foo.startDate
    }
}
