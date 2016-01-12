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
package com.stehno.vanilla.jdbc.mapper.transform

import com.stehno.vanilla.jdbc.mapper.FieldMapping
import com.stehno.vanilla.jdbc.mapper.ResultSetMapperDsl
import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.ClassNode

/**
 * Compile-time-based extension of the <code>ResultSetMapperBuilder</code> class. This class should not be used externally.
 */
@CompileStatic
class CompiledResultSetMapperBuilder implements ResultSetMapperDsl {

    final ClassNode mappedTypeNode

    private final Collection<String> ignoredNames = []
    private final Map<String, FieldMapping> mappings = [:]

    CompiledResultSetMapperBuilder(ClassNode mappedTypeNode) {
        this.mappedTypeNode = mappedTypeNode
    }

    /**
     * Maps the specified object property name and encapsulates it in a <code>FieldMapping</code>. The <code>FieldMapping</code> object is stored
     * internally and a reference is returned.
     *
     * @param propertyName the object property being mapped
     * @return a reference to the created FieldMapping object
     */
    FieldMapping map(String propertyName) {
        FieldMapping mapping = new CompiledFieldMapping(propertyName)
        mappings[propertyName] = mapping
        mapping
    }

    /**
     * Configures the specified object properties to be ignored by the mapping.
     *
     * @param propertyNames one or more object property names to be ignored
     */
    void ignore(String... propertyNames) {
        this.ignoredNames.addAll(propertyNames)
    }

    /**
     * Retrieves a collection containing all the configured mappings, with any ignored property mappings removed.
     *
     * @return a Collection of the field mappings
     */
    @SuppressWarnings('ConfusingMethodName')
    Collection<FieldMapping> mappings() {
        mappings.findAll { String fname, FieldMapping fm ->
            !(fname in ignoredNames)
        }.values().asImmutable()
    }
}
