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

import groovy.transform.Immutable
import groovy.transform.ToString

/**
 * Immutable object representation of the file differences between two directories.
 * The resulting differences will be expressed as Strings representing the path below the shared root of comparison.
 */
@Immutable @ToString(includeNames = true)
class DirectoryDifferences {

    /**
     * The files found only in the first directory (A).
     */
    Collection<String> filesOnlyInA

    /**
     * The files found only in the second directory (B).
     */
    Collection<String> filesOnlyInB

    /**
     * The files existing in both directories that do not have the same content.
     */
    Collection<String> modifiedFiles
}
