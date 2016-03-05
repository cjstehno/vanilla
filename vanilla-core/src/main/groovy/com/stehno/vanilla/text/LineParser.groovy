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
package com.stehno.vanilla.text

/**
 * Defines the interface required for a TestFileReader line parser.
 */
interface LineParser {

    /**
     * Determines whether or not the provided line is parsable. A non-parsable line is not necessarily invalid, it may
     * simply be a comment or otherwise ignored line, dependent on the implementation.
     *
     * @param line the line being parsed
     * @return true if the line is parsable and should be parsed
     */
    boolean parsable(String line)

    /**
     * Parses the line of the file into an array of Objects.
     *
     * @param line the line of the file being parsed
     * @return the array of Objects representing the parsed line data.
     */
    Object[] parseLine(String line)

    /**
     * Parses an individual item from the line being parsed. This allows for specific types of objects to be created
     * when parsing a line, rather than just Strings.
     *
     * @param item the chunk of the line containing a single line item
     * @param index the chunk index (column index)
     * @return the parsed item which should be a String by default (if not otherwise processed)
     */
    Object parseItem(Object item, int index)
}
