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
