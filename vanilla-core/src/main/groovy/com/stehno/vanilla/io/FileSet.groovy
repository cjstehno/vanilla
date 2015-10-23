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

import groovy.transform.CompileStatic
import groovy.transform.Immutable

//@formatter:off
/**
 * Unique collection of File object representing a set of files. FileSets are immutable and should be created with the DSL or builder directly.
 *
 * ```
 * FileSet.fileSet {
 *     dirs '/some/top/dir'
 *     file '/myfile.txt'
 *     dir( '/another/dir' ){ f->
 *         f.name.endsWith('.txt')
 *     }
 * }
 * ```
 */
//@formatter:on
@Immutable @CompileStatic
class FileSet {

    /**
     * The files contained in the file set.
     */
    Set<File> files

    /**
     * Retrieves the number of files in the set.
     *
     * @return number of files represented
     */
    int size() {
        files.size()
    }

    /**
     * Iterator over the files in the set. The closure will be called once with each file in the FileSet.
     *
     * @param closure the closure applied on each element found
     */
    void each(final Closure closure) {
        files.each closure
    }

    /**
     * Transforms the files in the FileSet and returns a new collection of the transformed values.
     *
     * @param closure used to transform each element
     * @return the transformed collection
     */
    List collect(final Closure closure) {
        files.collect closure
    }

    /**
     * Creates a FileSet from the provided closure definition (DSL).
     *
     * @param closure the DSL closure used to define the FileSet contents
     * @return the populated file set
     */
    static FileSet fileSet(final Closure closure) {
        FileSetBuilder fsb = new FileSetBuilder()
        closure.delegate = fsb
        closure()
        fsb.build()
    }
}
