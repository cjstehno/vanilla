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
package com.stehno.vanilla.config

import spock.lang.Specification

class CompositeConfigurationSourceSpec extends Specification {

    CompositeConfigurationSource config = new CompositeConfigurationSource([
        new MapConfigurationSource([
            alpha: 'one',
            bravo: 'two'
        ]),
        new MapConfigurationSource([
            alpha  : 'three',
            charlie: 'four'
        ])
    ])

    def 'composite configuration'() {
        expect:
        config.getString(key, defaultVal) == result

        where:
        key       | defaultVal || result
        'delta'   | null       || null
        'delta'   | 'zero'     || 'zero'
        'alpha'   | null       || 'one'
        'bravo'   | null       || 'two'
        'charlie' | null       || 'four'
        'charlie' | 'five'     || 'four'
    }

    def 'inject new source'(){
        setup:
        config.insertSource(new MapConfigurationSource([delta:'x', charlie:'y']), 1)

        expect:
        config.getString(key, defaultVal) == result

        where:
        key       | defaultVal || result
        'delta'   | null       || 'x'
        'delta'   | 'zero'     || 'x'
        'alpha'   | null       || 'one'
        'bravo'   | null       || 'two'
        'charlie' | null       || 'y'
        'charlie' | 'five'     || 'y'
    }
}
