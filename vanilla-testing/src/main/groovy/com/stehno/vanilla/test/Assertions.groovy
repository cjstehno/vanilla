/*
 * Copyright (c) 2014 Christopher J. Stehno
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

import static java.util.Calendar.*

/**
 *  Reusable assertions for testing common coding constructs.
 */
class Assertions {

    /**
     * Tests the given set objects for proper equals(Object) and hashCode() behavior. The "same" objects should all be equivalent, but not the same
     * instance of the object. The "different" object should not be equivalent to the other three.
     *
     * @param sameA
     * @param sameB
     * @param sameC
     * @param differentA
     *
     * @see "Effective Java" - Joshua Bloch
     */
    @SuppressWarnings(['ExplicitCallToEqualsMethod', 'ComparisonWithSelf'])
    static void assertValidEqualsAndHashcode(final Object sameA, final Object sameB, final Object sameC, final Object differentA) {
        assert sameA.equals(sameA)
        assert sameA.hashCode() == sameA.hashCode()
        assert sameA.hashCode() == sameB.hashCode()

        assert sameA.equals(sameB)
        assert sameB.equals(sameA)

        assert sameA.equals(differentA)
        assert differentA.equals(sameA)

        assert sameA.hashCode() != differentA.hashCode()

        assert sameA.equals(sameB)
        assert sameB.equals(sameC)
        assert sameC.equals(sameA)

        assert sameA.equals(sameB)
        assert sameA.equals(sameB)

        assert sameA.equals(differentA)
        assert sameA.equals(differentA)

        assert sameA.hashCode() == sameB.hashCode()
        assert sameA.hashCode() == sameB.hashCode()

        assert sameA.equals(null)
    }

    /**
     * Asserts that the toString() method of a given object returns a String containing the given properties. Useful when using the Groovy Apache
     * Commons toString() helpers.
     *
     * @param obj the object
     * @param contains a map of property names and values to be verified
     */
    static void assertToString(final Object obj, Map<String, Object> contains) {
        String string = obj as String
        contains.each { name, value ->
            assert string.contains("$name=$value")
        }
    }

    /**
     * Asserts that the actual value matches the expected values. The expected map values correspond to the properties of the actual object; however,
     * if a closure is passed in rather than a value, the closure will be given the actual value for additional validation checking.
     *
     * <pre>
     * assertMatches(name:'Bob', age:{a-> a > 20}, person)
     * </pre>
     *
     * @param expected map of expected values or closures to match
     * @param actual the actual data object being tested
     */
    static void assertMatches(Map expected, actual) {
        expected.each { k, v ->
            def actualVal = actual[k]
            if (v instanceof Closure) {
                v(actualVal)
            } else {
                assert v == actualVal, "$k: expected $v but was $actualVal"
            }
        }
    }

    /**
     * A loose assertion of a Date object that simply ensures that the date is sometime today. Potential for error exists when testing a time that
     * may overlap midnight.
     *
     * @param date the date to be tested
     */
    static void assertToday(final Date date) {
        def cal = date.toCalendar()
        def now = new Date().toCalendar()

        assert (cal.get(YEAR) == now.get(YEAR)) && (cal.get(WEEK_OF_YEAR) == now.get(WEEK_OF_YEAR)) && (cal.get(DAY_OF_WEEK) == now.get(DAY_OF_WEEK))
    }
}