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
package com.stehno.vanilla.overlap

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 *  A ComparableLobe is a Lobe implementation providing support for single Comparable values, Comparable range values and combinations of either type.
 *
 *  <pre>
 *  new ComparableLobe( 100 )
 *  new ComparableLobe( 100..1000 )
 *  new ComparableLobe( 100, 'a'..'q', 10..15 )
 *  </pre>
 */
@ToString(includeFields = true, includeNames = true) @EqualsAndHashCode(includeFields = true)
class ComparableLobe implements Lobe {

    private final rules = []

    /**
     * Creates a Lobe based on Comparable data.
     *
     * @param items the comparable data items making up the Lobe
     */
    ComparableLobe(Object... items) {
        items?.each {
            addRule it
        }
    }

    /**
     * Creates a Lobe based on Comparable data.
     *
     * @param items the comparable data items making up the Lobe
     */
    ComparableLobe(Collection items) {
        items?.each {
            addRule it
        }
    }

    void addRule(item) {
        rules << (item instanceof Range ? item : (item..item))
    }

    /**
     * Alias for <code>addRule(item)</code>.
     *
     * @param item
     */
    void leftShift(item) {
        addRule(item)
    }

    boolean overlaps(Lobe other) {
        boolean overlap = false
        if (this.is(ANY) || other.is(ANY)) {
            overlap = true

        } else if (this == other) {
            overlap = true

        } else {
            def overlappedRule = rules.find { rule ->
                other.rules.find { orule -> rangesOverlap(rule, orule) }
            }

            if (overlappedRule) {
                overlap = true
            }
        }
        overlap
    }

    private static boolean rangesOverlap(Range a, Range b) {
        (a.to >= b.from && a.to <= b.to) || (b.to >= a.from && b.to <= a.to)
    }
}