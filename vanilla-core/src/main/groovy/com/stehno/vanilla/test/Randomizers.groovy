/*
 * Copyright (C) 2016 Christopher J. Stehno <chris@stehno.com>
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
package com.stehno.vanilla.test

import groovy.transform.TypeChecked

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.ThreadLocalRandom

import static groovy.transform.TypeCheckingMode.SKIP

/**
 * A collection of useful randomizers for use with the PropertyRandomizer. All of the methods return Closures which will generate random values
 * when called.
 */
@TypeChecked
class Randomizers {

    private static final String CHARS = 'abcdefghijklmnopqrstuvwxyz0123456789'

    /**
     * Produces a Closure which will always return the specified value when called.
     *
     * @param value the value to be returned
     * @return the specified value
     */
    static <T> Closure<T> constant(final T value) {
        return { r -> value }
    }

    /**
     * Generates the random value produced by the given randomizer. This method is generally for simple single-case random uses, provided as a
     * convenience.
     *
     * @param randomizer the randomizer closure to be called
     * @return the generated random value
     */
    static <T> T random(Closure<T> randomizer) {
        randomizer.call(ThreadLocalRandom.current())
    }

    /**
     * Produces a Closure which will generate a random list of random values provided by the supplied randomizer.
     *
     * @param size the randomized size range of the list
     * @param valueRandomizer the Closure used to randomize the generated list values
     * @return the list-generating closure
     */
    static <T> Closure<List<T>> forList(IntRange size = 0..10, Closure<T> valueRandomizer) {
        return { Random r, inst ->
            List<T> list = []
            nextInt(r, size.from as int, size.to as int).times {
                if (valueRandomizer.maximumNumberOfParameters > 1) {
                    list << valueRandomizer.call(r, inst)
                } else {
                    list << valueRandomizer.call(r)
                }
            }
            list
        }
    }

    /**
     * Produces a Closure which will generate a random set of random values provided by the supplied randomizer.
     *
     * @param size the randomized size range of the set
     * @param valueRandomizer the Closure used to randomize the generated set values
     * @return the set-generating closure
     */
    static <T> Closure<Set<T>> forSet(IntRange size = 0..10, Closure<T> valueRandomizer) {
        return { Random r, inst ->
            Set<T> coll = [] as Set<T>
            nextInt(r, size.from as int, size.to as int).times {
                if (valueRandomizer.maximumNumberOfParameters > 1) {
                    coll << valueRandomizer.call(r, inst)
                } else {
                    coll << valueRandomizer.call(r)
                }
            }
            coll
        }
    }

    /**
     * Produces a Closure which will generate a random array of random values provided by the supplied randomizer.
     *
     * @param size the randomized size range of the array
     * @param valueRandomizer the Closure used to randomize the generated array values
     * @return the array-generating closure
     */
    static <T> Closure<T[]> forArray(IntRange size = 0..10, Closure<T> valueRandomizer) {
        return { Random r, inst ->
            List<T> coll = []
            nextInt(r, size.from as int, size.to as int).times {
                if (valueRandomizer.maximumNumberOfParameters > 1) {
                    coll << valueRandomizer.call(r, inst)
                } else {
                    coll << valueRandomizer.call(r)
                }
            }
            coll.toArray() as T[]
        }
    }

    /**
     * Produces a Closure which will generate a random string.
     *
     * @param size the size range of the string
     * @return the string-generating closure
     */
    static Closure<String> forString(IntRange size = (0..10)) {
        return { Random rng ->
            def chars = []
            nextInt(rng, size.from as int, size.to as int).times {
                chars << CHARS[rng.nextInt(CHARS.size())]
            }
            chars.join('')
        }
    }

    /**
     * Produces a Closure which will generate a random integer.
     *
     * @param size the size range of the integer
     * @return the integer-generating closure
     */
    static Closure<Integer> forInteger(IntRange range = (Integer.MIN_VALUE..Integer.MAX_VALUE)) {
        return { Random rng ->
            range ? nextInt(rng, range.from as int, range.to as int) : rng.nextInt()
        }
    }

    /**
     * Produces a Closure which will generate a random long.
     *
     * @param size the size range of the long
     * @return the long-generating closure
     */
    static Closure<Long> forLong(Range<Long> range = (Long.MIN_VALUE..Long.MAX_VALUE)) {
        return { Random rng ->
            nextLong(rng, range.from, range.to)
        }
    }

    /**
     * Produces a Closure which will generate a random boolean.
     *
     * @return the boolean-generating closure
     */
    static Closure<Boolean> forBoolean() {
        return { Random rng ->
            rng.nextBoolean()
        }
    }

    /**
     * Produces a Closure which will generate a random byte.
     *
     * @return the byte-generating closure
     */
    static Closure<Byte> forByte() {
        return { Random rng ->
            byte[] bytes = new byte[1]
            rng.nextBytes(bytes)
            bytes[0]
        }
    }

