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
 *
 *  A "wildcard" Lobe is provided for cases when the Lobe should always be considered an overlap.
 */
@ToString(includeFields = true, includeNames = true) @EqualsAndHashCode(includeFields = true)
class ComparableLobe implements Lobe {

    /**
     * This ComparableLobe should be used in a "wildcard" case where overlap should be true no matter what the contents of the other Lobe being
     * tested.
     */
    static final ComparableLobe ANY = new ComparableLobe()

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
    ComparableLobe(Collection items){
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

            if (overlappedRule) overlap = true
        }
        return overlap
    }

    private static boolean rangesOverlap(Range a, Range b) {
        (a.to >= b.from && a.to <= b.to) || (b.to >= a.from && b.to <= a.to)
    }
}