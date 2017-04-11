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
package com.stehno.vanilla.config

import com.stehno.vanilla.util.TimeSpan
import spock.lang.Specification
import spock.lang.Unroll

import static com.stehno.vanilla.util.TimeSpanUnit.HOURS

class MapConfigurationSourceSpec extends Specification {

    private final ConfigurationSource source = new MapConfigurationSource([
        alpha      : 'hello',
        bravo      : '101',
        charlie    : '98767',
        delta      : '123.45',
        echo       : '3456.234',
        foxtrot    : 'true',
        golf       : 'false',
        'hotel.a'  : '100',
        'hotel.b'  : 'something',
        'hotel.c'  : '345.754',
        'hotel.d.e': 'down deep'
    ])

    @Unroll def 'simple property (#type) with null default'() {
        expect:
        source."get$type"(key) == result

        where:
        type      | key       || result
        'String'  | 'alpha'   || 'hello'
        'Int'     | 'bravo'   || 101
        'Long'    | 'charlie' || 98767
        'Float'   | 'delta'   || 123.45f
        'Double'  | 'echo'    || 3456.234d
        'Boolean' | 'foxtrot' || true
        'Boolean' | 'golf'    || false
    }

    @Unroll def 'simple property (#type) with specified default'() {
        expect:
        source."get$type"('nothing', defaultVal) == defaultVal

        where:
        type       || defaultVal
        'String'   || 'other'
        'Int'      || 211
        'Long'     || 12345L
        'Float'    || 3.1415f
        'Double'   || 6.1234d
        'Boolean'  || false
        'Boolean'  || true
        'TimeSpan' || new TimeSpan(1, HOURS)
        'File'     || new File('/other.bin')
        'Path'     || new File('/some.dat').toPath()
    }

    def 'getMap'() {
        expect:
        source.getMap('hotel') == [a: '100', b: 'something', c: '345.754', 'd.e': 'down deep']
    }

    def 'getConfiguration'() {
        when:
        def config = source.getConfiguration('hotel')

        then:
        config.getString('a') == '100'
        config.getString('b') == 'something'
        config.getString('c') == '345.754'
        config.getString('d.e') == 'down deep'
    }
}
