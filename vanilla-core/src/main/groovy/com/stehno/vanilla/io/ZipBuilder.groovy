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

package com.stehno.vanilla.io

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Uses the fluent builder pattern to build zip files.
 */
class ZipBuilder {

    private final ZipOutputStream zos

    /**
     * Creates a zip builder with the given output stream. The stream will be closed when the <code>zip()</code>
     * method is called.
     *
     * @param os the output stream the zip file is to be written into
     */
    ZipBuilder(final OutputStream os) {
        zos = new ZipOutputStream(os)
    }

    /**
     * Used to specify the the zip file should be compressed. By default no compression is performed.
     *
     * @param level the compression level, defaults to 0 - no compression
     * @return a reference to the zip builder
     */
    ZipBuilder useCompression(final int level = 0) {
        zos.method = ZipOutputStream.DEFLATED
        zos.level = level
        this
    }

    /**
     * Used to specify a zip file level comment. This comment does not seem to be visible to the
     * Java API itself however, other applications such as WinZip do recognize it.
     *
     * @param comment the file level comment
     * @return a reference to the builder
     */
    ZipBuilder withComment(final String comment) {
        zos.comment = comment
        this
    }

    /**
     * Used to add an entry to the zip file.
     *
     * @param entry the zip entry to be added
     * @param bytes the data bytes of the entry data
     * @return a reference to the zip builder
     * @throws IOException if there is a problem writing the entry data
     */
    ZipBuilder addEntry(final ZipEntry entry, final byte[] bytes) throws IOException {
        zos.putNextEntry(entry)
        zos.write(bytes)
        zos.closeEntry()
        this
    }

    /**
     * 	Used to add the entry to the zip file. The data from the input stream will be read and
     * the stream will be closed by this method.
     *
     * @param entry the zip entry
     * @param in the input stream containing the data for the entry
     * @return a reference to the builder
     * @throws IOException if there is a problem writing the entry data
     */
    ZipBuilder addEntry(final ZipEntry entry, final InputStream ins) throws IOException {
        zos.putNextEntry(entry)
        zos << ins.bytes
        zos.closeEntry()
        this
    }

    /**
     * Used to add an entry with the given data.
     *
     * @param name the entry name
     * @param bytes the entry data
     * @param comment the optional comment for the entry
     * @return a reference to the builder
     * @throws IOException if there is a problem writing the entry data
     */
    ZipBuilder addEntry(final String name, final byte[] bytes, final String comment = null) throws IOException {
        final ZipEntry entry = new ZipEntry(name)
        if (comment) {
            entry.comment = comment
        }
        addEntry(entry, bytes)
    }

    /**
     * Used to add an entry to the zip file. The input stream will be read and closed by this method.
     *
     * @param name the entry name
     * @param in the input stream containing the entry data
     * @param comment the optional comment for the entry
     * @return a reference to the builder
     * @throws IOException if there is a problem writing the entry data
     */
    ZipBuilder addEntry(final String name, final InputStream ins, final String comment = null) throws IOException {
        final ZipEntry entry = new ZipEntry(name)
        if (comment) {
            entry.comment = comment
        }
        addEntry(entry, ins)
    }

    /**
     * 	Used to finish writing the zip file and close out the builder.
     *
     * @throws IOException if there is a problem writing the zip file
     */
    void zip() throws IOException {
        try {
            zos.finish()
        }
        finally {
            zos?.close()
        }
    }
}
