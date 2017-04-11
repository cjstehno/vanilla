/*
 * Copyright (C) 2017 Christopher J. Stehno <chris@stehno.com>
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
 * Trait providing a means of determining whether or not two objects overlap. In this case, overlapping means that all of the configured Lobes for
 * each object overlap individually.
 *
 * This is based loosely on a blog post that I wrote: http://coffeaelectronica.com/blog/2011/overlap-ccross-multiple-variables.html
 */
trait Overlappable {

    /**
     * Determines whether or not the two objects overlap across all their comparison lobes.
     *
     * @param other the other Overlappable object
     * @return true, if all lobes overlap
     */
    boolean overlaps(Overlappable other) {
        def builder = new OverlapBuilder()

        lobes.eachWithIndex { lobe, idx ->
            if (lobe instanceof Lobe) {
                builder.appendLobe(lobe, other.lobes[idx])
            } else {
                builder.appendComparable(lobe, other.lobes[idx])
            }
        }

        builder.overlaps()
    }

    /**
     * Provides the list of lobes to be used in overlap determination. This list must be have a determinate order and be the same order between the
     * two objects being compared. They do not need to be the same type of object as long as they have the same number of Lobes in the same order.
     *
     * This method will also allow raw Comparable types, which will be wrapped in a ComparableLobe object internally.
     *
     * @return a List of Lobes to be compared
     */
    abstract List getLobes()
}