    /**
     * Produces a Closure which will generate a random byte array.
     *
     * @param count the size range of the array
     * @return the byte array-generating closure
     */
    static Closure<byte[]> forByteArray(IntRange count = 0..10) {
        return { Random r ->
            byte[] bytes = new byte[nextInt(r, count.from as int, count.to as int)]
            r.nextBytes(bytes)
            bytes
        }
    }

    /**
     * Produces a Closure which will generate a random short.
     *
     * @param range the allowed value range of the random short
     * @return the short-generating closure
     */
    static Closure<Short> forShort(Range<Short> range = Short.MIN_VALUE..Short.MAX_VALUE) {
        return { Random rng ->
            nextShort(rng, range.from as short, range.to as short)
        }
    }

    /**
     * Produces a Closure which will generate a random float.
     *
     * @param range the allowed value range of the random float
     * @return the float-generating closure
     */
    static Closure<Float> forFloat(Range<Float> range = Float.MIN_VALUE..Float.MAX_VALUE) {
        return { Random rng ->
            nextFloat(rng, range.from as float, range.to as float)
        }
    }

    /**
     * Produces a Closure which will generate a random double.
     *
     * @param range the allowed value range of the random double
     * @return the double-generating closure
     */
    static Closure<Double> forDouble(Range<Double> range = Double.MIN_VALUE..Double.MAX_VALUE) {
        return { Random rng ->
            nextDouble(rng, range.from, range.to)
        }
    }

    /**
     * Produces a Closure which will generate a random Date.
     *
     * @param range the allowed value range of the random Date
     * @return the Date-generating closure
     */
    static Closure<Date> forDate(Range<Date> range = (new Date(0)..new Date(Long.MAX_VALUE))) {
        return { Random rng ->
            new Date(nextLong(rng, range?.from?.time ?: 0, range?.to?.time ?: Long.MAX_VALUE))
        }
    }

    /**
     * Produces a Closure which will generate a random char.
     *
     * @return the char-generating closure
     */
    static Closure<Character> forChar() {
        return { Random rng ->
            CHARS[rng.nextInt(CHARS.size())] as char
        }
    }

    /**
     * Produces a Closure which will generate a random enum value selected from all the values of the provided enum.
     *
     * @param e the enum
     * @return the enum-generating closure
     */
    @TypeChecked(SKIP)
    static <T> Closure<T> forEnum(T e) {
        return { Random rng ->
            def vals = e.values()
            vals[rng.nextInt(vals.size())]
        }
    }

    /**
     * Produces a Closure which will generate a random LocalDateTime.
     *
     * @param range the allowed value range of the random LocalDateTime
     * @return the LocalDateTime-generating closure
     */
    static Closure<LocalDateTime> forLocalDateTime(Range<LocalDateTime> range = null, ZoneOffset offset = ZoneOffset.UTC) {
        return { Random rng ->
            long lower = range?.from ? range.from.toInstant(offset).toEpochMilli() : 0
            long upper = range?.to ? range.to.toInstant(offset).toEpochMilli() : Long.MAX_VALUE

            return LocalDateTime.ofInstant(Instant.ofEpochMilli(nextLong(rng, lower, upper)), offset)
        }
    }

    /**
     * Produces a Closure which will select a random value from the provided collection.
     *
     * @param collection the value collection
     * @return the value-selecting closure
     */
    static <X> Closure<X> forCollection(Collection<X> collection) {
        return { Random rng ->
            collection[rng.nextInt(collection.size())]
        }
    }

    /**
     * Produces a Closure which will select a random value from the provided array.
     *
     * @param collection the value array
     * @return the value-selecting closure
     */
    static <X> Closure<X> forCollection(X[] collection) {
        return { Random rng ->
            collection[rng.nextInt(collection.length)]
        }
    }

    /**
     * Produces a Closure which will select a random value from the provided argument list.
     *
     * @param items the available items
     * @return the value-selecting closure
     */
    static <X> Closure<X> forItems(X... items) {
        return { Random rng ->
            items[rng.nextInt(items.length)]
        }
    }

    /**
     * Provides a Closure for selecting a random value from the provided range.
     *
     * @param range the range of allowed values
     * @return the randomizer closure (accepts Random as argument)
     */
    static Closure<Object> forRange(Range range) {
        return { Random rng ->
            range[rng.nextInt(range.size())]
        }
    }

    private static short nextShort(Random r, short min, short max) {
        nextDouble(r, min, max) as short
    }

    private static float nextFloat(Random r, float min, float max) {
        nextDouble(r, min, max) as float
    }

    private static Integer nextInt(Random r, int min, int max) {
        nextDouble(r, min, max) as int
    }

    private static long nextLong(Random r, long min, long max) {
        nextDouble(r, min, max) as long
    }

    private static double nextDouble(Random r, double min, double max) {
        (min + ((max - min) * r.nextDouble()))
    }
}