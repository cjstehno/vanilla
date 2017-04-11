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
package com.stehno.vanilla.matcher

import com.stehno.vanilla.test.Pet
import spock.lang.Specification

class ObjectMatcherSpec extends Specification {

    def 'matcher'() {
        setup:
        def matcher = ObjectMatcher.matcher(Pet) {
            propertyMatchers([
                name: { o -> o },
                age : { o -> o > 1 && o < 15 }
            ])
        }

        expect:
        matcher.matches(object) == matches

        where:
        object                           || matches
        new Pet(name: 'Fluffy', age: 12) || true
        new Pet(age: 12)                 || false
    }

    def 'score'() {
        setup:
        def matcher = ObjectMatcher.matcher(Pet) {
            propertyMatchers([
                name: { o -> o },
                age : { o -> o > 1 && o < 15 }
            ])
        }

        expect:
        matcher.score(object) == score

        where:
        object                           || score
        new Pet(name: 'Fluffy', age: 12) || 1.0f
        new Pet(age: 12)                 || 0.5f
    }
}
