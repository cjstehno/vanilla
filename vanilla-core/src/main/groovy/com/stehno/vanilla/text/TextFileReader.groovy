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

import java.nio.file.Path
import java.nio.file.Paths

import static com.stehno.vanilla.Affirmations.affirm
import static java.nio.file.Files.exists
import static java.nio.file.Files.isReadable

/**
 * Utility component for reading lines of data from a formatted line-based text file, such as a simple CSV file. Each
 * line will be processed with a configured LineParser when the file is read, to generate an array of object for each
 * line of text.
 *
 * This is useful for quick formatted data loading.
 */
class TextFileReader {

    /**
     * The Path of the text file to be read.
     */
    Path filePath

    /**
     * The LineParser instance to be used. An instance must be specified before attempting to read a file.
     */
    LineParser lineParser

    /**
     * The index of the first line of actual content to be read. This may be used to offset the data read to avoid
     * parsing file header content. The default is 0.
     */
    int firstLine

    /**
     * Whether or not the lines of the file will be trimmed (as String.trim()) when they are read, but before they are
     * passed into the LineParser. Defaults to true.
     */
    boolean trimmed = true

    /**
     * Specifies the path of the file to be read.
     */
    void setFilePath(Path path) {
        this.filePath = path
    }

    /**
     * An alternate means of specifying the filePath as a URI.
     */
    void setFilePath(URI uri) {
        this.filePath = Paths.get(uri)
    }

    /**
     * An alternate means of specifying the filePath as a URL.
     */
    void setFilePath(URL fileUrl) {
        setFilePath(fileUrl.toURI())
    }

    /**
     * An alternate means of specifying the filePath as a File.
     */
    void setFilePath(File file) {
        setFilePath(file.toURI())
    }

    /**
     * Used to iterate over each parsable line of the file, calling the closure with the parsed object array for each
     * line.
     *
     * @param closure the Closure used to handle the parsed lines, will be called with an Object array for each line.
     */
    void eachLine(Closure closure) {
        affirm lineParser != null, 'No LineParser has been specified.'
        affirm filePath && exists(filePath) && isReadable(filePath), "The file (${filePath.fileName}) does not exist or is not readable."

        filePath.eachLine(0) { String string, int idx ->
            if (idx >= firstLine) {
                String line = trimmed ? string.trim() : string

                if (lineParser.parsable(line)) {
                    closure.call(lineParser.parseLine(line))
                }
            }
        }
    }

    /**
     * A convenience method used to read, parse and return all lines of the file.
     *
     * @return a List of the data parsed from all lines of the file
     */
    List<Object[]> readLines() {
        List<Object[]> data = []

        eachLine { Object[] items ->
            data << items
        }

        data
    }
}