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
package com.stehno.vanilla.io

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class DefaultRolloverFileProviderSpec extends Specification {

    @Rule public TemporaryFolder folder = new TemporaryFolder()

    def 'provide: no directory specified, not compressed'() {
        setup:
        def provider = new DefaultRolloverFileProvider()
        File file = folder.newFile()

        when:
        File provided = provider.provide(file, false)

        then:
        provided.name.startsWith(file.name - '.tmp' + '-')
        provided.name.endsWith('.tmp')
    }

    def 'provide: no directory specified, compressed'() {
        setup:
        def provider = new DefaultRolloverFileProvider()
        File file = folder.newFile()

        when:
        File provided = provider.provide(file, true)

        then:
        provided.name.startsWith(file.name - '.tmp' + '-')
        provided.name.endsWith('.tmp.gz')
    }

    def 'provide: directory specified, not compressed'() {
        setup:
        File directory = folder.newFolder()
        def provider = new DefaultRolloverFileProvider(directory: directory)
        File file = folder.newFile()

        when:
        File provided = provider.provide(file, false)

        then:
        provided.name.startsWith(file.name - '.tmp' + '-')
        provided.name.endsWith('.tmp')
        provided.parent == directory.path
    }

    def 'provide: directory specified, compressed'() {
        setup:
        File directory = folder.newFolder()
        def provider = new DefaultRolloverFileProvider(directory: directory)
        File file = folder.newFile()

        when:
        File provided = provider.provide(file, true)

        then:
        provided.name.startsWith(file.name - '.tmp' + '-')
        provided.name.endsWith('.tmp.gz')
        provided.parent == directory.path
    }
}
