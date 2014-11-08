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

import groovy.io.FileType

/**
 * Builder used to create an immutable file set, may be used directly or via the DSL (see FileSet.fileSet(Closure)).
 */
class FileSetBuilder {

    private final Set<File> files = [] as Set<File>

    void file(final String filePath) {
        file new File(filePath)
    }

    void file(final File file) {
        files << checkFile(file)
    }

    void dir(final String directory, final Closure filter = null) {
        dir new File(directory), filter
    }

    void dir(final File directory, final Closure filter = null) {
        checkDirectory(directory).eachFile(FileType.FILES) { f ->
            if (!filter || filter(f)) {
                files << f
            }
        }
    }

    void dirs(final String directory, final Closure filter = null) {
        dirs new File(directory), filter
    }

    void dirs(final File directory, final Closure filter = null) {
        checkDirectory(directory).eachFileRecurse(FileType.FILES) { f ->
            if (!filter || filter(f)) {
                files << f
            }
        }
    }

    void merge(final FileSet fs) {
        fs.each { f ->
            files << f
        }
    }

    FileSet build() {
        new FileSet(files.asImmutable())
    }

    private File checkFile(final File file) {
        if (file.exists() && file.file && file.canRead()) {
            return file
        }
        throw new IllegalArgumentException("Specified file ($file) cannot be read or does not exist.")
    }

    private File checkDirectory(final File directory) {
        if (directory.exists() && directory.directory && directory.canRead()) {
            return directory
        }
        throw new IllegalArgumentException("Specified path ($directory) cannot be read or is not a directory.")
    }
}
