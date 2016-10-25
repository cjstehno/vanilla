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

import groovy.transform.TypeChecked

/**
 * RollingFile RolloverTriggerStrategy implementation based on the size of the file. The rollover will be triggered when the file
 * size is greater than or equal to the specified file size. The resulting file size will not be exact, depending somewhat on the
 * size of the data buffers being written.
 */
@TypeChecked
class FileSizeRolloverTriggerStrategy implements RolloverTriggerStrategy {

    private final long triggerSize

    FileSizeRolloverTriggerStrategy(long fileSize = 10, StorageUnit unit = StorageUnit.MEGABYTES) {
        this.triggerSize = StorageUnit.BYTES.approximate(fileSize, unit)
    }

    @Override
    boolean shouldRoll(final File file) {
        file.length() >= triggerSize
    }
}
