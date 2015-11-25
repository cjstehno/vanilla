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
package com.stehno.vanilla.jdbc
import com.stehno.vanilla.test.Person
import com.stehno.vanilla.transform.GroovyShellEnvironment
import org.junit.Rule
import spock.lang.Specification

import static com.stehno.vanilla.test.jdbc.ResultSetBuilder.resultSet

class JdbcMapperTransformSpec extends Specification {

    @Rule GroovyShellEnvironment shell

    // FIXME: test without implicit (no value)
    // FIXME: test with explicit
    // FIXME: test with no DSL and implicit
    // FIXME: test with no DSL and explicit (error)
    // FIXME: test with POGO having setters rather than properties

    // FIXME: make this (and the one in dyn mapper) use randomizer

    def 'implicit mapper'() {
        setup:
        def person = new Person(
            name: 'Bob', age: 42, birthDate: new Date()
        )

        def rs = resultSet {
            columns 'name', 'age', 'birth_date', 'bank_pin'
            object person
        }

        when:
        def mapper = shell.evaluate('''
            package testing

            import com.stehno.vanilla.jdbc.JdbcMapper
            import com.stehno.vanilla.jdbc.ResultSetMapper
            import java.time.format.*

            import static com.stehno.vanilla.jdbc.MappingStyle.IMPLICIT

            class Foo {
                @JdbcMapper(
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

        then:
        obj == new Person(
            name: 'Bob', age: 37, birthDate: person.birthDate
        )
    }

    def 'explicit mapper'() {
        setup:
        def person = new Person(
            name: 'Bob', age: 42, birthDate: new Date()
        )

        def rs = resultSet {
            columns 'name', 'age', 'birth_date', 'bank_pin'
            object person
        }

        when:
        def mapper = shell.evaluate('''
            package testing

            import com.stehno.vanilla.jdbc.JdbcMapper
            import com.stehno.vanilla.jdbc.ResultSetMapper
            import java.time.format.*

            import static com.stehno.vanilla.jdbc.MappingStyle.EXPLICIT

            class Foo {
                @JdbcMapper(
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

        then:
        obj == new Person(
            name: 'Name: Bob', age: 37, birthDate: person.birthDate
        )
    }
}
