/*
 * Copyright (C) 2016 Christopher J. Stehno <chris@stehno.com>
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

    /**
     * Adds a single file (by path string) to the file set. The path will be converted to a File object before being added to the file set.
     *
     * @param filePath a file path string
     */
    void file(final String filePath) {
        file new File(filePath)
    }

    /**
     * Adds a single file as a File object to the file set.
     *
     * @param file the file object to be added
     */
    void file(final File file) {
        files << checkFile(file)
    }

    /**
     * Adds a single directory (non-recursive) to the file set. An optional filtering closure may be provided; the closure should accept a File object
     * and return a value of true if the file should be included in the file set, false if not.
     *
     * @param directory the directory to include
     * @param filter the optional filtering closure
     */
    void dir(final String directory, final Closure filter = null) {
        dir new File(directory), filter
    }

    /**
     * Adds a single directory (non-recursive) to the file set as a File object. An optional filtering closure may be provided; the closure should
     * accept a File object and return a value of true if the file should be included in the file set, false if not.
     *
     * @param directory the directory to include
     * @param filter the optional filtering closure
     */
    void dir(final File directory, final Closure filter = null) {
        checkDirectory(directory).eachFile(FileType.FILES) { f ->
            if (!filter || filter(f)) {
                files << f
            }
        }
    }

    /**
     * Adds a directory and its child directories recursively to the file set. An optional filtering closure may be provided; the closure should
     * accept a File object and return a value of true if the file should be included in the file set, false if not.
     *
     * @param directory the directory to include
     * @param filter the optional filtering closure
     */
    void dirs(final String directory, final Closure filter = null) {
        dirs new File(directory), filter
    }

    /**
     * Adds a directory and its child directories recursively to the file set. An optional filtering closure may be provided; the closure should
     * accept a File object and return a value of true if the file should be included in the file set, false if not.
     *
     * @param directory the directory to include
     * @param filter the optional filtering closure
     */
    void dirs(final File directory, final Closure filter = null) {
        checkDirectory(directory).eachFileRecurse(FileType.FILES) { f ->
            if (!filter || filter(f)) {
                files << f
            }
        }
    }

    /**
     * Merge another file set into the file set being built. The external file set is unchanged.
     *
     * @param fs the file set to be merged
     */
    void merge(final FileSet fs) {
        fs.each { f ->
            files << f
        }
    }

    /**
     * Build the final immutable FileSet object.
     *
     * @return a populated FileSet object
     */
    FileSet build() {
        new FileSet(files.asImmutable())
    }

    private static File checkFile(final File file) {
        if (file.exists() && file.file && file.canRead()) {
            return file
        }
        throw new IllegalArgumentException("Specified file ($file) cannot be read or does not exist.")
    }

    private static File checkDirectory(final File directory) {
        if (directory.exists() && directory.directory && directory.canRead()) {
            return directory
        }
        throw new IllegalArgumentException("Specified path ($directory) cannot be read or is not a directory.")
    }
}
