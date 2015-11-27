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
package com.stehno.vanilla.mapper

import groovy.transform.TypeChecked

/**
 * Provides implementations for the common methods of the ObjectMapper interface.
 */
@TypeChecked @SuppressWarnings('AbstractClassWithoutAbstractMethod')
abstract trait ObjectMapperSupport implements ObjectMapper {

    Object create(final Object src, final Class destClass) {
        def dest = destClass.newInstance()
        copy src, dest
        dest
    }

    Closure collector(final Class destClass) {
        return { o ->
            create o, destClass
        }
    }
}
