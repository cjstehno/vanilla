/*
 * Copyright (C) 2017 Christopher J. Stehno <chris@stehno.com>
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
package com.stehno.vanilla.jdbc.mapper.runtime

import com.stehno.vanilla.jdbc.DummyObjectA
import com.stehno.vanilla.test.Person
import spock.lang.Specification

import static com.stehno.vanilla.jdbc.mapper.MappingStyle.EXPLICIT
import static com.stehno.vanilla.jdbc.mapper.runtime.RuntimeResultSetMapper.mapper
import static com.stehno.vanilla.test.Assertions.assertMatches
import static com.stehno.vanilla.test.jdbc.mock.ResultSetBuilder.resultSet

class RuntimeResultSetMapperSpec extends Specification {

    def 'mapper: Implicit'() {
        setup:
        def person = new Person(
            name: 'Bob', age: 42, birthDate: new Date()
        )

        def rs = resultSet {
            columns 'name', 'age', 'birth_date', 'bank_pin'
            data person.name, person.age, person.birthDate.format('yyyy-MM-dd'), person.bankPin
        }

        def mapper = mapper(Person) {
            ignore 'bankPin'
            ignore 'pet'
            map 'birthDate' fromDate 'birth_date'
            map 'age' from 2 using { a -> a - 5 }
            map 'name' from 'name'
            ignore 'children'
        }

        when:
        rs.next()
        def obj = mapper(rs)

        then:
        assertMatches(
            obj,
            name: person.name,
            age: person.age - 5,
            birthDate: { act -> person.birthDate.format('yyyy-MM-dd') == act.format('yyyy-MM-dd') },
            bankPin: person.bankPin
        )
    }

    def 'mapper: Implicit (no config)'() {
        setup:
        def dummy = new DummyObjectA('one', 2, 3.14159f)

        def rs = resultSet {
            columns 'alpha', 'bravo', 'charlie'
            object dummy
        }

        def mapper = mapper(DummyObjectA)

        when:
        rs.next()
        def obj = mapper(rs)

        then:
        obj == dummy
    }

    def 'mapper: Implicit (no config) with prefix'() {
        setup:
        def dummy = new DummyObjectA('one', 2, 3.14159f)

        def rs = resultSet {
            columns 'foo_alpha', 'foo_bravo', 'foo_charlie'
            data dummy.alpha, dummy.bravo, dummy.charlie
        }

        def mapper = mapper(DummyObjectA)
        mapper.prefix = 'foo_'

        when:
        rs.next()
        def obj = mapper(rs)

        then:
        obj == dummy
    }

    def 'mapper: Explicit with prefix'() {
        setup:
        def dummy = new DummyObjectA('one', 2, 3.14159f)

        def rs = resultSet {
            columns 'foo_alpha', 'foo_xray', 'foo_charlie'
            data dummy.alpha, dummy.bravo, dummy.charlie
        }

        def mapper = mapper(DummyObjectA, EXPLICIT){
            map 'alpha'
            map 'bravo' from 'xray'
            map 'charlie' from 'charlie'
        }
        mapper.prefix = 'foo_'

        when:
        rs.next()
        def obj = mapper(rs)

        then:
        obj == dummy
    }

    def 'mapper: Implicit (some config)'() {
        setup:
        def dummy = new DummyObjectA('one', 2, 3.14159f)

        def rs = resultSet {
            columns 'alpha', 'bravo', 'charlie'
            object dummy
        }

        def mapper = mapper(DummyObjectA) {
            map 'alpha' from 'charlie' using { x -> x as String }
        }

        when:
        rs.next()
        def obj = mapper(rs)

        then:
        obj == new DummyObjectA('3.14159', 2, 3.14159f)
    }

    def 'mapper: Explicit'() {
        setup:
        def person = new Person(
            name: 'Bob', age: 42, birthDate: new Date()
        )

        def rs = resultSet {
            columns 'name', 'age', 'birth_date', 'bank_pin'
            data person.name, person.age, person.birthDate.format('yyyy-MM-dd'), person.bankPin
        }

        def mapper = mapper(Person, EXPLICIT) {
            map 'birthDate' fromDate 'birth_date'
            map 'age' from 2 using { a -> a - 5 }
            map 'name' using { n -> "Name: $n" }
        }

        when:
        rs.next()
        def obj = mapper(rs)

        then:
        assertMatches(
            obj,
            name: "Name: ${person.name}",
            age: person.age - 5,
            birthDate: { act -> person.birthDate.format('yyyy-MM-dd') == act.format('yyyy-MM-dd') },
            bankPin: person.bankPin
        )
    }

    def 'nested mappers'() {
        setup:
        def rs = resultSet {
            columns 'id', 'alp_id', 'alp_value'
            data 100, 200, 'something'
        }

        when:
        def mapper = new GroovyShell().evaluate('''
            package testing
            import com.stehno.vanilla.jdbc.mapper.ResultSetMapper
            import com.stehno.vanilla.jdbc.mapper.runtime.RuntimeResultSetMapper
            import com.stehno.vanilla.jdbc.mapper.annotation.InjectResultSetMapper
            import groovy.transform.ToString

            class Alpha {
                long id
                String value

                static ResultSetMapper mapper(String prefix=''){
                    def m = RuntimeResultSetMapper.mapper(Alpha)
                    m.prefix = prefix
                    m
                }
            }

            class Bravo {
                long id
                Alpha alpha

                static ResultSetMapper mapper(){
                    RuntimeResultSetMapper.mapper(Bravo){
                        map 'alpha' fromMapper Alpha.mapper('alp_')
                    }
                }
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
}

