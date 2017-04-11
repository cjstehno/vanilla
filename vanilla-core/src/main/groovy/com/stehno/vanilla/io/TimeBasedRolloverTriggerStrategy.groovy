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

import com.stehno.vanilla.util.TimeSpan
import groovy.transform.TypeChecked

import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes

import static java.lang.System.currentTimeMillis

/**
 * Rollover strategy which will cause the file to roll on the next write after the expiration time. This is not a out-of-process
 * rolling; if the file is never written to again, it will never roll.
 */
@TypeChecked
class TimeBasedRolloverTriggerStrategy implements RolloverTriggerStrategy {

    /**
     * The maximum time a file will be allowed to exist before being rolled (note that the file will not roll before that time, but
     * is not guaranteed to roll at all unless the file is written to).
     */
    TimeSpan maxLifespan

    @Override
    boolean shouldRoll(final File file) {
        currentTimeMillis() - Files.readAttributes(file.toPath(), BasicFileAttributes).creationTime().toMillis() >= maxLifespan.toMillis()
    }
}
