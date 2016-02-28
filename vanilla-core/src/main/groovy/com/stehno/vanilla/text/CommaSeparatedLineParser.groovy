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

/**
 * Simple comma-separated value implementation of the LineParser interface.
 *
 * This implementation provides optional line item parser configuration via conversion closures.
 *
 * Empty lines and lines starting with # will be ignored.
 *
 * There is no requirement that every line must have the same number of items.
 *
 * This LineParser should be used with the TextFileReader.trimmed = true (the default setting).
 */
@TypeChecked
class CommaSeparatedLineParser implements LineParser {

    private static final String HASH = '#'
    private static final String COMMA = ','
    private final Map<Integer, Closure> itemConverters = [:]

    /**
     * Creates an instance of the line parser with optional conversion closures. The map of Closures should use the
     * column index (Integer) as the key for the map, with the Closure being the mapped value. The Closure should
     * accept a single argument, being the line item to be parsed.
     *
     * If a column does not have a conversion closure defined, it will simply create Strings.
     *
     * @param converters the map of conversion closures
     */
    CommaSeparatedLineParser(Map<Integer, Closure> converters = [:]) {
        itemConverters.putAll(converters)
    }

    @Override
    boolean parsable(String line) {
        line && !line.startsWith(HASH)
    }

    @Override
    Object[] parseLine(String line) {
        def data = []

        line.split(COMMA).eachWithIndex { item, int idx ->
            data << parseItem(item, idx)
        }

        data as Object[]
    }

    @Override
    Object parseItem(Object item, int index) {
        Closure converter = itemConverters[index]
        return converter ? converter.call(item) : (item as String)
    }
}
