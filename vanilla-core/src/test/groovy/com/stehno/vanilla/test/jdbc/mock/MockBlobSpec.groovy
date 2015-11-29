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
package com.stehno.vanilla.test.jdbc.mock

import spock.lang.Specification

class MockBlobSpec extends Specification {

    private static final byte[] BYTES = [11, 66, 42, 97, 111, 115, 104, 106, 118, 112] as byte[]
    private MockBlob blob = new MockBlob(BYTES)

    def 'length & truncate'() {
        when:
        int len = blob.length()

        then:
        len == BYTES.length

        when:
        blob.truncate(8)
        len = blob.length()

        then:
        len == 8
    }

    def 'getBytes'(){
        when:
        def bytes = blob.getBytes(3, 7)

        then:
        bytes == [42, 97, 111, 115, 104, 106, 118] as byte[]
    }

    def 'getBinaryStream'(){
        when:
        def stream = blob.getBinaryStream()

        then:
        stream.bytes == BYTES
    }

    def 'getBinaryStream(long, long)'(){
        when:
        def stream = blob.getBinaryStream(3, 3)

        then:
        stream.bytes == [42, 97, 111] as byte[]
    }

    def 'position: bytes'(){
        when:
        def pos = blob.position([104, 106, 118] as byte[], 3)

        then:
        pos == 7
    }

    def 'position: Blob'(){
        when:
        def other = new MockBlob([104, 106, 118] as byte[])
        def pos = blob.position(other, 3)

        then:
        pos == 7
    }

    def 'setBytes(long, byte[], int, int)'(){
        when:
        byte[] data = [87, 23, 77, 44, 11, 53] as byte[]
        def len = blob.setBytes(2, data, 2, 3)

        then:
        len == 3
        blob.getBytes(1, blob.length() as int) == [11, 77, 44, 11] as byte[]
    }

    def 'setBytes(long, byte[])'(){
        when:
        byte[] data = [87, 23, 77, 44, 11, 53] as byte[]
        def len = blob.setBytes(2, data)

        then:
        len == 6
        blob.getBytes(1, blob.length() as int) == [11, 87, 23, 77, 44, 11, 53] as byte[]
    }

    def 'setBinaryStream'(){
        when:
        blob.setBinaryStream(2).withStream { out->
            out.write([99,88, 77] as byte[])
        }

        then:
        blob.getBytes(1, blob.length() as int) == [11, 99, 88, 77] as byte[]
    }
}