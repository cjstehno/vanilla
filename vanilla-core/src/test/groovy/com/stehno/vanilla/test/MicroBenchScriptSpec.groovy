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
