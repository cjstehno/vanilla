package com.stehno.vanilla.test.jdbc.mock

import spock.lang.Specification

class BlobOutputStreamSpec extends Specification {

    def 'write'(){
        setup:
        BlobOutputStream bos = new BlobOutputStream()

        when:
        bos.write(42)

        then:
        bos.index == 1
    }
}
