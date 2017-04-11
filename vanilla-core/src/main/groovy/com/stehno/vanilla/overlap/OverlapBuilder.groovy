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
 * The OverlapBuilder is used to build the overlap comparison by appending the lobes for comparison. This class used inside the Overlappable
 * trait; however, it may be used outside of the trait as needed.
 *
 * The lobes should always be appended in the same order when comparing.
 */
class OverlapBuilder {

    private final lobePairs = []

    /**
     * Appends the lobes for comparison.
     *
     * @param lobeA the first lobe
     * @param lobeB the second lobe
     * @return an instance of the OverlapBuilder
     */
    OverlapBuilder appendLobe(Lobe lobeA, Lobe lobeB) {
        if (!lobeA && !lobeB) {
            throw new IllegalArgumentException('Both lobes cannot be null.')
        }

        lobePairs << [lobeA, lobeB]
        this
    }

    /**
     * Shortcut helper for appending Comparable items as ComparableLobes.
     *
     * @param comparableA the first comparable object
     * @param comparableB the second comparable object
     * @return an instance of the OverlapBuilder
     */
    OverlapBuilder appendComparable(comparableA, comparableB) {
        appendLobe(new ComparableLobe(comparableA), new ComparableLobe(comparableB))
    }

    /**
     * Used to calculate whether or not the provided lobes overlap.
     *
     * @return true if the lobes overlap
     */
    boolean overlaps() {
        boolean overlap = lobePairs.size() > 0

        for (def pair : lobePairs) {
            Lobe lobeA = pair[0]
            Lobe lobeB = pair[1]

            if (!lobeA || !lobeB) {
                throw new IllegalArgumentException('Lobes array cannot have null elements.')
            }

            if (!lobeA.overlaps(lobeB)) {
                overlap = false
                break
            }
        }

        overlap
    }
}