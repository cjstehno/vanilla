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
