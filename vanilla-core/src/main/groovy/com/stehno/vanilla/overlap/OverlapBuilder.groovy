package com.stehno.vanilla.overlap

/**
 * The OverlapBuilder is used to build the overlap comparison by appending the lobes for comparison. This class is generally used inside the overlap
 * method implementation of an Overlappable object.
 *
 * The lobes should always be appended in the same order when comparing.
 *
 * @author cjstehno
 */
class OverlapBuilder {

    // FIXME: consider refactoring this overlap stuff into a trait or two

    private final lobePairs = []

    /**
     * Appends the lobes for comparison.
     *
     * @param lobeA
     * @param lobeB
     * @return
     */
    OverlapBuilder appendLobe(Lobe lobeA, Lobe lobeB) {
        if (!lobeA && !lobeB) {
            throw new IllegalArgumentException('Both lobes cannot be null.')
        }

        lobePairs << [lobeA, lobeB]
        return this
    }

    /**
     * Shortcut helper for appending Comparable items as ComparableLobes.
     *
     * @param comparableA
     * @param comparableB
     * @return
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

        for( def pair : lobePairs ){
            Lobe lobeA = pair[0]
            Lobe lobeB = pair[1]

            if (!lobeA || !lobeB) throw new IllegalArgumentException('Lobes array cannot have null elements.')

            if (!lobeA.overlaps(lobeB)) {
                overlap = false
                break
            }
        }

        return overlap
    }
}