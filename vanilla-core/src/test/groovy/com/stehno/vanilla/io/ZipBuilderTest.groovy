/*
 * Copyright (c) 2014 Christopher J. Stehno
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

import static java.util.zip.Deflater.BEST_COMPRESSION

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class ZipBuilderTest {

    private static final String[] ENTRY_NAME = ['ZipEntryName', 'AnotherZipEntryName']
    private static final String[] COMMENT_ENTRYLEVEL = ['This is an entry-level comment', 'This is another entry-level comment']
    private static final byte[][] DATA_ARRAY = ['This is some content'.bytes, 'This is some more content'.bytes]

    @Rule public final TemporaryFolder temporaryFolder = new TemporaryFolder()

    private File zipfile
    private ZipBuilder zipBuilder

    @Before void before() {
        zipfile = temporaryFolder.newFile('some.zip')
        zipBuilder = new ZipBuilder(new FileOutputStream(zipfile)).useCompression(BEST_COMPRESSION)
    }

    @Test void 'addEntry: EntryAndBytes'() {
        zipBuilder.withComment('some comment') // cannot currently test for file level comments

        ENTRY_NAME.eachWithIndex { en, i ->
            ZipEntry entry = new ZipEntry(en)
            entry.comment = COMMENT_ENTRYLEVEL[i]

            zipBuilder.addEntry(entry, DATA_ARRAY[i])
        }

        zipBuilder.zip()

        final ZipFile zf = wrapZip()
        assert zf.size() == 2

        ENTRY_NAME.eachWithIndex { en, i ->
            assertZipEntry zf, ENTRY_NAME[i], COMMENT_ENTRYLEVEL[i], DATA_ARRAY[i]
        }
    }

    @Test void 'addEntry: Name Comment Input'() {
        zipBuilder.addEntry(ENTRY_NAME[0], new ByteArrayInputStream(DATA_ARRAY[0]), COMMENT_ENTRYLEVEL[0]).zip()

        final ZipFile zf = wrapZip()
        assert 1 == zf.size()
        assertZipEntry zf, ENTRY_NAME[0], COMMENT_ENTRYLEVEL[0], DATA_ARRAY[0]
    }

    @Test void 'addEntry: Name Comment Bytes'() {
        zipBuilder.addEntry(ENTRY_NAME[0], DATA_ARRAY[0], COMMENT_ENTRYLEVEL[0]).zip()

        final ZipFile zf = wrapZip()
        assert 1 == zf.size()
        assertZipEntry zf, ENTRY_NAME[0], COMMENT_ENTRYLEVEL[0], DATA_ARRAY[0]
    }

    @Test void 'addEntry: NameBytes'() {
        zipBuilder.addEntry(ENTRY_NAME[0], DATA_ARRAY[0]).zip()

        final ZipFile zf = wrapZip()
        assert 1 == zf.size()
        assertZipEntry zf, ENTRY_NAME[0], null, DATA_ARRAY[0]
    }

    @Test void 'addEntry: Name Stream'() {
        zipBuilder.addEntry(ENTRY_NAME[0], new ByteArrayInputStream(DATA_ARRAY[0])).zip()

        final ZipFile zf = wrapZip()
        assert 1 == zf.size()
        assertZipEntry zf, ENTRY_NAME[0], null, DATA_ARRAY[0]
    }

    @Test void addEntry_EntryAndInputstream() {
        ENTRY_NAME.eachWithIndex { en, i ->
            final ZipEntry entry = new ZipEntry(en)
            entry.setComment(COMMENT_ENTRYLEVEL[i])

            zipBuilder.addEntry(entry, new ByteArrayInputStream(DATA_ARRAY[i]))
        }

        zipBuilder.zip()

        // asserts
        final ZipFile zf = wrapZip()
        assert 2 == zf.size()

        ENTRY_NAME.eachWithIndex { en, i ->
            assertZipEntry zf, en, COMMENT_ENTRYLEVEL[i], DATA_ARRAY[i]
        }
    }

    private static void assertZipEntry(final ZipFile zf, final String name, final String comment, final byte[] data) {
        final ZipEntry ze = zf.getEntry(name)
        assert ze
        assert name == ze.name
        assert comment == ze.comment
        assert data == zf.getInputStream(ze).bytes
    }

    private ZipFile wrapZip() {
        new ZipFile(zipfile)
    }
}