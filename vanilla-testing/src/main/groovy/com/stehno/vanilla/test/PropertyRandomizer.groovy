package com.stehno.vanilla.test

import static com.stehno.vanilla.test.Randomizers.*
import static groovy.lang.Closure.DELEGATE_FIRST

/**
 * Utility for injecting random property values into POJOs and POGOs for testing. This utility may be used directly as a builder or as a DSL for
 * configuring a randomizer.
 */
class PropertyRandomizer {

    private final List<Class> ignoredTypes = [Class]
    private final List<String> ignoredProperties = []

    @SuppressWarnings('InsecureRandom')
    private final Random rng = new Random()

    private final Class target

    private final Map<Class, Closure> classRandomizers = [
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

    private final Map<String, Closure> nameRandomizers = [:]

    private PropertyRandomizer(Class target) {
        this.target = target
    }

    /**
     * Creates a new randomizer for the provided target class. If a closure parameter is provided, it will be used to configure the randomizer.
     * The returned PropertyRandomizer may continue to be configured with or without a configuration closure.
     *
     * @param target the class to be provided random property values
     * @param closure the closure containing the DSL-style randomizer configuration
     * @return the configured PropertyRandomizer for use or further configuration.
     */
    static PropertyRandomizer randomize(Class target, @DelegatesTo(value = PropertyRandomizer, strategy = DELEGATE_FIRST) Closure closure = null) {
        def rando = new PropertyRandomizer(target)

        if (closure) {
            closure.delegate = rando
            closure.resolveStrategy = DELEGATE_FIRST
            closure()
        }

        rando
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
     * The randomizer closure may accept zero, one, or two parameters - where the parameters are the random number generator (Random) as the first
     * parameter, and the instance being populated as the second parameter.
     *
     * @param randomizers the type randomizers to be used
     * @param clean whether or not to replace all existing randomizers with the provided set (defaults to false)
     * @return the PropertyRandomizer instance
     */
    PropertyRandomizer typeRandomizers(Map<Class, Closure> randomizers, boolean clean = false) {
        if (clean) classRandomizers.clear()
        classRandomizers.putAll(randomizers)
        this
    }

    PropertyRandomizer typeRandomizer(Class type, Closure randomizer) {
        classRandomizers.put(type, randomizer)
        this
    }

    /**
     * Configures randomizers for specific properties of the object being randomized. Specific property randomizations
     * will override any configured type randomizers.
     *
     * The randomizer closure may accept zero, one, or two parameters - where the parameters are the random number generator (Random) as the first
     * parameter, and the instance being populated as the second parameter.
     *
     * @param randomizers the property randomizers to be used
     * @return the PropertyRandomizer instance
     */
    PropertyRandomizer propertyRandomizers(Map<String, Closure> randomizers) {
        nameRandomizers.putAll(randomizers)
        this
    }

    PropertyRandomizer propertyRandomizer(String name, Closure randomizer) {
        nameRandomizers.put(name, randomizer)
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
                def randomizer = nameRandomizers[p.name] ?: classRandomizers[p.type]

                if (!randomizer) throw new IllegalStateException("No randomizer configured for property (${p.type.simpleName} ${p.name}).")

                inst[p.name] = callRandomizer(inst, randomizer)
            }
        }
        inst
    }

    private callRandomizer(instance, Closure randomizer) {
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
