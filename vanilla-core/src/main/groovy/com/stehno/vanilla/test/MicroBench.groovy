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

import groovy.transform.CompileStatic

/**
 * Micro benchmark utility class, providing a means of preparing a data set and running a code block through a specified number
 * of iterations. Benchmarking in this manner is generally meant for testing the relative performance difference between two or
 * more alternate code blocks over a known set of conditions - it is NOT meant for general performance testing.
 */
@CompileStatic
class MicroBench {

    private int iterations = 100_000_000
    private Closure preparation = {}
    private Closure testing

    /**
     * Used to specify the number of iterations.
     *
     * @param iter
     */
    void iterations(int iter) {
        this.iterations = iter
    }

    /**
     * Used to prepare any data to be used during the performance testing.
     *
     * @param closure
     */
    void prepare(Closure closure) {
        this.preparation = closure
    }

    /**
     * Used to specify the test operations - the closure will be run once for every iteration.
     *
     * @param closure
     */
    void test(Closure closure) {
        this.testing = closure
    }

    String run(){
        preparation.call()

        long started = System.currentTimeMillis()

        for (int i = 0; i < iterations; i++) {
            testing.call()
        }

        report(System.currentTimeMillis() - started)
    }

    private String report(final long elapsed) {
        "Ran $iterations in $elapsed ms (${iterations / elapsed} /ms)"
    }
}
