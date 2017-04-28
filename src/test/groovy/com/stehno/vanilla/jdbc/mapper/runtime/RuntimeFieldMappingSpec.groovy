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
package com.stehno.vanilla.jdbc.mapper.runtime
import com.stehno.vanilla.jdbc.mapper.FieldMapping
import spock.lang.Specification

import java.sql.ResultSet

class RuntimeFieldMappingSpec extends Specification {

    private static final Long TEST_LONG = 8675309L
    private final RuntimeFieldMapping mapping = new RuntimeFieldMapping('someTestProperty')
    private final ResultSet resultSet = GroovyMock(ResultSet){
        _ * getLong(42) >> TEST_LONG
        _ * getLong('the_long') >> TEST_LONG
    }

    def 'extract: number'(){
        when:
        FieldMapping fieldMapping = mapping.extract(42, 'getLong')

        then:
        fieldMapping.extractor
        fieldMapping.extractor.call(resultSet,'') == TEST_LONG
    }

    def 'extract: string'(){
        when:
        FieldMapping fieldMapping = mapping.extract('the_long', 'getLong')

        then:
        fieldMapping.extractor
        fieldMapping.extractor.call(resultSet,'') == TEST_LONG
    }

    def 'extract: gstring'(){
        setup:
        String prefix = 'the_'

        when:
        FieldMapping fieldMapping = mapping.extract("${prefix}long", 'getLong')

        then:
        fieldMapping.extractor
        fieldMapping.extractor.call(resultSet,'') == TEST_LONG
    }
}
