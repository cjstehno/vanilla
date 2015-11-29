/*
 * Copyright (C) 2015 Christopher J. Stehno <chris@stehno.com>
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
package com.stehno.vanilla.jdbc.mapper

/**
 * Encapsulates the allowed mapping styles.
 */
enum MappingStyle {

    // FIXME: move this into one of the other classes or come up with a way to share it with the other mapper

    /**
     * Mapping is implied - All properties of the bean are mapped and must be "ignored" to be skipped.
     */
    IMPLICIT,

    /**
     * Mapping is explicit - only configured properties mapped from bean.
     */
    EXPLICIT
}
