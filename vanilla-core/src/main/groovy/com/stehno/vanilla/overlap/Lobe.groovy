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
     * Determines whether or not this Lobe overlaps with the given Lobe.
     *
     * @param other the Lobe being compared to this Lobe
     * @return true, if the two Lobes overlap
     */
    boolean overlaps( Lobe other )
}
