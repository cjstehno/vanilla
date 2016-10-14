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
package com.stehno.vanilla.util

import groovy.transform.Immutable

import static com.stehno.vanilla.util.TimeSpanUnit.fromAbbreviation

/**
 * Immutable representation of a time span, parsable from a string (e.g. "1 hour").
 */
@Immutable(knownImmutableClasses=[Number])
class TimeSpan {

    // TODO: this might make an interesting extension to Number (e.g. 12.hours)

    Number value
    TimeSpanUnit unit

    /**
     * Parses the provided string to create a TimeSpan value. The format of the string must be a number and a unit matching a TimeSpanUnit value.
     *
     * @param value the string value to be parsed
     * @return the parsed TimeSpan
     */
    static TimeSpan parse(String value){
        def digits = []
        def unit = []

        value.trim().toCharArray().each { c->
            if(c.isDigit() || c== '.'){
                digits << c
            } else {
                unit << c
            }
        }

        new TimeSpan(new BigDecimal(digits.join()), fromAbbreviation(unit.join().trim()))
    }

    /**
     * Formats the TimeSpan as a String using the TimeSpanUnit.abbreviate() method.
     *
     * @return a formatted string representation of the TimeSpan
     */
    String format(){
        "${value} ${unit.abbreviate(value > 1 || value < 1)}"
    }

    TimeSpan plus(TimeSpan ts){
        assert unit == ts.unit // no conversion at this point
        new TimeSpan(value + ts.value, unit)
    }

    TimeSpan minus(TimeSpan ts){
        assert unit == ts.unit // no conversion at this point
        new TimeSpan(value - ts.value, unit)
    }

    TimeSpan multiply(Number num){
        new TimeSpan(value*num, unit)
    }

    TimeSpan div(Number num){
        new TimeSpan(value/num, unit)
    }

    TimeSpan next(){
        new TimeSpan(++value, unit)
    }

    TimeSpan previous(){
        new TimeSpan(--value, unit)
    }
}

