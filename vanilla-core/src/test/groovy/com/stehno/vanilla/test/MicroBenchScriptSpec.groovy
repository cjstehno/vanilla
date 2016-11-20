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
package com.stehno.vanilla.test

import spock.lang.Specification

class MicroBenchScriptSpec extends Specification {

    private final GroovyShell shell = new GroovyShell()

    def 'benchmark script'() {
        when:
        String report = shell.evaluate '''
            import groovy.transform.BaseScript
            import com.stehno.vanilla.test.MicroBenchScript

            @BaseScript MicroBenchScript bench

            iterations 100

            count = 2

            prepare {
                items = new String[count]
                count.times { n->
                    items[n] = "some_value_$n".toString()
                }
            }

            test {
                Set<String> data = new HashSet<String>()
                for( final String s : items ){
                    data.add(s)
                }
                assert data.size() == count
            }
        '''

        then:
        report.startsWith('Ran 100 in ')
        report.endsWith(' /ms)')
    }
}
