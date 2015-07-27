package com.stehno.vanilla.test

import static com.stehno.vanilla.test.Randomizers.*
import static groovy.lang.Closure.DELEGATE_FIRST

/**
 * Utility for injecting random property values into POJOs and POGOs for testing. This utility may be used directly as a builder or as a DSL for
 * configuring a randomizer.
 *
 * If the target type matches one of the configured class randomizers, that randomizer will be used to randomize the object itself
 * rather than it's internal fields. This allows the randomization of simple types.
 *
 * An example usage, would be similar to the following:
 *
 * ```
 * def rando = randomize(Person){
 *     typeRandomizers(
 *         (Date):{ new Date() },
 *         (Pet): { randomize(Pet).one() }
 *     )
 * }
 * def instance = rando.one()
 * ```
 *
 * More information may be found in my blog post, "[Property Randomization for Testing](http://coffeaelectronica.com/blog/2015/property-randomization.html)"
 */
class PropertyRandomizer {

    private final List<Class> ignoredTypes = [Class]
    private final List<String> ignoredProperties = []

    @SuppressWarnings('InsecureRandom')
    private final Random rng = new Random()

    private final Class target

    private final Map<Class, Object> classRandomizers = [
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

    private final Map<String, Object> nameRandomizers = [:]

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
    PropertyRandomizer typeRandomizers(Map<Class, Object> randomizers, boolean clean = false) {
        if (clean) classRandomizers.clear()
        classRandomizers.putAll(randomizers)
        this
    }

    PropertyRandomizer typeRandomizer(Class type, Object randomizer) {
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
    PropertyRandomizer propertyRandomizers(Map<String, Object> randomizers) {
        nameRandomizers.putAll(randomizers)
        this
    }

    PropertyRandomizer propertyRandomizer(String name, Object randomizer) {
        nameRandomizers.put(name, randomizer)
        this
    }

    /**
     * Used to retrieve a single randomized instance of the target class. Each call to this method will
     * return a new randomized object.
     *
     * The randomized instance of the object will also be convertible to a Map using the "asType(Class)" method or using the
     * "as Map" operation. This map will be immutable and only contain the randomized properties. Simple types whose class
     * randomizers are found directly in the configured randomizers will not have this added functionality.
     *
     * @return a single randomized instance of the target class
     */
    def one() {
        def inst = target.newInstance()

        if (classRandomizers.containsKey(target)) {
            inst = callRandomizer(inst, classRandomizers[target])

        } else {
            def props = [:]

            target.metaClass.properties.each { p ->
                if (p.setter && !(p.type in ignoredTypes) && !(p.name in ignoredProperties)) {
                    def randomizer = nameRandomizers[p.name] ?: classRandomizers[p.type]

                    if (!randomizer) throw new IllegalStateException("No randomizer configured for property (${p.type.simpleName} ${p.name}).")

                    def value = callRandomizer(inst, randomizer)
                    inst[p.name] = value
                    props[p.name] = value
                }
            }

            def originalAsType = target.metaClass.getMetaMethod('asType', [Class] as Class[])

            inst.metaClass.asType = { Class type ->
                if (type.isAssignableFrom(Map)) {
                    return props.asImmutable()
                } else {
                    return originalAsType.invoke(delegate, [type] as Object[])
                }
            }
        }

        inst
    }

    private callRandomizer(instance, Object randomizer) {
        if (randomizer instanceof PropertyRandomizer) {
            return randomizer.one()

        } else if (randomizer instanceof Closure) {
            switch (randomizer.maximumNumberOfParameters) {
                case 2:
                    return randomizer.call(rng, instance)
                case 1:
                    return randomizer.call(rng)
                default:
                    return randomizer.call()
            }

        } else {
            throw new IllegalArgumentException("Randomizers of type ${randomizer.class} are not supported.")
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
