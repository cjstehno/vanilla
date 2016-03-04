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
