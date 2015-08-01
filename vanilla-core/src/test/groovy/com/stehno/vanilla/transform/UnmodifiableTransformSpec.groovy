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

package com.stehno.vanilla.transform

import org.junit.Rule
import spock.lang.Specification

class UnmodifiableTransformSpec extends Specification {

    @Rule GroovyShellEnvironment shell

    def 'simple original object usage'() {
        when:
        def results = shell.evaluate('''
            package testing
            import groovy.transform.Canonical
            import com.stehno.vanilla.annotation.Unmodifiable
            @Unmodifiable @Canonical
            class Person {
                String name
                int age
            }

            def moe = new Person('Moe',53)
            moe.age = 50

            [ new Person('Larry', 42), moe, new Person() ]
        ''')

        then:
        results.size() == 3

        results[0].name == 'Larry'
        results[0].age == 42

        results[1].name == 'Moe'
        results[1].age == 50

        !results[2].name
        !results[2].age
    }

    def 'simple immutable object usage'() {
        when:
        def results = shell.evaluate('''
            package testing
            import groovy.transform.Canonical
            import com.stehno.vanilla.annotation.Unmodifiable
            @Unmodifiable @Canonical
            class Person {
                String name
                int age
            }

            def moe = new Person('Moe',53)
            def immutable = moe.asImmutable()
            [moe, immutable, immutable.asMutable()]
        ''')

        results.eachWithIndex{ res, idx ->
            res.age = res.age + idx + 1
        }

        then:
        results.size() == 3

        results[0].name == 'Moe'
        results[0].age == 54

        results[1].name == 'Moe'
        results[1].age == 53

        results[2].name == 'Moe'
        results[2].age == 56
    }

    // FIXME: there is an issue with typed collections

    def 'simple immutable object usage with collection'() {
        when:
        def results = shell.evaluate('''
            package testing
            import groovy.transform.Canonical
            import com.stehno.vanilla.annotation.Unmodifiable
            @Canonical @Unmodifiable(knownImmutables=['pets'])
            class Person {
                String name
                int age
                def pets = []
            }

            def moe = new Person('Moe',53, ['Fido'])
            def immutable = moe.asImmutable()
            [moe, immutable, immutable.asMutable()]
        ''')

        results.eachWithIndex{ res, idx ->
            res.age = res.age + idx + 1
        }

        then:
        results.size() == 3

        results[0].name == 'Moe'
        results[0].age == 54

        results[1].name == 'Moe'
        results[1].age == 53

        results[2].name == 'Moe'
        results[2].age == 56
    }
}


