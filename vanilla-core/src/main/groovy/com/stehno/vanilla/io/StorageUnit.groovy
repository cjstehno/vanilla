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
package com.stehno.vanilla.io

import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor

import static java.lang.Math.abs

/**
 * Enum used to denote file size and memory storage size units.
 */
@CompileStatic @TupleConstructor
enum StorageUnit {

    BYTES(0),
    KILOBYTES(1),
    MEGABYTES(2),
    GIGABYTES(3),
    TERABYTES(4)

    final int mult

    /**
     * Converts the given source value to the desired unit.
     *
     * @param sourceValue the source size value
     * @param sourceUnit the source unit
     * @return the converted value in the desired units
     */
    double convert(long sourceValue, StorageUnit sourceUnit) {
        int m = sourceUnit.mult - mult
        if (m == 0) {
            return sourceValue as double
        } else if (m > 0) {
            return (sourceValue * (1024**m)) as double
        } else {
            return (sourceValue / (1024**abs(m))) as double
        }
    }

    /**
     * Converts the given source value to the desired unit as a long approximation (rounded).
     *
     * @param sourceValue
     * @param sourceUnit
     * @return
     */
    long approximate(long sourceValue, StorageUnit sourceUnit) {
        Math.round(convert(sourceValue, sourceUnit))
    }
}
