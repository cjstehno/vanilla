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

import groovy.transform.TupleConstructor

import java.sql.Blob
import java.sql.SQLException

/**
 * OutputStream facade over data contained in a Blob. This class is not generally meant for external use.
 */
@TupleConstructor
class BlobOutputStream extends OutputStream {

    Blob blob = new MockBlob()
    int index

    @Override
    void write(int b) throws IOException {
        try {
            blob.setBytes(index + 1, [b] as byte[])
            index++

        } catch (SQLException ex) {
            throw new IOException(ex.message)
        }
    }
}
