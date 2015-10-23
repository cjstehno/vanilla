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
package com.stehno.vanilla.util

import groovy.transform.CompileStatic

/**
 * FIXME: document me
 */
@CompileStatic
class Arrays {

    // TODO: other primitives - also figure out a way to share the code
    // TODO: test boundary conditions and nulls
    // FIXME: this may just go away

    static <A> A[] insert(final A[] target, final int idx, final A[] incoming, final int off, final int len) {
        def prefix = target[0..(idx - 1)]
        def inject = incoming[off..(off + len - 1)]
        def suffix = target[idx..-1]

        (prefix + inject + suffix) as A[]
    }

    static char[] insert(final char[] target, final int idx, final char[] incoming, final int off, final int len) {
        def prefix = target[0..(idx - 1)]
        def inject = incoming[off..(off + len - 1)]
        def suffix = target[idx..-1]

        (prefix + inject + suffix) as char[]
    }
}
