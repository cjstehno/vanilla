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
package com.stehno.vanilla.test.jdbc

import spock.lang.Specification
import spock.lang.Unroll

import java.sql.SQLException

import static java.nio.charset.StandardCharsets.US_ASCII

class MockClobSpec extends Specification {

    public static final String STRING = 'this is a test, only a test.'
    private MockClob clob = new MockClob(STRING.toCharArray())

    // FIXME: test the exception cases

    def 'length & truncate'() {
        when:
        int len = clob.length()

        then:
        len == STRING.length()

        when:
        clob.truncate(8)
        len = clob.length()

        then:
        len == 8
    }

    @Unroll
    def 'getSubString(#pos, #len)'() {
        expect:
        clob.getSubString(pos, len) == sub

        where:
        pos | len           || sub
        1   | STRING.size() || STRING
        4   | 5             || 's is '
        21  | 8             || ' a test.'
    }

    def 'position: String'() {
        expect:
        clob.position('test', start) == pos

        where:
        start           || pos
        1               || 11
        2               || 11
        4               || 11
        STRING.length() || -1
    }

    def 'position: Clob'() {
        setup:
        def other = new MockClob('only')

        when:
        long pos = clob.position(other, 2)

        then:
        pos == 17
    }

    def 'characterStream'() {
        when:
        def reader = clob.characterStream

        then:
        reader.text == STRING
    }

    def 'characterStream: freed'() {
        when:
        clob.free()
        clob.characterStream

        then:
        def ex = thrown(SQLException)
        ex.message == 'The clob has been freed and is no longer valid.'
    }

    def 'getCharacterStream(long, long)'() {
        when:
        def reader = clob.getCharacterStream(4, 10)

        then:
        reader.text == 's is a tes'
    }

    def 'asciiStream'() {
        when:
        def stream = clob.asciiStream

        then:
        stream.bytes == STRING.getBytes(US_ASCII)
    }

    def 'setString'() {
        when:
        int len = clob.setString(5, 'xxxxx')

        then:
        len == 5
        clob.toString() == 'thisxxxxx'
    }

    def 'setString: with offset'() {
        when:
        int len = clob.setString(8, 'abcdefg', 2, 2)

        then:
        len == 2
        clob.toString() == 'this iscd'
    }

    def 'setAsciiStream'() {
        when:
        clob.setAsciiStream(7).withStream { outs ->
            outs.write('hello'.bytes)
        }

        then:
        clob.toString() == 'this ihello'
    }

    def 'setCharacterStream'() {
        when:
        clob.setCharacterStream(9).withWriter { outs ->
            outs.write('hey'.toCharArray())
        }

        then:
        clob.toString() == 'this is hey'
    }
}
