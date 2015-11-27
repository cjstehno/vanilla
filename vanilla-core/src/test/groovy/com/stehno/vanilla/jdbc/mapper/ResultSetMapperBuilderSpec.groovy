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
package com.stehno.vanilla.jdbc.mapper

import com.stehno.vanilla.test.Person
import spock.lang.Specification

import static com.stehno.vanilla.jdbc.mapper.ResultSetMapperBuilder.mapper
import static com.stehno.vanilla.jdbc.mapper.MappingStyle.EXPLICIT
import static com.stehno.vanilla.test.jdbc.ResultSetBuilder.resultSet

class ResultSetMapperBuilderSpec extends Specification {

    def 'mapper: Implicit'() {
        setup:
        def person = new Person(
            name: 'Bob', age: 42, birthDate: new Date()
        )

        def rs = resultSet {
            columns 'name', 'age', 'birth_date', 'bank_pin'
            object person
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
        obj == new Person(
            name: 'Bob', age: 37, birthDate: person.birthDate
        )
    }

    def 'mapper: Explicit'() {
        setup:
        def person = new Person(
            name: 'Bob', age: 42, birthDate: new Date()
        )

        def rs = resultSet {
            columns 'name', 'age', 'birth_date', 'bank_pin'
            object person
        }

        def mapper = mapper(Person, EXPLICIT) {
            map 'birthDate' fromDate 'birth_date'
            map 'age' from 2 using { a -> a - 5 }
            map 'name' using { n-> "Name: $n"}
        }

        when:
        rs.next()
        def obj = mapper(rs)

        then:
        obj == new Person(
            name: 'Name: Bob', age: 37, birthDate: person.birthDate
        )
    }
}
