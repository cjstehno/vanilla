package com.stehno.vanilla.test

import groovy.transform.TypeChecked

/**
 * Defines the DSL for the PropertyRandomizer.
 */
@TypeChecked
interface RandomizerDsl {

    /**
     * Configures the randomizer with property types which will be ignored by the randomization.
     *
     * @param types the property types to be ignored
     * @return the RandomizerDsl instance
     */
    RandomizerDsl ignoringTypes(Class... types)

    /**
     * Configures the randomizer with the names of properties to be ignored by the randomization.
     *
     * @param props the names of properties to be ignored
     * @return the RandomizerDsl instance
     */
    RandomizerDsl ignoringProperties(String... props)

    /**
     * Configures specific randomizers (Closures) to be used for specific property types.
     * The provided randomizer configurations will be appended (and may overwrite) the default randomizer
     * set; however, if the clean parameter is specified as "true", the provided map of randomizers will
     * be used as the inclusive configuration set.
     *
     * The randomizer closure may accept zero, one, or two parameters - where the parameters are the random number generator (Random) as the first
     * parameter, and the instance being populated as the second parameter.
     *
     * @param randomizers the type randomizers to be used
     * @param clean whether or not to replace all existing randomizers with the provided set (defaults to false)
     * @return the RandomizerDsl instance
     */
    RandomizerDsl typeRandomizers(Map<Class, Object> randomizers, boolean clean)

    RandomizerDsl typeRandomizers(Map<Class, Object> randomizers)

    RandomizerDsl typeRandomizer(Class type, Object randomizer)

    /**
     * Configures randomizers for specific properties of the object being randomized. Specific property randomizations
     * will override any configured type randomizers.
     *
     * The randomizer closure may accept zero, one, or two parameters - where the parameters are the random number generator (Random) as the first
     * parameter, and the property map of the instance being populated as the second parameter.
     *
     * @param randomizers the property randomizers to be used
     * @return the RandomizerDsl instance
     */
    RandomizerDsl propertyRandomizers(Map<String, Object> randomizers)

    RandomizerDsl propertyRandomizer(String name, Object randomizer)
}
