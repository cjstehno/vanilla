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
package com.stehno.vanilla.jdbc.mapper.transform

import com.stehno.vanilla.jdbc.DummyObjectC
import com.stehno.vanilla.test.Person
import spock.lang.Specification

import static com.stehno.vanilla.test.Assertions.assertMatches
import static com.stehno.vanilla.test.jdbc.mock.ResultSetBuilder.resultSet

class InjectResultSetMapperTransformSpec extends Specification {

    private final GroovyShell shell = new GroovyShell()

    def 'implicit mapper'() {
        setup:
        def person = new Person(name: 'Bob', age: 42, birthDate: new Date())

        def rs = resultSet {
            columns 'name', 'age', 'birth_date', 'bank_pin'
            data person.name, person.age, person.birthDate.format('yyyy-MM-dd'), person.bankPin
            data null, 0, null, null
        }

        when:
        def mapper = shell.evaluate('''
            package testing

            import com.stehno.vanilla.jdbc.mapper.annotation.InjectResultSetMapper
            import com.stehno.vanilla.jdbc.mapper.ResultSetMapper
            import java.time.format.*

            import static com.stehno.vanilla.jdbc.mapper.MappingStyle.IMPLICIT

            class Foo {
                @InjectResultSetMapper(
                    value = com.stehno.vanilla.test.Person,
                    style = IMPLICIT,
                    config = {
                        ignore 'bankPin'
                        ignore 'pet'
                        map 'birthDate' fromDate 'birth_date'
                        map 'age' from 2 using { a -> a - 5 }
                        map 'name' from 'name'
                        ignore 'children'
                    }
                )
                static ResultSetMapper createMapper(){}
            }

            Foo.createMapper()
        ''')

        rs.next()
        def obj = mapper(rs)

        rs.next()
        def empty = mapper(rs)

        then:
        assertMatches(
            obj,
            name: person.name,
            age: person.age - 5,
            birthDate: { act -> person.birthDate.format('yyyy-MM-dd') == act.format('yyyy-MM-dd') },
            bankPin: person.bankPin
        )

        assertMatches(
            empty,
            name: null,
            age: -5,
            birthDate: null,
            bankPin: null
        )
    }

    def 'implicit mapper without config should map everything'() {
        setup:
        DummyObjectC objectC = new DummyObjectC('Larry', 56, 125.65f)
        objectC.somethingElse = 55 as byte

        def rs = resultSet {
            columns 'name', 'age', 'weight', 'something_else'
            object objectC
            object new DummyObjectC()
        }

        when:
        def mapper = shell.evaluate('''
            package testing

            import com.stehno.vanilla.jdbc.mapper.annotation.InjectResultSetMapper
            import com.stehno.vanilla.jdbc.mapper.ResultSetMapper
            import java.time.format.*
            import com.stehno.vanilla.jdbc.DummyObjectC

            import static com.stehno.vanilla.jdbc.mapper.MappingStyle.IMPLICIT

            class Foo {
                @InjectResultSetMapper(
                    value = DummyObjectC,
                    style = IMPLICIT
                )
                static ResultSetMapper createMapper(){}
            }

            Foo.createMapper()
        ''')

        rs.next()
        def obj = mapper(rs)

        rs.next()
        def empty = mapper.call(rs)

        then:
        obj == objectC
        empty == new DummyObjectC()
    }

    def 'unspecified style should be implicit'() {
        setup:
        DummyObjectC objectC = new DummyObjectC('Larry', 56, 125.65f)
        objectC.somethingElse = 55 as byte

        def rs = resultSet {
            columns 'name', 'age', 'weight', 'something_else'
            object objectC
        }

        when:
        def mapper = shell.evaluate('''
            package testing

            import com.stehno.vanilla.jdbc.mapper.annotation.InjectResultSetMapper
            import com.stehno.vanilla.jdbc.mapper.ResultSetMapper
            import java.time.format.*
            import com.stehno.vanilla.jdbc.DummyObjectC

            class Foo {
                @InjectResultSetMapper(DummyObjectC)
                static ResultSetMapper createMapper(){}
            }

            Foo.createMapper()
        ''')

        rs.next()
        def obj = mapper(rs)

        then:
        obj == objectC
        obj.name == 'Larry'
    }

