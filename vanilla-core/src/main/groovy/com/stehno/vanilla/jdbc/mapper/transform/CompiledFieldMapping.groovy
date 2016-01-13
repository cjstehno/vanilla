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
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression

import static com.stehno.vanilla.util.Strings.camelCaseToUnderscore
import static org.codehaus.groovy.ast.tools.GeneralUtils.*

/**
 * FieldMapping implementation used by compiled version of the ResultSet Mapper DSL.
 *
 * This class is used in AST operations and should not be used externally.
 */
class CompiledFieldMapping extends FieldMapping {

    private static final String RS = 'rs'

    /**
     * Creates a property mapping for the specified property.
     *
     * @param propertyName the name of the mapped property
     */
    protected CompiledFieldMapping(String propertyName) {
        super(propertyName)

        from constX(camelCaseToUnderscore(propertyName))
    }

    @Override
    protected FieldMapping extract(nameOrPosition, String getterName) {
        Expression argEx = nameOrPosition as Expression

        if (argEx.type == ClassHelper.STRING_TYPE) {
            argEx = plusX(callThisX('getPrefix'), argEx)
        }

        extractor = callX(varX(RS), getterName, args(argEx))

        this
    }

    @Override
    protected FieldMapping extract(Object mapper) {
        extractor = callX(mapper as MethodCallExpression, 'call', args(varX('rs')))
        this
    }
}
