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

import groovy.transform.EqualsAndHashCode
import groovy.transform.TypeChecked

import java.sql.Blob
import java.sql.SQLException

import static java.lang.System.arraycopy
import static java.util.Arrays.copyOf

/**
 * A simple implementation of the Blob interface useful for unit test scenarios. This is primarily meant for use with
 * the MockResultSet; however, there is no restriction as such.
 */
@EqualsAndHashCode @TypeChecked @SuppressWarnings('DuplicateNumberLiteral')
class MockBlob implements Blob, DataObject {

    private byte[] bytes

    MockBlob(byte[] bytes=[] as byte[]) {
        this.bytes = bytes
    }

    @Override
    long length() throws SQLException {
        bytes.length
    }

    @Override
    byte[] getBytes(long pos, int length) throws SQLException {
        checkFree()

        int index = positionIndex(pos)

        bytes[index..(index + length - 1)] as byte[]
    }

    @Override
    InputStream getBinaryStream() throws SQLException {
        getBinaryStream(1, length())
    }

    @Override
    InputStream getBinaryStream(long pos, long length) throws SQLException {
        checkFree()

        new ByteArrayInputStream(getBytes(pos, length as int))
    }

    @Override
    long position(byte[] pattern, long start) throws SQLException {
        checkFree()

        int index = -1

        outer:
        for (int b = positionIndex(start); b < bytes.length; b++) {
            if (bytes[b] == pattern[0]) {
                for (int p = 1; p < pattern.length; p++) {
                    if (bytes[b + p] != pattern[p]) {
                        continue outer
                    }
                }

                index = b
                break
            }
        }

        return index == -1 ? -1 : index + 1
    }

    @Override
    long position(Blob pattern, long start) throws SQLException {
        checkFree()

        position pattern.getBytes(1, pattern.length() as int), start
    }

    @Override
    int setBytes(long pos, byte[] data) throws SQLException {
        setBytes pos, data, 0, data.length
    }

    @Override
    int setBytes(long pos, byte[] data, int offset, int len) throws SQLException {
        checkFree()

        bytes = copyOf(bytes, (pos + len - 1) as int)
        arraycopy(data, offset as int, bytes, positionIndex(pos), len as int)

        return len
    }

    @Override
    OutputStream setBinaryStream(long pos) throws SQLException {
        checkFree()

        new BlobOutputStream(this, positionIndex(pos))
    }

    @Override
    void truncate(long len) throws SQLException {
        checkFree()

        this.bytes = copyOf(bytes, len as int)
    }
}


