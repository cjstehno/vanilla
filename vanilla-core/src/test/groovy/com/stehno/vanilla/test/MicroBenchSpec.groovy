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
package com.stehno.vanilla.test

import spock.lang.Specification

class MicroBenchSpec extends Specification {

    private final MicroBench bench = new MicroBench()

    def 'bench'() {
        setup:
        bench.iterations 100

        int count = 2
        String[] items

        bench.prepare {
            items = new String[count]
            count.times { n->
                items[n] = "some_value_$n".toString()
            }
        }

        bench.test {
            Set<String> data = new HashSet<String>()
            for( final String s : items ){
                data.add(s)
            }
            assert data.size() == count
        }

        when:
        String report = bench.run()

        then:
        report.startsWith('Ran 100 in ')
        report.endsWith(' /ms)')
    }
}
