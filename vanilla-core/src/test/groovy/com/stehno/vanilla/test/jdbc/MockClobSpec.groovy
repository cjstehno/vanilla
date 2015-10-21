package com.stehno.vanilla.test.jdbc

import spock.lang.Specification
import spock.lang.Unroll

class MockClobSpec extends Specification {

    public static final String STRING = 'this is a test, only a test.'
    private MockClob clob = new MockClob(STRING.toCharArray())

    // FIXME: test the exception cases

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
}
