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
package com.stehno.vanilla.overlap

/**
 * Lobes are the discrete dimensions of an overlap comparison, e.g. a single variable of an object used to build the whole overlap-comparison basis.
 * The actual overlap comparison will generally be made up of more than one Lobe instance.
 *
 * When being checked for overlap, a Lobe is a single unit of comparison, meaning that the whole scope of the Lobe is compared as a whole, even if
 * the Lobe itself is made up of discrete parts.
 *
 * It is generally advisable that all Lobe instances have a proper <code>equals()</code> implementation since equivalent objects overlap by
 * definition.
 *
 * In general, a Lobe instance should only be compared with other Lobe instances of the same type.
 */
interface Lobe {

    /**
     * This Lobe should be used in a "wildcard" case where overlap should be true no matter what the contents of the other Lobe being tested.
     */
    static final Lobe ANY = new Lobe() {
        @Override
        boolean overlaps(Lobe other) {
            return true
        }
    }

    /**
     * Determines whether or not this Lobe overlaps with the given Lobe.
     *
     * @param other the Lobe being compared to this Lobe
     * @return true, if the two Lobes overlap
     */
    boolean overlaps( Lobe other )
}
