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
package com.stehno.vanilla.io

import static FileSet.fileSet

import org.junit.Rule
import org.junit.Test

class FileSetTest {

    @Rule public final FileSetFixture fileFixture = new FileSetFixture()

    @Test @SuppressWarnings(['SpaceAfterOpeningBrace', 'SpaceBeforeClosingBrace']) void empty() {
        FileSet fs = fileSet {}

        assert fs.size() == 0
    }

    @Test void 'file: as file'() {
        FileSet fs = fileSet {
            file fileFixture.echoFile
        }

        assertFiles fs, [fileFixture.echoFile]
    }

    @Test void 'file: as string'() {
        FileSet fs = fileSet {
            file(fileFixture.echoFile as String)
        }

        assertFiles fs, [fileFixture.echoFile]
    }

    @Test void 'files: both types'() {
        FileSet fs = fileSet {
            file(fileFixture.echoFile as String)
            file fileFixture.hotelFile
        }

        assertFiles fs, [fileFixture.echoFile, fileFixture.hotelFile]
    }

    @Test(expected = IllegalArgumentException) void 'file: non-existant'() {
        fileSet {
            file new File('/xray/zulu.dat')
        }
    }

    @Test void 'dir: as file'() {
        FileSet fs = fileSet {
            dir(fileFixture.charlieDir)
        }

        assertFiles fs, [fileFixture.deltaFile, fileFixture.echoFile]
    }

    @Test void 'dir: as string'() {
        FileSet fs = fileSet {
            dir(fileFixture.charlieDir as String)
        }

        assertFiles fs, [fileFixture.deltaFile, fileFixture.echoFile]
    }

    @Test void 'dir: as file with filter'() {
        FileSet fs = fileSet {
            dir(fileFixture.charlieDir) { f -> f.name.endsWith('.txt') }
        }

        assertFiles fs, [fileFixture.echoFile]
    }

    @Test void 'dir: as string with filter'() {
        FileSet fs = fileSet {
            dir(fileFixture.charlieDir as String) { f -> f.name.endsWith('.txt') }
        }

        assertFiles fs, [fileFixture.echoFile]
    }

    @Test void 'dirs: as file'() {
        FileSet fs = fileSet {
            dirs(fileFixture.charlieDir)
        }

        assertFiles fs, [fileFixture.deltaFile, fileFixture.echoFile, fileFixture.golfFile, fileFixture.hotelFile]
    }

    @Test void 'dirs: as string'() {
        FileSet fs = fileSet {
            dirs(fileFixture.charlieDir as String)
        }

        assertFiles fs, [fileFixture.deltaFile, fileFixture.echoFile, fileFixture.golfFile, fileFixture.hotelFile]
    }

    @Test void 'dirs: as file with filter'() {
        FileSet fs = fileSet {
            dirs(fileFixture.charlieDir) { f -> f.name.endsWith('.txt') }
        }

        assertFiles fs, [fileFixture.echoFile, fileFixture.golfFile]
    }

    @Test void 'dirs: as string with filter'() {
        FileSet fs = fileSet {
            dirs(fileFixture.charlieDir as String) { f -> f.name.endsWith('.txt') }
        }

        assertFiles fs, [fileFixture.echoFile, fileFixture.golfFile]
    }

    @Test(expected = UnsupportedOperationException) void immutability() {
        FileSet fs = fileSet {
            file(fileFixture.echoFile as String)
            file fileFixture.hotelFile
        }

        assertFiles fs, [fileFixture.echoFile, fileFixture.hotelFile]

        fs.files.add(fileFixture.alphaFile)
    }

    @Test void 'merging filesets'() {
        FileSet fs = fileSet {
            file fileFixture.bravoFile
            merge fileSet {
                file fileFixture.alphaFile
            }
        }

        assertFiles fs, [fileFixture.alphaFile, fileFixture.bravoFile]
    }

    private void assertFiles(final FileSet fs, final Collection files) {
        assert fs.size() == files.size()

        fs.each {
            assert files.contains(it)
        }
    }
}