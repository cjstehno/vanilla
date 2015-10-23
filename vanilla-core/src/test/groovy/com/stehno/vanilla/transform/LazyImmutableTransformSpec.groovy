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
package com.stehno.vanilla.transform

import groovy.transform.Canonical
import org.junit.Rule
import spock.lang.Specification

class LazyImmutableTransformSpec extends Specification {

    @Rule GroovyShellEnvironment shell

    def 'simple original object usage'() {
        when:
        def results = shell.evaluate('''
            package testing
            import groovy.transform.Canonical
            import com.stehno.vanilla.annotation.LazyImmutable
            @LazyImmutable @Canonical
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

        assertPerson results[0], name: 'Larry', age: 42
        assertPerson results[1], name: 'Moe', age: 50

        !results[2].name
        !results[2].age
    }

    def 'simple immutable object usage'() {
        when:
        def results = shell.evaluate('''
            package testing
            import groovy.transform.Canonical
            import com.stehno.vanilla.annotation.LazyImmutable
            @LazyImmutable @Canonical
            class Person {
                String name
                int age
            }

            def moe = new Person('Moe',53)
            def immutable = moe.asImmutable()
            [moe, immutable, immutable.asMutable()]
        ''')

        results.eachWithIndex { res, idx ->
            res.age = res.age + idx + 1
        }

        then:
        results.size() == 3

        assertPerson results[0], name: 'Moe', age: 54
        assertPerson results[1], name: 'Moe', age: 53
        assertPerson results[2], name: 'Moe', age: 56
    }

    def 'simple usage with builder'() {
        when:
        def results = shell.evaluate('''
            package testing
            import groovy.transform.builder.Builder
            import com.stehno.vanilla.annotation.LazyImmutable
            @LazyImmutable @Builder
            class Person {
                String name
                int age
            }

            def moe = Person.builder().name('Moe').age(42).build()
            def immutable = moe.asImmutable()
            [moe, immutable, immutable.asMutable()]
        ''')

        then:
        results.size() == 3

        assertPerson results[0], name: 'Moe', age: 42
        assertPerson results[1], name: 'Moe', age: 42
        assertPerson results[2], name: 'Moe', age: 42
    }

    def 'immutable with known immutable (class)'() {
        when:
        def results = shell.evaluate('''
            package testing

            import groovy.transform.Canonical
            import com.stehno.vanilla.annotation.LazyImmutable
            import com.stehno.vanilla.transform.SomeImmutable

            @Canonical
            @LazyImmutable(knownImmutableClasses=[SomeImmutable])
            class Person {
                String name
                int age
                SomeImmutable value
            }

            def moe = new Person('Moe',53, new SomeImmutable(86))
            def immutable = moe.asImmutable()
            [moe, immutable, immutable.asMutable()]
        ''')

        then:
        results.size() == 3

        assertPerson results[0], name: 'Moe', age: 53, value: new SomeImmutable(86)
        assertPerson results[1], name: 'Moe', age: 53, value: new SomeImmutable(86)
        assertPerson results[2], name: 'Moe', age: 53, value: new SomeImmutable(86)
    }

    def 'immutable with known immutable (property)'() {
        when:
        def results = shell.evaluate('''
            package testing

            import groovy.transform.Canonical
            import com.stehno.vanilla.annotation.LazyImmutable
            import com.stehno.vanilla.transform.SomeImmutable

            @Canonical
            @LazyImmutable(knownImmutables=['value'])
            class Person {
                String name
                int age
                SomeImmutable value
            }

            def moe = new Person('Moe',53, new SomeImmutable(86))
            def immutable = moe.asImmutable()
            [moe, immutable, immutable.asMutable()]
        ''')

        then:
        results.size() == 3

        assertPerson results[0], name: 'Moe', age: 53, value: new SomeImmutable(86)
        assertPerson results[1], name: 'Moe', age: 53, value: new SomeImmutable(86)
        assertPerson results[2], name: 'Moe', age: 53, value: new SomeImmutable(86)
    }

    def 'copyWith usage'() {
        when:
        def (moe, immutable, mutable) = shell.evaluate('''
            package testing
            import groovy.transform.Canonical
            import com.stehno.vanilla.annotation.LazyImmutable
            @Canonical @LazyImmutable(copyWith=true)
            class Person {
                String name
                int age
            }

            def moe = new Person('Moe',53)
            def immutable = moe.asImmutable()
            [moe, immutable, immutable.asMutable()]
        ''')

        moe = moe.copyWith(name: 'Bob', age: 42)
        immutable = immutable.copyWith(name: 'Blaine', age: 23)
        mutable = mutable.copyWith(name: 'Claire', age: 21)

        then:
        assertPerson moe, name: 'Bob', age: 42
        assertPerson immutable, name: 'Blaine', age: 23
        assertPerson mutable, name: 'Claire', age: 21
    }

    def 'equality'() {
        when:
        def (moe, immutable, mutable) = shell.evaluate('''
            package testing
            import groovy.transform.EqualsAndHashCode
            import com.stehno.vanilla.annotation.LazyImmutable
            @LazyImmutable @EqualsAndHashCode
            class Person {
                String name
                int age
            }

            def moe = new Person(name:'Moe', age:65)
            def immutable = moe.asImmutable()
            [moe, immutable, immutable.asMutable()]
        ''')

        then:
        moe == mutable
        moe == immutable
        immutable != mutable // TODO: should this be changed, or does it really matter?
    }

    private static boolean assertPerson(Map attrs, object) {
        attrs.every { k, v ->
            object[k] == v
        }
    }
}

@Canonical
class SomeImmutable {

    private final int value

    SomeImmutable(final int value) {
        this.value = value
    }

    int getValue() {
        return value
    }
}