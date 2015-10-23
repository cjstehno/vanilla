/*
 * Copyright (c) 2015 Christopher J. Stehno
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

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor
import groovy.transform.TypeChecked

import java.sql.Clob
import java.sql.SQLException

import static com.stehno.vanilla.Affirmations.affirm
import static java.lang.String.valueOf
import static java.lang.System.arraycopy
import static java.nio.charset.StandardCharsets.US_ASCII
import static java.util.Arrays.copyOf

/**
 * A simple implementation of the Clob interface useful for unit test scenarios. This is primarily meant for use with
 * the MockResultSet; however, there is no restriction as such.
 */
@EqualsAndHashCode @TypeChecked
class MockClob implements Clob {

    private char[] characters
    private boolean freed

    MockClob(char[] chars) {
        this.characters = chars
    }

    MockClob(String string) {
        this.characters = string.toCharArray()
    }

    @Override
    long length() throws SQLException {
        characters.length
    }

    @Override
    String getSubString(long pos, int length) throws SQLException {
        checkFree()

        int index = positionIndex(pos)

        valueOf characters[index..(index + length - 1)] as char[]
    }

    @Override
    Reader getCharacterStream() throws SQLException {
        checkFree()

        new StringReader(toString())
    }

    @Override
    InputStream getAsciiStream() throws SQLException {
        checkFree()

        new ByteArrayInputStream(toString().getBytes(US_ASCII))
    }

    @Override
    long position(final String str, final long start) throws SQLException {
        checkFree()

        int index = toString().indexOf(str, positionIndex(start))
        return index != -1 ? index + 1 : -1
    }

    @Override
    long position(final Clob clob, final long start) throws SQLException {
        position clob.getSubString(1, clob.length() as int), start
    }

    @Override
    int setString(long pos, String str) throws SQLException {
        setString pos, str, 0, str.length()
    }

    @Override
    int setString(final long pos, final String str, final int offset, final int len) throws SQLException {
        checkFree()

        characters = copyOf(characters, (pos + len - 1) as int)
        arraycopy(str.toCharArray(), offset as int, characters, positionIndex(pos), len as int)

        return len
    }

    @Override
    OutputStream setAsciiStream(long pos) throws SQLException {
        checkFree()

        new ClobOutputStream(this, positionIndex(pos))
    }

    @Override
    Writer setCharacterStream(long pos) throws SQLException {
        checkFree()

        new ClobWriter(this, positionIndex(pos))
    }

    @Override
    void truncate(long len) throws SQLException {
        checkFree()

        this.characters = copyOf(characters, len as int)
    }

    @Override
    void free() throws SQLException {
        freed = true
    }

    @Override
    Reader getCharacterStream(long pos, long length) throws SQLException {
        checkFree()

        new StringReader(getSubString(pos, length as int))
    }

    private void checkFree() throws SQLException {
        affirm !freed, 'The clob has been freed and is no longer valid.', SQLException
    }

    private int positionIndex(long pos) throws SQLException {
        affirm pos > 0, "The position must be greater than zero ($pos).", SQLException
        affirm pos <= characters.length, "The position must be within the length of the buffer ($pos > ${characters.length}).", SQLException

        pos - 1
    }

    @Override
    String toString() {
        valueOf characters
    }
}

@TupleConstructor
class ClobWriter extends Writer {

    Clob clob
    int index

    @Override
    void write(final char[] buf, final int off, final int len) throws IOException {
        try {
            clob.setString(index + 1, valueOf(buf, off, len))
            index += len

        } catch (SQLException ex) {
            throw new IOException(ex.message)
        }
    }

    @Override
    void close() throws IOException {
        // nothing
    }

    @Override
    void flush() throws IOException {
        // nothing
    }
}

@TupleConstructor
class ClobOutputStream extends OutputStream {

    Clob clob
    int index

    void write(int byteValue) throws IOException {
        try {
            clob.setString(index + 1, new String([byteValue as byte] as byte[]))
            index++

        } catch (SQLException ex) {
            throw new IOException(ex.message)
        }
    }
}