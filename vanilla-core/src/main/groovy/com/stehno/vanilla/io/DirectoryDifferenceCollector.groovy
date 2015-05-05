package com.stehno.vanilla.io

import java.security.MessageDigest

import static groovy.io.FileType.FILES

/**
 * Utility object used to scan two directories and provide a summary of their differences at the file level.
 *
 * In order to determine if the content of two files is different, a SHA hash is compared for each.
 */
class DirectoryDifferenceCollector {

    private final MessageDigest digester = MessageDigest.getInstance('SHA')

    /**
     * Scan and summarize the file differences between the two directories. The returned DirectoryDifferences object
     * will contain the summary of the differences. The order of the directories passed into this method is arbitrary
     * from the point of view of this object, the differences will be the same no matter which directory is A and which
     * is B.
     *
     * @param dirA the first directory
     * @param dirB the second directory
     * @return the differences between the two directories.
     */
    DirectoryDifferences scan(File dirA, File dirB) {
        def filesInA = collectFiles(dirA)
        def filesInB = collectFiles(dirB)

        new DirectoryDifferences(
            findFiles(filesInA, filesInB).asImmutable(),
            findFiles(filesInB, filesInA).asImmutable(),
            filesInA.intersect(filesInB).findAll { file -> different(dirA, dirB, file) }.asImmutable()
        )
    }

    private List<File> collectFiles(File dir) {
        def files = []
        dir.eachFileRecurse(FILES) {
            files << (it.toURI() as String) - (dir.toURI() as String)
        }
        return files
    }

    private boolean different(File dirA, File dirB, String path) {
        hashFile(asFile(dirA, path)) != hashFile(asFile(dirB, path))
    }

    private static File asFile(File dir, String path) {
        new File(new URI((dir.toURI() as String) + path))
    }

    private static Collection findFiles(Collection thatAreIn, Collection notIn) {
        (thatAreIn - notIn)
    }

    private byte[] hashFile(File file) {
        digester.digest(file.bytes)
    }
}
