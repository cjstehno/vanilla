package com.stehno.vanilla.test

import groovy.transform.CompileStatic

/**
 * Simple code micro-benchmark tool for use as a Groovy script.
 */
@CompileStatic
abstract class MicroBenchScript extends Script {

    @Delegate final MicroBench bench = new MicroBench()

    abstract def runScript()

    def run() {
        runScript()

        bench.run()
    }
}