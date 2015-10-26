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
package com.stehno.vanilla.jdbc

import com.stehno.vanilla.util.Strings

import java.sql.ResultSet

/**
 * FIXME: document me
 *
 * from methods map to the get methods of ResultSet
 * from, fromObject --> getObject
 * fromString --> getString
 * fromDate --> getDate
 * etc (field name or position is valid)
 */
class FieldMapping {

    final String propertyName
    private Closure extractor
    private Closure converter

    Closure getExtractor() {
        extractor
    }

    Closure getConverter(){
        converter
    }

    FieldMapping(String propertyName) {
        this.propertyName = propertyName

        from Strings.camelCaseToUnderscore(propertyName)
    }

    FieldMapping methodMissing(String name, args) {
        // FIXME: validate against approved list?

        MetaMethod method = ResultSet.metaClass.getMetaMethod(
            name == 'from' ? 'getObject' : "get${name - 'from'}",
            args[0]
        )

        if (method) {
            extractor = { ResultSet rs ->
                method.invoke(rs, args[0])
            }
            this

        } else {
            throw new NoSuchMethodException("No type conversion method ($name) exists.")
        }
    }

    void using(Closure closure) {
        converter = closure
    }
}
