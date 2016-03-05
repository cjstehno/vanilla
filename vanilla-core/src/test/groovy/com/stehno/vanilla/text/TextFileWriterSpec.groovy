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
package com.stehno.vanilla.text

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class TextFileWriterSpec extends Specification {

    @Rule public TemporaryFolder folder = new TemporaryFolder()

    def 'writer'(){
        setup:
        File file = folder.newFile()

        TextFileWriter writer = new TextFileWriter(
            lineFormatter: new CommaSeparatedLineFormatter(),
            filePath: file
        )

        when:
        writer.writeComment('This is a header')
        writer.write(['a','b','c'])
        writer.write(['d','e','f'] as Object[])
        writer.writeComment('This is a footer')

        then:
        def lines = file.readLines()
        lines.size() == 4
        lines[0] == '# This is a header'
        lines[1] == 'a,b,c'
        lines[2] == 'd,e,f'
        lines[3] == '# This is a footer'
    }
}
