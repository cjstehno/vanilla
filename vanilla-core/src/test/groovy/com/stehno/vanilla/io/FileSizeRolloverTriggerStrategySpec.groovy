package com.stehno.vanilla.io

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

import static com.stehno.vanilla.io.StorageUnit.BYTES
import static com.stehno.vanilla.test.Randomizers.forByteArray
import static com.stehno.vanilla.test.Randomizers.random

class FileSizeRolloverTriggerStrategySpec extends Specification {

    @Rule public TemporaryFolder folder = new TemporaryFolder()

    private FileSizeRolloverTriggerStrategy strategy = new FileSizeRolloverTriggerStrategy(10, BYTES)

    @Unroll
    def 'shouldRoll: #size'() {
        expect:
        strategy.shouldRoll(fileOfSize(size)) == result

        where:
        size || result
        5    || false
        9    || false
        10   || true
        100  || true
    }

    private File fileOfSize(int byteCount) {
        File f = folder.newFile()
        f.bytes = random(forByteArray(byteCount..byteCount))
        f
    }
}
