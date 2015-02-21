package com.stehno.vanilla.test

import static com.stehno.vanilla.test.Randomizers.*

/**
 * Builder-style utility for injecting random property values into POJOs and POGOs for testing.
 */
class PropertyRandomizer {

    private final List<Class> ignoredTypes = [Class]
    private final List<String> ignoredProperties = []

    @SuppressWarnings('InsecureRandom')
    private final Random rng = new Random()
    
    private final Class target

    // FIXME: need to handle arrays, collections and maps

    private final Map<Class, Closure> typeRandomizers = [
        (String)   : forString(),
        (int)      : forInteger(80),
        (Integer)  : forInteger(80),
        (byte)     : forByte(),
        (Byte)     : forByte(),
        (short)    : forShort(),
        (Short)    : forShort(),
        (char)     : forChar(),
        (Character): forChar(),
        (long)     : forLong(),
        (Long)     : forLong(),
        (boolean)  : forBoolean(),
        (Boolean)  : forBoolean(),
        (float)    : forFloat(),
        (Float)    : forFloat(),
        (double)   : forDouble(),
        (Double)   : forDouble()
    ]

    private final Map<String, Closure> propertyRandomizers = [:]

    private PropertyRandomizer(Class target) {
        this.target = target
    }

    /**
     * Creates a new randomizer for the provided target class.
     *
     * @param target the class to be provided random property values
     * @return the configured PropertyRandomizer for use or further configuration.
     */
    static PropertyRandomizer randomize(Class target) {
        new PropertyRandomizer(target)
    }

    /**
     * Configures the randomizer with property types which will be ignored by the randomization.
     *
     * @param types the property types to be ignored
     * @return the PropertyRandomizer instance
     */
    PropertyRandomizer ignoringTypes(Class... types) {
        ignoredTypes.addAll(types)
        this
    }

    /**
     * Configures the randomizer with the names of properties to be ignored by the randomization.
     *
     * @param props the names of properties to be ignored
     * @return the PropertyRandomizer instance
     */
    PropertyRandomizer ignoringProperties(String... props) {
        ignoredProperties.addAll(props)
        this
    }

    /**
     * Configures specific randomizers (Closures) to be used for specific property types.
     * The provided randomizer configurations will be appended (and may overwrite) the default randomizer
     * set; however, if the clean parameter is specified as "true", the provided map of randomizers will
     * be used as the inclusive configuration set.
     *
     * The randomizer closure may accept zero or one parameter - where the parameter is the instance of the
     * object being randomized.
     *
     * @param randomizers the type randomizers to be used
     * @param clean whether or not to replace all existing randomizers with the provided set (defaults to false)
     * @return the PropertyRandomizer instance
     */
    PropertyRandomizer withTypeRandomizers(Map<Class, Closure> randomizers, boolean clean = false) {
        if (clean) typeRandomizers.clear()
        typeRandomizers.putAll(randomizers)
        this
    }

    /**
     * Configures randomizers for specific properties of the object being randomized. Specific property randomizations
     * will override any configured type randomizers.
     *
     * The randomizer closure may accept zero or one parameter - where the parameter is the instance of the
     * object being randomized.
     *
     * @param randomizers the property randomizers to be used
     * @return the PropertyRandomizer instance
     */
    PropertyRandomizer withPropertyRandomizers(Map<String, Closure> randomizers) {
        propertyRandomizers.putAll(randomizers)
        this
    }

    /**
     * Used to retrieve a single randomized instance of the target class. Each call to this method will
     * return a new randomized object.
     *
     * @return a single randomized instance of the target class
     */
    def one() {
        def inst = target.newInstance()
        target.metaClass.properties.each { p ->
            if (!(p.type in ignoredTypes) && !(p.name in ignoredProperties)) {
                def randomizer = propertyRandomizers[p.name] ?: typeRandomizers[p.type]

                if (!randomizer) throw new IllegalStateException("No randomizer configured for property (${p.type.simpleName} ${p.name}).")

                inst[p.name] = callRandomizer(inst, randomizer)
            }
        }
        inst
    }

    private callRandomizer(instance, Closure randomizer) {
        // FIXME: update the docs to reflect this!
        switch (randomizer.maximumNumberOfParameters) {
            case 2:
                return randomizer.call(rng, instance)
            case 1:
                return randomizer.call(rng)
            default:
                return randomizer.call()
        }
    }

    /**
     * Used to retrieve multiple randomized instances of the target class (each being different).
     *
     * @param x the number of random instances to be retrieved
     * @return a List of randomized instances of the target class
     */
    List times(int x) {
        def items = []
        x.times {
            items << one()
        }
        items
    }

    /**
     * An override of the multiplication operation as an alias to the "times(int)" method.
     *
     * @param x the number of random instances to be retrieved
     * @return a List of randomized instances of the target class
     */
    List multiply(int x) {
        times(x)
    }
}
