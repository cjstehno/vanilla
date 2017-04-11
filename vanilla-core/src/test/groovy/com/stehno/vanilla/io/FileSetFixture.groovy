/*
 * Copyright (C) 2017 Christopher J. Stehno <chris@stehno.com>
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

import org.junit.rules.ExternalResource
import org.junit.rules.TemporaryFolder

/**
 * JUnit Rule fixture for managing a set of temporary files.
 */
class FileSetFixture extends ExternalResource {

    File alphaFile, bravoFile, charlieDir, deltaFile, echoFile, foxtrotDir, golfFile, hotelFile
    Collection<File> files

    private final TemporaryFolder folder = new TemporaryFolder()

    @Override
    protected void before() throws Throwable {
        folder.before()

        alphaFile = folder.newFile 'alpha.txt'
        bravoFile = folder.newFile 'bravo.bin'

        charlieDir = folder.newFolder 'charlie'

        deltaFile = folder.newFile 'charlie/delta.bin'
        echoFile = folder.newFile 'charlie/echo.txt'

        foxtrotDir = folder.newFolder 'charlie', 'foxtrot'

        golfFile = folder.newFile 'charlie/foxtrot/golf.txt'
        hotelFile = folder.newFile 'charlie/foxtrot/hotel.bin'

        files = [alphaFile, bravoFile, deltaFile, echoFile, golfFile, hotelFile]
    }

    @Override
    protected void after() {
        folder.after()
    }

    FileSet fileSetOfAll() {
        FileSet.fileSet {
            files.each {
                file it
            }
        }
    }
}
