/*
 * Copyright (c) 2015 Christopher J. Stehno
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

package com.stehno.vanilla.mapper

interface ObjectMapper {

    /**
     * Performs the mapping/copy operation from the source object to the destination object.
     *
     * @param src the source object instance
     * @param dest the destination object instance
     */
    void copy(final Object src, final Object dest)

    /**
     * Creates a new instance of the destination object class populated with the data from the source object (using the
     * mapper configuration). The destination class must have a default/empty constructor.
     *
     * @param src the source object
     * @param destClass the destination class (must have default constructor)
     * @return a populated instance of the destination class
     */
    Object create(final Object src, final Class destClass)

    /**
     * Wraps a create(Object,Class) method call in a closure for use as the argument to a collect method call.
     *
     * @param destClass the destination class (must have a default constructor)
     * @return a closure which will call the create method to populate a new instance of the destination class.
     */
    Closure collector(Class destClass)
}