    def 'explicit mapping in same object'() {
        setup:
        def rs = resultSet {
            columns 'name', 'number'
            data 'something', 42
        }

        when:
        def mapper = shell.evaluate('''
            package testing

            import com.stehno.vanilla.jdbc.mapper.MappingStyle
            import com.stehno.vanilla.jdbc.mapper.ResultSetMapper
            import com.stehno.vanilla.jdbc.mapper.annotation.InjectResultSetMapper

            import static com.stehno.vanilla.jdbc.mapper.MappingStyle.EXPLICIT

            class Foo {
                String name
                int number

                @InjectResultSetMapper(value=Foo, style=EXPLICIT, config={
                    map 'name'
                    map 'number'
                })
                static ResultSetMapper mapper(){}
            }

            Foo.mapper()
        ''')

        rs.next()
        def obj = mapper(rs)

        then:
        obj
        obj.name == 'something'
        obj.number == 42
    }

    def 'implicit mapping with prefix'() {
        setup:
        def rs = resultSet {
            columns 'blah_name', 'blah_number'
            data 'something', 42
        }

        when:
        def mapper = shell.evaluate('''
            package testing

            import com.stehno.vanilla.jdbc.mapper.MappingStyle
            import com.stehno.vanilla.jdbc.mapper.ResultSetMapper
            import com.stehno.vanilla.jdbc.mapper.annotation.InjectResultSetMapper

            import static com.stehno.vanilla.jdbc.mapper.MappingStyle.EXPLICIT

            class Foo {
                String name
                int number

                @InjectResultSetMapper(value=Foo)
                static ResultSetMapper mapper(String prefix='blah_'){}
            }

            Foo.mapper()
        ''')

        rs.next()
        def obj = mapper(rs)

        then:
        mapper.prefix == 'blah_'
        obj
        obj.name == 'something'
        obj.number == 42
    }

    def 'explicit mapping with prefix'() {
        setup:
        def rs = resultSet {
            columns 'blah_name', 'blah_number'
            data 'something', 42
        }

        when:
        def mapper = shell.evaluate('''
            package testing

            import com.stehno.vanilla.jdbc.mapper.MappingStyle
            import com.stehno.vanilla.jdbc.mapper.ResultSetMapper
            import com.stehno.vanilla.jdbc.mapper.annotation.InjectResultSetMapper

            import static com.stehno.vanilla.jdbc.mapper.MappingStyle.EXPLICIT

            class Foo {
                String name
                int number

                @InjectResultSetMapper(value=Foo, style=EXPLICIT, config = {
                    map 'name' from 'name'
                    map 'number'
                })
                static ResultSetMapper mapper(String prefix=''){}
            }

            Foo.mapper('blah_')
        ''')

        rs.next()
        def obj = mapper(rs)

        then:
        mapper.prefix == 'blah_'
        obj
        obj.name == 'something'
        obj.number == 42
    }

    def 'explicit mapping from multiple fields'() {
        setup:
        def rs = resultSet {
            columns 'first_name', 'last_name', 'number'
            data 'Inigo', 'Montoya', 42
        }

        when:
        def mapper = shell.evaluate('''
            package testing

            import com.stehno.vanilla.jdbc.mapper.MappingStyle
            import com.stehno.vanilla.jdbc.mapper.ResultSetMapper
            import com.stehno.vanilla.jdbc.mapper.annotation.InjectResultSetMapper

            import static com.stehno.vanilla.jdbc.mapper.MappingStyle.EXPLICIT

            class Foo {
                String name
                int number

                @InjectResultSetMapper(value=Foo, style=EXPLICIT, config={
                    map 'name' from 'first_name' using { fn, rs-> "$fn ${rs.getString('last_name')}"}
                    map 'number'
                })
                static ResultSetMapper mapper(){}
            }

            Foo.mapper()
        ''')

        rs.next()
        def obj = mapper(rs)

        then:
        obj
        obj.name == 'Inigo Montoya'
        obj.number == 42
    }

