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
package com.stehno.vanilla.util

import spock.lang.Specification

import static com.stehno.vanilla.util.Strings.camelCaseToUnderscore
import static com.stehno.vanilla.util.Strings.underscoreToCamelCase

class StringsSpec extends Specification {

    def 'underscore to camel'() {
        expect:
        underscoreToCamelCase(under) == camel

        where:
        under           || camel
        'one'           || 'one'
        'one_two'       || 'oneTwo'
        'one_two_three' || 'oneTwoThree'
    }

    def 'camel to underscore'() {
        expect:
        camelCaseToUnderscore(camel) == under

        where:
        camel         || under
        'one'         || 'one'
        'oneTwo'      || 'one_two'
        'oneTwoThree' || 'one_two_three'
    }
}
