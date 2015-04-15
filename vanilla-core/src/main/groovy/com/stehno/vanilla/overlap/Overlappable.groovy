package com.stehno.vanilla.overlap

/**
 * An Overlappable object is one that can be compared with another Overlappable object of the same type to determine whether the to objects
 * overlap/intersect along a specific set of comparable dimensions.
 *
 * It is strongly suggested that Overlappable implementations also implement a proper equals() method and use it in the determination of overlap, as
 * two objects that are equal are overlapping by definition.
 */
interface Overlappable {

    /**
     * Determines whether or not the given Overlappable object overlaps with this Overlappable object across the appropriate dimensions for the
     * implementing type.
     *
     * Generally, compared Overlappable objects should be of the same type.
     *
     * @param other the Overlappable being compared with this Overlappable
     * @return true if there is overlap
     */
    boolean overlaps( Overlappable other )
}