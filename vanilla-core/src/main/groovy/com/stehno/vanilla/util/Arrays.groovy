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
