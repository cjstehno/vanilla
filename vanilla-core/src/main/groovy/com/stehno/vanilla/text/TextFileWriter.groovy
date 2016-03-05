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

import groovy.transform.TypeChecked

import java.nio.file.Path
import java.nio.file.Paths

/**
 * Utility component for writing object data to a file as one line of text per object written. A configured LineFormatter
 * is used to write the text to the file.
 *
 * Comments may also be written to the file and these will be passed through the LineFormatter so that they may be appropriately
 * formatter for the implementation.
 *
 * This component will not be very performant. It is intended for non-critical path operations.
 */
@TypeChecked
class TextFileWriter {

    /**
     * The Path of the text file being written.
     */
    Path filePath

    /**
     * The LineFormatter used to convert the incoming objects to the written line data.
     */
    LineFormatter lineFormatter

    /**
     * Used to specify the filePath of the file being written.
     */
    void setFilePath(Path path) {
        this.filePath = path
    }

    /**
     * Alternate means of specifying the filePath, as a URI.
     */
    void setFilePath(URI uri) {
        this.filePath = Paths.get(uri)
    }

    /**
     * Alternate means of specifying the filePath, as a URL.
     */
    void setFilePath(URL fileUrl) {
        setFilePath(fileUrl.toURI())
    }

    /**
     * Alternate means of specifying the filePath, as a File.
     */
    void setFilePath(File file) {
        setFilePath(file.toURI())
    }

    /**
     * Writes the given object to the file. The object will be processed by the LineFormatter.
     *
     * @param object the object to be written
     */
    void write(Object object) {
        writeText lineFormatter.formatLine(object)
    }

    /**
     * Writes the given text as a comment in the file, using the LineFormatter to format the comment text.
     *
     * @param comment the comment text
     */
    void writeComment(String comment) {
        writeText lineFormatter.formatComment(comment)
    }

    /**
     * A convenience method used to write all objects contained in the iterable to the file.
     *
     * @param objects the objects to be written
     */
    void writeAll(Iterable<Object> objects) {
        objects.each { obj ->
            write obj
        }
    }

    private void writeText(String text) {
        filePath.append(text)
        filePath.append('\n')
    }
}