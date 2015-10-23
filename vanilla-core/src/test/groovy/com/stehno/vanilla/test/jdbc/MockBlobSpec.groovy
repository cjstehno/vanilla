package com.stehno.vanilla.test.jdbc

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
