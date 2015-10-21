package com.stehno.vanilla.test.jdbc

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.TypeChecked

import java.nio.charset.StandardCharsets
import java.sql.Clob
import java.sql.SQLException

import static com.stehno.vanilla.Affirmations.affirm
import static java.lang.String.valueOf

/**
 * FIXME: document me
 */
@EqualsAndHashCode @TypeChecked
class MockClob implements Clob {

    private final StringBuilder characters
    private boolean freed

    @Override
    long length() throws SQLException {
        return characters.length()
    }

    // https://github.com/mockrunner/mockrunner/blob/master/mockrunner-jdbc/src/main/java/com/mockrunner/mock/jdbc/MockClob.java

    @Override
    String getSubString(long pos, int length) throws SQLException {
        checkFree()

        int index = (pos as int) - 1

        affirm index >= 0, "The position must be greater than zero ($pos).", SQLException
        affirm pos < characters.length(), "The position must be within the length of the buffer ($pos >= ${characters.length()}).", SQLException
        affirm length >= 0, "The length must be non-negative ($length).", SQLException
        affirm index + length <= characters.length(), "The specified substring is outside the bounds of the buffer ($pos + $length > ${characters.length()}).", SQLException

        valueOf(characters[(index)..(index + length - 1)] as char[])
    }

    @Override
    Reader getCharacterStream() throws SQLException {
        checkFree()
        new StringReader(characters.toString())
    }

    @Override
    InputStream getAsciiStream() throws SQLException {
        checkFree()
        new ByteArrayInputStream(characters.toString().getBytes(StandardCharsets.US_ASCII))
    }

    @Override
    long position(String searchstr, long start) throws SQLException {
        return 0
    }

    @Override
    long position(Clob searchstr, long start) throws SQLException {
        return 0
    }

    @Override
    int setString(long pos, String str) throws SQLException {
        return 0
    }

    @Override
    int setString(long pos, String str, int offset, int len) throws SQLException {
        return 0
    }

    @Override
    OutputStream setAsciiStream(long pos) throws SQLException {
        return null
    }

    @Override
    Writer setCharacterStream(long pos) throws SQLException {
        return null
    }

    @Override
    void truncate(long len) throws SQLException {
        checkFree()
        characters.length = len as int
    }

    @Override
    void free() throws SQLException {

    }

    @Override
    Reader getCharacterStream(long pos, long length) throws SQLException {
        return null
    }

    private void checkFree() throws SQLException {
        affirm !freed, 'The clob has been freed and is no longer valid.', SQLException
    }
}
