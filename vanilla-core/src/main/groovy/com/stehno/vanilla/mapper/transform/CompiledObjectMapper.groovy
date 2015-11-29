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
package com.stehno.vanilla.mapper.transform

import com.stehno.vanilla.mapper.ObjectMapperSupport
import groovy.transform.TypeChecked

/**
 * Static (compile-time) base class for ObjectMappers created using the @Mapper annotation (and transformation).
 * Generally, this class should be only used internally.
 */
@TypeChecked @SuppressWarnings('AbstractClassWithoutAbstractMethod')
abstract class CompiledObjectMapper implements ObjectMapperSupport {
    // Nothing special, just giving the compiled mapper something to use.
}
