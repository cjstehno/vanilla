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
package com.stehno.vanilla.text

import groovy.transform.TypeChecked

import static java.lang.Math.max

/**
 * Component for generating formatted text-based tables.
 */
@TypeChecked
class TextTable {

    final List<TableColumn> columns
    final List<List<String>> rows = []

    private static final String SPACE = ' '
    private static final String PIPE = '|'
    private static final String NEW_LINE = '\n'
    private static final String PLUS = '+'

    private final boolean borders
    private int width

    /**
     *  Creates a text-based table with the specified columns.
     *
     * @param borders whether or not to include borders between the rows (defaults to true)
     * @param columns the column definitions
     */
    TextTable(boolean borders = true, TableColumn... columns) {
        this.borders = borders
        this.columns = (columns as List).asImmutable()
    }

    /**
     * Calculates the table parameters. This method must be called before rendering the table.
     */
    void normalize() {
        rows.each { row ->
            int rowWidth = 0

            row.eachWithIndex { String cell, index ->
                def col = columns[index]
                col.minWidth = max(cell.size() + 2, col.minWidth)
                rowWidth += col.minWidth
            }

            width = max(width, rowWidth)
        }

        width += (columns.size() - 1)
    }

    /**
     * Renders the text table.
     *
     * @return the string table representation
     */
    String render() {
        StringBuilder tbl = new StringBuilder()

        // top border
        appendBorder tbl

        // header

        tbl.append(PIPE)
        columns.each { col ->
            tbl.append(SPACE)
            tbl.append(col.name.padRight(col.minWidth - 1))
            tbl.append(PIPE)
        }
        tbl.append(NEW_LINE)

        appendBorder tbl, '='

        // cells

        rows.each { row ->
            tbl.append(PIPE)
            row.eachWithIndex { cell, index ->
                tbl.append(SPACE)
                tbl.append(cell.padRight(columns[index].minWidth - 1))
                tbl.append(PIPE)
            }
            tbl.append(NEW_LINE)

            if (borders) {
                appendBorder tbl
            }
        }

        // bottom border
        if (!borders) {
            appendBorder tbl
        }

        tbl.toString()
    }

    /**
     * Alias for <code>addRow(List<String>)</code>.
     *
     * @param row the row to be added
     */
    void leftShift(List<String> row) {
        addRow(row)
    }

    /**
     * Used to add the row data to the table.
     *
     * @param row the row data to be added
     */
    void addRow(List<String> row) {
        assert row.size() == columns.size()
        rows.add(row)
    }

    private void appendBorder(final StringBuilder tbl, String character = '-') {
        tbl.append(PLUS).append(character * width).append(PLUS).append(NEW_LINE)
    }
}

/**
 * TextTable column definition.
 */
@TypeChecked
class TableColumn {
    final String name
    int minWidth

    TableColumn(String name, int minWidth = 0) {
        this.name = name
        this.minWidth = max(name.size() + 2, minWidth ?: 3)
    }
}
