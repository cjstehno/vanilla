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

package com.stehno.vanilla.jdbc
import com.stehno.vanilla.test.Person
import spock.lang.Specification

import static com.stehno.vanilla.test.jdbc.ResultSetBuilder.resultSet

class ResultSetMapperFactorySpec extends Specification {

    def 'dsl'() {
        setup:
        def person = new Person(
            name: 'Bob', age: 42, birthDate: new java.util.Date()
        )

        def rs = resultSet {
            columns 'name', 'age', 'birth_date', 'bank_pin'
            object person
        }

        def mapper = ResultSetMapperFactory.dsl(Person) {
            ignore 'bankPin'
            ignore 'pet'
            map 'birthDate' fromDate 'birth_date'
            map 'age'
            map 'name' fromString 'name'
            ignore 'children'
        }

        when:
        rs.next()
        def obj = mapper(rs)

        then:
        obj == person
    }
}

