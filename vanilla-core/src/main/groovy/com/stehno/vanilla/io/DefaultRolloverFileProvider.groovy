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
 * A simple implementation of the RolloverFileProvider interface using a specified directory, or the same directory as the parent
 * file. The timestamp (at file creation time) will be appended to the file name.
 *
 * This class may be overridden to provide additional or modified functionality.
 */
@TypeChecked
class DefaultRolloverFileProvider implements RolloverFileProvider {

    /**
     * The directory where the rollover files will be written.
     */
    File directory

    private static final String PERIOD = '.'

    @Override
    File provide(final File file, final boolean compression) {
        new File(getParentPath(file), "${getNameWithoutExtension(file)}-${System.currentTimeMillis()}${getExtension(file, compression)}")
    }

    protected String getParentPath(final File file) {
        directory?.path ?: file.parent
    }

    protected String getNameWithoutExtension(final File file) {
        file.name[0..(file.name.lastIndexOf(PERIOD) - 1)]
    }

    protected String getExtension(final File file, final boolean compression) {
        file.name[(file.name.lastIndexOf(PERIOD) - 1)..-1] + (compression ? '.gz' : '')
    }
}
