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

/**
 * Strategy used by the RollingFile to generate an archive file where the current file contents
 * will be written on rollover.
 */
interface RolloverFileProvider {

    /**
     * Generates a unique file for rolling over written file data from the parent file.
     *
     * @param file the parent file (where the data will be coming from)
     * @param compression whether or not the compression is enabled
     * @return the new File to be used as the rollover target
     */
    File provide(File file, boolean compression)
}
