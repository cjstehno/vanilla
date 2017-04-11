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
package com.stehno.vanilla.text;

import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Path
import java.nio.file.Paths;

class TextFileReaderSpec extends Specification {

    public static final URI DUMMY_URI = new URI('file:///foo/bar.txt')

    @Unroll
    def 'path setters (set#type)'() {
        setup:
        Path expected = Paths.get('/foo').resolve('bar.txt')

        expect:
        new TextFileReader(filePath: path).filePath.toAbsolutePath() == expected.toAbsolutePath()

        where:
        type   | path
        'Path' | Paths.get(DUMMY_URI)
        'Uri'  | DUMMY_URI
        'Url'  | DUMMY_URI.toURL()
        'File' | new File('/foo/bar.txt')
    }

    def 'eachLine: no file'() {
        setup:
        TextFileReader reader = new TextFileReader(
            filePath: new File('non-existing.txt'),
            lineParser: new CommaSeparatedLineParser()
        )

        when:
        reader.eachLine { Object[] data -> }

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == 'The file (non-existing.txt) does not exist or is not readable.'
    }

    def 'eachLine'() {
        setup:
        TextFileReader reader = new TextFileReader(
            filePath: TextFileReaderSpec.getResource('/text-file-a.txt'),
            lineParser: new CommaSeparatedLineParser(),
            firstLine: 2
        )

        when:
        def lines = []

        reader.eachLine { Object[] data ->
            lines << data
        }

        then:
        lines.size() == 4

        lines[0] == ['alpha', '1', 'The number one']
        lines[1] == ['bravo', '2', 'The number two']
        lines[2] == ['delta', '4', 'The number four']
        lines[3] == ['echo', '5', 'The number five']
    }

    def 'readLines'() {
        setup:
        TextFileReader reader = new TextFileReader(
            filePath: TextFileReaderSpec.getResource('/text-file-a.txt'),
            lineParser: new CommaSeparatedLineParser(),
            firstLine: 2
        )

        when:
        def lines = reader.readLines()

        then:
        lines.size() == 4

        lines[0] == ['alpha', '1', 'The number one']
        lines[1] == ['bravo', '2', 'The number two']
        lines[2] == ['delta', '4', 'The number four']
        lines[3] == ['echo', '5', 'The number five']
    }

    def 'eachLine: with converters'() {
        setup:
        TextFileReader reader = new TextFileReader(
            filePath: TextFileReaderSpec.getResource('/text-file-a.txt'),
            lineParser: new CommaSeparatedLineParser(
                (1): { v -> (v as Long) * 2 }
            ),
            firstLine: 2
        )

        when:
        def lines = []

        reader.eachLine { Object[] data ->
            lines << data
        }

        then:
        lines.size() == 4

        lines[0] == ['alpha', 2L, 'The number one']
        lines[1] == ['bravo', 4L, 'The number two']
        lines[2] == ['delta', 8L, 'The number four']
        lines[3] == ['echo', 10L, 'The number five']
    }
}