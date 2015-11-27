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
package com.stehno.vanilla.jdbc.mapper.transform

import com.stehno.vanilla.jdbc.mapper.FieldMapping
import com.stehno.vanilla.jdbc.mapper.MappingStyle
import com.stehno.vanilla.jdbc.mapper.ResultSetMapperBuilder
import org.codehaus.groovy.ast.ClassNode

/**
 * Created by cjstehno on 11/26/15.
 */
class CompiledResultSetMapperBuilder extends ResultSetMapperBuilder {

    final ClassNode mappedTypeNode

    CompiledResultSetMapperBuilder(ClassNode mappedTypeNode, MappingStyle style) {
        super(mappedTypeNode.typeClass, style)
        this.mappedTypeNode = mappedTypeNode
    }

    protected FieldMapping createMapping(final String propertyName) {
        new CompiledFieldMapping(propertyName)
    }
}
