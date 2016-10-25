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
package com.stehno.vanilla.transform

import spock.lang.Specification

class OnceTransformSpec extends Specification {

    private final GroovyShell shell = new GroovyShell()

    def 'one ping only vasily'() {
        when:
        def result = shell.evaluate('''
            package testing
            import com.stehno.vanilla.annotation.Once

            class Submarine {
                int pings = 0
                @Once void ping(){
                    pings++
                }
            }

            def redOctober = new Submarine()
            redOctober.ping()
            redOctober.ping()
            redOctober.ping()
            redOctober
        ''')

        then:
        result.pings == 1
        result.pingCalled
        result.isPingCalled()
    }

    def 'a ping by any other name'() {
        when:
        def result = shell.evaluate('''
            package testing
            import com.stehno.vanilla.annotation.Once

            class Submarine {
                int pings = 0
                @Once('pinged') void ping(){
                    pings++
                }
            }

            def redOctober = new Submarine()
            redOctober.ping()
            redOctober.ping()
            redOctober.ping()
            redOctober
        ''')

        then:
        result.pings == 1
        result.pinged
        result.isPinged()
    }

    def 'exactly one ping only vasily'() {
        setup:
        def result = shell.evaluate('''
            package testing
            import com.stehno.vanilla.annotation.Once

            class Submarine {
                int pings = 0
                @Once(ignoreMultipleCalls=false) void ping(){
                    pings++
                }
            }

            new Submarine()
        ''')

        when:
        result.ping()
        result.ping()

        then:
        thrown(IllegalStateException)
    }
}
