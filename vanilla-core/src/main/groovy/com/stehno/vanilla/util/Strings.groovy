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
package com.stehno.vanilla.util

/**
 * Utilities for working with String objects.
 */
class Strings {

    /**
     * Converts a string of characters from underscore style to camel-case (e.g. first_name to firstName).
     *
     * @param underscore the underscore-style string
     * @return the string converted to camel-case
     */
    static String underscoreToCamelCase(String underscore) {
        if (!underscore || underscore.isAllWhitespace()) {
            return ''
        }
        return underscore.replaceAll(/_\w/) { it[1].toUpperCase() }
    }

    /**
     * Converts a string of camel-case characters to underscore-style (e.g. firstName to first_name).
     *
     * @param camel the camel-case string
     * @return the string converted to underscore-style
     */
    static String camelCaseToUnderscore(String camel) {
        camel.replaceAll(/\B[A-Z]/) { '_' + it }.toLowerCase()
    }
}