    def 'explicit mapping of generated property from multiple fields'() {
        setup:
        def rs = resultSet {
            columns 'first_name', 'last_name', 'number'
            data 'Inigo', 'Montoya', 42
        }

        when:
        def mapper = shell.evaluate('''
            package testing

            import com.stehno.vanilla.jdbc.mapper.MappingStyle
            import com.stehno.vanilla.jdbc.mapper.ResultSetMapper
            import com.stehno.vanilla.jdbc.mapper.annotation.InjectResultSetMapper

            import static com.stehno.vanilla.jdbc.mapper.MappingStyle.EXPLICIT

            class Foo {
                String name
                int number

                @InjectResultSetMapper(value=Foo, style=EXPLICIT, config={
                    map 'name' from 'first_name' using { fn, rs-> "$fn ${rs.getString('last_name')}"}
                    map 'number'
                })
                static ResultSetMapper mapper(){}
            }

            Foo.mapper()
        ''')

        rs.next()
        def obj = mapper(rs)

        then:
        obj
        obj.name == 'Inigo Montoya'
        obj.number == 42
    }

    def 'implicit mapping in same object'() {
        setup:
        def rs = resultSet {
            columns 'name', 'number'
            data 'something', 42
        }

        when:
        def mapper = shell.evaluate('''
            package testing
            import com.stehno.vanilla.jdbc.mapper.ResultSetMapper
            import com.stehno.vanilla.jdbc.mapper.annotation.InjectResultSetMapper
            import groovy.transform.CompileStatic

            class Foo {
                String name
                int number

                @InjectResultSetMapper(Foo)
                static ResultSetMapper mapper(){}
            }

            Foo.mapper()
        ''')

        rs.next()
        def obj = mapper(rs)

        then:
        obj
        obj.name == 'something'
        obj.number == 42
    }

    def 'implicit mapping in external object'() {
        setup:
        def rs = resultSet {
            columns 'name', 'number'
            data 'something', 42
        }

        when:
        def mapper = shell.evaluate('''
            package testing

            import com.stehno.vanilla.jdbc.mapper.ResultSetMapper
            import com.stehno.vanilla.jdbc.mapper.annotation.InjectResultSetMapper

            class Foo {
                String name
                int number
            }

            class FooMappers {
                @InjectResultSetMapper(Foo)
                static ResultSetMapper mapper(){}
            }

            FooMappers.mapper()
        ''')

        rs.next()
        def obj = mapper(rs)

        then:
        obj
        obj.name == 'something'
        obj.number == 42
    }

    def 'explicit mapper'() {
        setup:
        def person = new Person(name: 'Bob', age: 42, birthDate: new Date())

        def rs = resultSet {
            columns 'name', 'age', 'birth_date', 'bank_pin'
            data person.name, person.age, person.birthDate.format('yyyy-MM-dd'), person.bankPin
            data null, 0, null, null
        }

        when:
        def mapper = shell.evaluate('''
            package testing

            import com.stehno.vanilla.jdbc.mapper.annotation.InjectResultSetMapper
            import com.stehno.vanilla.jdbc.mapper.ResultSetMapper
            import java.time.format.*

            import static com.stehno.vanilla.jdbc.mapper.MappingStyle.EXPLICIT

            class Foo {
                @InjectResultSetMapper(
                    value = com.stehno.vanilla.test.Person,
                    style = EXPLICIT,
                    config = {
                        map 'birthDate' fromDate 'birth_date\'
                        map 'age' from 2 using { a -> a - 5 }
                        map 'name' using { n-> "Name: $n"}
                    }
                )
                static ResultSetMapper createMapper(){}
            }

            Foo.createMapper()
        ''')

        rs.next()
        def obj = mapper(rs)

        rs.next()
        def empty = mapper(rs)

        then:
        assertMatches(
            obj,
            name: "Name: ${person.name}",
            age: person.age - 5,
            birthDate: { act -> person.birthDate.format('yyyy-MM-dd') == act.format('yyyy-MM-dd') },
            bankPin: person.bankPin
        )

        assertMatches(
            empty,
            name: 'Name: null',
            age: -5,
            birthDate: null,
            bankPin: null
        )
    }

