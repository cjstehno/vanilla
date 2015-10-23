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
package com.stehno.vanilla

import spock.lang.Specification

class AffirmationsSpec extends Specification {

    def 'affirm(negative): one-arg'(){
        when:
        Affirmations.affirm(false)

        then:
        def ex = thrown(IllegalArgumentException)
        !ex.message
    }

    def 'affirm(negative): two-arg'(){
        when:
        Affirmations.affirm(false, 'An exception')

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == 'An exception'
    }

    def 'affirm(negative): two-arg (no message)'(){
        when:
        Affirmations.affirm(false, IllegalStateException)

        then:
        def ex = thrown(IllegalStateException)
        !ex.message
    }

    def 'affirm(negative): three-arg'(){
        when:
        Affirmations.affirm(false, 'Another exception', IllegalStateException)

        then:
        def ex = thrown(IllegalStateException)
        ex.message == 'Another exception'
    }

    def 'affirm(positive): one-arg'(){
        when:
        Affirmations.affirm(true)

        then:
        notThrown(IllegalArgumentException)
    }

    def 'affirm(positive): two-arg'(){
        when:
        Affirmations.affirm(true, 'Should not see')

        then:
        notThrown(IllegalArgumentException)
    }

    def 'affirm(positive): two-arg (no message)'(){
        when:
        Affirmations.affirm(true, IllegalStateException)

        then:
        notThrown(IllegalStateException)
    }

    def 'affirm(positive): three-arg'(){
        when:
        Affirmations.affirm(true, 'Should not see', IllegalStateException)

        then:
        notThrown(IllegalStateException)
    }

    def 'affirmNot(negative): one-arg'(){
        when:
        Affirmations.affirmNot(true)

        then:
        def ex = thrown(IllegalArgumentException)
        !ex.message
    }

    def 'affirmNot(negative): two-arg'(){
        when:
        Affirmations.affirmNot(true, 'An exception')

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == 'An exception'
    }

    def 'affirmNot(negative): two-arg (no message)'(){
        when:
        Affirmations.affirmNot(true, IllegalStateException)

        then:
        def ex = thrown(IllegalStateException)
        !ex.message
    }

    def 'affirmNot(negative): three-arg'(){
        when:
        Affirmations.affirmNot(true, 'Another exception', IllegalStateException)

        then:
        def ex = thrown(IllegalStateException)
        ex.message == 'Another exception'
    }

    def 'affirmNot(positive): one-arg'(){
        when:
        Affirmations.affirmNot(false)

        then:
        notThrown(IllegalArgumentException)
    }

    def 'affirmNot(positive): two-arg'(){
        when:
        Affirmations.affirmNot(false, 'Should not see')

        then:
        notThrown(IllegalArgumentException)
    }

    def 'affirmNot(positive): two-arg (no message)'(){
        when:
        Affirmations.affirmNot(false, IllegalStateException)

        then:
        notThrown(IllegalStateException)
    }

    def 'affirmNot(positive): three-arg'(){
        when:
        Affirmations.affirmNot(false, 'Should not see', IllegalStateException)

        then:
        notThrown(IllegalStateException)
    }
}
