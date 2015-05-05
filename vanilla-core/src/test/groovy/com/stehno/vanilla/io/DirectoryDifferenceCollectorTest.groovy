package com.stehno.vanilla.io

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DirectoryDifferenceCollectorTest {

    @Rule public TemporaryFolder folderA = new TemporaryFolder()
    @Rule public TemporaryFolder folderB = new TemporaryFolder()

    @Before void before(){
        File folderAAlpha = folderA.newFolder('alpha')
        createFile folderAAlpha, 'one.txt', 'content for one'
        createFile folderAAlpha, 'two.txt', 'content for two'

        File folderABravo = folderA.newFolder('alpha','bravo')
        createFile folderABravo, 'four.txt', 'content for four'

        File folderBAlpha = folderB.newFolder('alpha')
        createFile folderBAlpha, 'one.txt', 'content for one'
        createFile folderBAlpha, 'three.txt', 'content for three'

        File folderBBravo = folderB.newFolder('alpha','bravo')
        createFile folderBBravo, 'four.txt', 'content for different four'
    }

    @Test void 'general usage'(){
        DirectoryDifferences differences = new DirectoryDifferenceCollector().scan(folderA.root, folderB.root)
        assert differences.filesOnlyInA == ['alpha/two.txt']
        assert differences.filesOnlyInB == ['alpha/three.txt']
        assert differences.modifiedFiles == ['alpha/bravo/four.txt']
    }

    @Test void 'ensure symmetry'(){
        DirectoryDifferences differences1 = new DirectoryDifferenceCollector().scan(folderA.root, folderB.root)
        DirectoryDifferences differences2 = new DirectoryDifferenceCollector().scan(folderB.root, folderA.root)

        assert differences1.filesOnlyInA == differences2.filesOnlyInB
        assert differences1.filesOnlyInB == differences2.filesOnlyInA
        assert differences1.modifiedFiles == differences2.modifiedFiles
    }

    private static File createFile(File folder, String name, String content){
        def file = new File(folder, name)
        file.text = content
        return file
    }
}
