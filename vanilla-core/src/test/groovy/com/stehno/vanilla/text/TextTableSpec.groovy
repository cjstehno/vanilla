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

import spock.lang.Specification

class TextTableSpec extends Specification {

    def 'table: no borders'() {
        setup:
        def table = new TextTable(
            false,
            new TableColumn('Alpha', 10),
            new TableColumn('Bravo', 10),
            new TableColumn('Charlie', 10),
            new TableColumn('Delta')
        )

        when:
        table << ['And', 'now for something', 'completely different...', 'a man with three buttocks.']
        table << ['a', 'b', 'c', 'd']
        table << ['blah', 'foo', 'bar', 'gaz']

        table.normalize()

        String rendered = table.render()

        then:
        rendered == '''+-------------------------------------------------------------------------------------+
| Alpha    | Bravo             | Charlie                 | Delta                      |
+=====================================================================================+
| And      | now for something | completely different... | a man with three buttocks. |
| a        | b                 | c                       | d                          |
| blah     | foo               | bar                     | gaz                        |
+-------------------------------------------------------------------------------------+
'''
    }

    def 'table: with borders'() {
        setup:
        def table = new TextTable(
            new TableColumn('Alpha', 10),
            new TableColumn('Bravo', 10),
            new TableColumn('Charlie', 10),
            new TableColumn('Delta')
        )

        when:
        table << ['And', 'now for something', 'completely different...', 'a man with three buttocks.']
        table << ['a', 'b', 'c', 'd']
        table << ['blah', 'foo', 'bar', 'gaz']

        table.normalize()

        String rendered = table.render()

        then:
        rendered == '''+-------------------------------------------------------------------------------------+
| Alpha    | Bravo             | Charlie                 | Delta                      |
+=====================================================================================+
| And      | now for something | completely different... | a man with three buttocks. |
+-------------------------------------------------------------------------------------+
| a        | b                 | c                       | d                          |
+-------------------------------------------------------------------------------------+
| blah     | foo               | bar                     | gaz                        |
+-------------------------------------------------------------------------------------+
'''
    }
}
