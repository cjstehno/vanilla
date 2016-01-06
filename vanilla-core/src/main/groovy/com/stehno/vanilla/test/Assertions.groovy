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

import static java.util.Calendar.*

/**
 *  Reusable assertions for testing common coding constructs.
 */
final class Assertions {

    /**
     * Tests the given set objects for proper equals(Object) and hashCode() behavior.
     *
     * The instances used for A, B, and C should be instances of the same class, with equivalent data, but not being
     * references to the same instance of the object.
     *
     * The instance used for D should be an instance of the same class but with non-equivalent data.
     *
     * @param objA an equivalent instance
     * @param objB an equivalent instance
     * @param objC an equivalent instance
     * @param objD a non-equivalent instance
     * @returns true if all the contract assertions pass
     */
    @SuppressWarnings(['ExplicitCallToEqualsMethod', 'ComparisonWithSelf'])
    static boolean assertValidEqualsAndHashcode(final Object objA, final Object objB, final Object objC, final Object objD) {
        // Symmetry: For two references, a and b, a.equals(b) if and only if b.equals(a)

        assert objA.equals(objB), 'Object equality (A:B) is not symmetrical.'
        assert objB.equals(objA), 'Object equality (B:A) is not symmetrical.'

        assert !objA.equals(objD), 'Object equality (A:D) is not symmetrical.'
        assert !objB.equals(objD), 'Object equality (B:D) is not symmetrical.'
        assert !objC.equals(objD), 'Object equality (C:D) is not symmetrical.'

        assert !objD.equals(objA), 'Object equality (D:A) is not symmetrical.'
        assert !objD.equals(objB), 'Object equality (D:B) is not symmetrical.'
        assert !objD.equals(objC), 'Object equality (D:C) is not symmetrical.'

        // Reflexivity: For all non-null references, a.equals(a)

        assert objA.equals(objA), 'Object equality (A) is not reflexive.'
        assert objB.equals(objB), 'Object equality (B) is not reflexive.'
        assert objC.equals(objC), 'Object equality (D) is not reflexive.'
        assert objD.equals(objD), 'Object equality (D) is not reflexive.'

        // Transitivity: If a.equals(b) and b.equals(c), then a.equals(c)

        assert objA.equals(objC), 'Object equality (A:C) is not transitive.'
        assert objC.equals(objA), 'Object equality (C:A) is not transitive.'
        assert objB.equals(objC), 'Object equality (B:C) is not transitive.'
        assert objC.equals(objA), 'Object equality (C:A) is not transitive.'

        // Consistency with hashCode(): Two equal objects must have the same hashCode() value

        assert objA.hashCode() == objB.hashCode(), 'Object hashCode (A:B) is not consistent.'
        assert objA.hashCode() == objC.hashCode(), 'Object hashCode (A:C) is not consistent.'
        assert objB.hashCode() == objC.hashCode(), 'Object hashCode (B:C) is not consistent.'

        true
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
                assert v(actualVal)
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