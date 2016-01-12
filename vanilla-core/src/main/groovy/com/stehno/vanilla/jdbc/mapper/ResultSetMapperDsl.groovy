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
package com.stehno.vanilla.jdbc.mapper
/**
 * Defines the methods available to the ResultSetMapper DSL.
 */
interface ResultSetMapperDsl {

    /**
     * Maps an object property and encapsulates it in a FieldMapping object for further configuration.
     *
     * @param propertyName the name of the object property
     * @return the populated FieldMapping object
     */
    FieldMapping map(String propertyName)

    /**
     * Specifies one or more object properties to be ignored by the mapper. This operation is only meaningful when using the IMPLICIT style of
     * mapping.
     *
     * @param propertyNames one or more object property names to be ignored during mapping
     */
    void ignore(String... propertyNames)
}

