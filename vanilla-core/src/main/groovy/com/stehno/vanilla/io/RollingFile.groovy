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

import groovy.transform.CompileStatic

import java.nio.file.Files
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.zip.GZIPOutputStream

/**
 * Decorator used to provide rolling file behavior for a standard File object. The rollover triggering is based on a strategy provided
 * by the configured instance of the RolloverTriggerStrategy interface. The resulting rollover file names (and possibly locations) are
 * provided by the configured instance of the RolloverFileProvider instance.
 *
 * By default, if not otherwise specified, the FileSizeRolloverTriggerStrategy and DefaultRolloverFileProvider are used.
 *
 * The rollover files may also be compressed.
 *
 * The rollover behavior is thread safe.
 */
@CompileStatic
class RollingFile {

    /**
     * The target file - the current active file to be rolled over as configured.
     */
    File file

    /**
     * The rollover strategy to be used. Defaults to FileSizeRolloverTriggerStrategy.
     */
    RolloverTriggerStrategy rolloverStrategy = new FileSizeRolloverTriggerStrategy()

    /**
     * The file provider to be used. Defaults to the DefaultRolloverFileProvider.
     */
    RolloverFileProvider fileProvider = new DefaultRolloverFileProvider()

    /**
     * Whether or not compression is to be used. Defaults to false.
     */
    boolean compression

    private final ReadWriteLock lock = new ReentrantReadWriteLock()

    /**
     * Adds the specified text to the file.
     *
     * @param text the text to be appended
     * @param charset the character set to be used (defaults to UTF-8)
     */
    void append(String text, String charset = 'UTF-8') {
        file.append(text, charset)

        checkRollover()
    }

    /**
     * Adds the given bytes to the file.
     *
     * @param bytes the bytes to be appended
     */
    void append(byte[] bytes) {
        file.append(bytes)

        checkRollover()
    }

    /**
     * Adds the specified text to the file.
     *
     * @param text the text to be appended
     */
    void leftShift(String text) {
        append text
    }

    /**
     * Adds the given bytes to the file.
     *
     * @param bytes the bytes to be appended
     */
    void leftShift(byte[] bytes) {
        append bytes
    }

    private void checkRollover() {
        lock.readLock().lock()

        if (rolloverStrategy.shouldRoll(file)) {
            lock.readLock().unlock()
            lock.writeLock().lock()
            try {
                if (rolloverStrategy.shouldRoll(file)) {
                    rollover()
                }

                lock.readLock().lock()

            } finally {
                lock.writeLock().unlock()
            }
        }

        lock.readLock().unlock()
    }

    private void rollover() {
        fileProvider.provide(file, compression).withOutputStream { outs ->
            if (compression) {
                new GZIPOutputStream(outs).withStream { gout ->
                    Files.copy(file.toPath(), gout)
                }
            } else {
                Files.copy(file.toPath(), outs)
            }
        }

        file.text = ''
    }
}
