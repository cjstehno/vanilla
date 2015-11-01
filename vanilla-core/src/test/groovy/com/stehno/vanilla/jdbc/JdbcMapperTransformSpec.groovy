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

import com.stehno.vanilla.mapper.BarObject
import com.stehno.vanilla.mapper.FooObject
import com.stehno.vanilla.test.PropertyRandomizer
import com.stehno.vanilla.transform.GroovyShellEnvironment
import org.junit.Rule
import spock.lang.Specification

import java.time.LocalDate

import static com.stehno.vanilla.test.PropertyRandomizer.randomize

/**
 * FIXME: document me
 */
class JdbcMapperTransformSpec extends Specification {

    @Rule GroovyShellEnvironment shell

    private BarObject bar = new BarObject()
    private PropertyRandomizer rando = randomize(FooObject) {
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

            import com.stehno.vanilla.jdbc.JdbcMapper
            import com.stehno.vanilla.mapper.ObjectMapper
            import java.time.format.*

            class Foo {
                @JdbcMapper({
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
}