    def 'explicit mapper with setter-property'() {
        setup:
        def dummy = new DummyObjectC('Fred', 42, 250.6f)
        dummy.somethingElse = 56 as byte

        def rs = resultSet {
            columns 'name', 'age', 'weight', 'something_else'
            object dummy
        }

        when:
        def mapper = shell.evaluate('''
            package testing

            import com.stehno.vanilla.jdbc.mapper.annotation.InjectResultSetMapper
            import com.stehno.vanilla.jdbc.mapper.ResultSetMapper
            import java.time.format.*
            import com.stehno.vanilla.jdbc.DummyObjectC

            import static com.stehno.vanilla.jdbc.mapper.MappingStyle.EXPLICIT

            class Foo {
                @InjectResultSetMapper(
                    value = DummyObjectC,
                    style = EXPLICIT,
                    config = {
                        map 'name' fromString 'name'
                        map 'age' fromInt 'age'
                        map 'weight' fromFloat 'weight'
                        map 'somethingElse' fromByte 'something_else'
                    }
                )
                static ResultSetMapper createMapper(){}
            }

            Foo.createMapper()
        ''')

        rs.next()
        def obj = mapper(rs)

        then:
        obj == dummy
    }

    def 'nested mappers'() {
        setup:
        def rs = resultSet {
            columns 'id', 'alp_id', 'alp_value'
            data 100, 200, 'something'
        }

        when:
        def mapper = shell.evaluate('''
            package testing
            import com.stehno.vanilla.jdbc.mapper.ResultSetMapper
            import com.stehno.vanilla.jdbc.mapper.annotation.InjectResultSetMapper
            import groovy.transform.ToString

            @ToString
            class Alpha {
                long id
                String value

                @InjectResultSetMapper(Alpha)
                static ResultSetMapper mapper(String prefix=''){}
            }

            @ToString
            class Bravo {
                long id
                Alpha alpha

                @InjectResultSetMapper(value=Bravo, config={
                    map 'alpha' fromMapper Alpha.mapper('alp_')
                })
                static ResultSetMapper mapper(){}
            }

            Bravo.mapper()
        ''')

        rs.next()
        def obj = mapper(rs)

        then:
        obj
        obj.id == 100
        obj.alpha.id == 200
        obj.alpha.value == 'something'
    }

    def 'memoized mappers'() {
        when:
        def mappers = shell.evaluate('''
            package testing

            import com.stehno.vanilla.jdbc.mapper.annotation.InjectResultSetMapper
            import com.stehno.vanilla.jdbc.mapper.ResultSetMapper
            import java.time.format.*
            import com.stehno.vanilla.jdbc.DummyObjectC

            class Alpha {
                String name
                int age

                @InjectResultSetMapper(Alpha)
                static ResultSetMapper createMapper(String prefix=''){}
            }

            [Alpha.createMapper(), Alpha.createMapper(), Alpha.createMapper('a_')]
        ''')

        then:
        mappers[0] == mappers[1]
        mappers[0] != mappers[2]
        mappers[1] != mappers[2]
    }

    def 'mapper immutability'() {
        when:
        def mapper = shell.evaluate('''
            package testing

            import com.stehno.vanilla.jdbc.mapper.annotation.InjectResultSetMapper
            import com.stehno.vanilla.jdbc.mapper.ResultSetMapper
            import java.time.format.*
            import com.stehno.vanilla.jdbc.DummyObjectC

            class Alpha {
                String name
                int age

                @InjectResultSetMapper(Alpha)
                static ResultSetMapper createMapper(String prefix=''){}
            }

            Alpha.createMapper()
        ''')

        mapper.prefix = 'something'

        then:
        thrown(ReadOnlyPropertyException)
    }
}

