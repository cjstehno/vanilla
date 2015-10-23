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
package com.stehno.vanilla.transform

import org.junit.rules.ExternalResource

/**
 * JUnit Rule for creating/destroying a GroovyShell for each test.
 *
 * Mostly lifted from `groovy.util.GroovyShellTestCase`.
 */
class GroovyShellEnvironment extends ExternalResource {

    @Delegate GroovyShell shell

    @Override
    protected void before() throws Throwable {
        shell = createNewShell()
    }

    @Override
    protected void after() {
        shell = null
    }

    /**
     * Create new shell instance.
     * Overwrite it to customize
     */
    GroovyShell createNewShell() {
        return new GroovyShell()
    }

    /**
     * Executes closure with given binding
     */
    def withBinding(Map map, Closure closure) {
        Binding binding = shell.context
        Map bmap = binding.variables
        try {
            Map vars = new HashMap(bmap)
            bmap.putAll map

            return closure.call()
        }
        finally {
            bmap.clear()
            bmap.putAll vars
        }
    }

    /**
     * Evaluates script with given binding
     */
    def withBinding(Map map, String script) {
        Binding binding = shell.context
        Map bmap = binding.variables
        try {
            Map vars = new HashMap(bmap)
            bmap.putAll map

            return evaluate(script)
        }
        finally {
            bmap.clear()
            bmap.putAll vars
        }
    }
}
