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
package com.stehno.vanilla

/**
 * Similar to the `assert` keyword and the Spring Framework `Assert` utility, these methods provide a means of ensuring that a specified condition
 * is met (or not met) such that an exception is thrown if the affirmation fails. The difference between this and the other mentioned utilities is
 * that the type of exception may be specified here, rather than always throwing a specific exception type.
 */
class Affirmations {

    /**
     * Affirms that the specified condition is true, if it is not, the specified Exception is thrown with the provided message.
     *
     * @param condition the condition being affirmed
     * @param message the optional exception message
     * @param exception the optional exception type, defaults to IllegalArgumentException
     */
    static void affirm(boolean condition, String message = null, Class<? extends Exception> exception = IllegalArgumentException) {
        if (!condition) {
            if (message) {
                throw exception.newInstance([message] as Object[])
            } else {
                throw exception.newInstance()
            }
        }
    }

    /**
     * Affirms that the specified condition is false, if it is not, the specified Exception is thrown with the provided message.
     *
     * @param condition the condition being affirmed
     * @param message the optional exception message
     * @param exception the optional exception type, defaults to IllegalArgumentException
     */
    static void affirmNot(boolean condition, String message = null, Class<? extends Exception> exception = IllegalArgumentException) {
        affirm !condition, message, exception
    }

    /**
     * Affirms that the specified condition is true, if it is not, the specified Exception is thrown with no message.
     *
     * @param condition the condition being affirmed
     * @param exception the exception type, defaults to IllegalArgumentException
     */
    static void affirm(boolean condition, Class<? extends Exception> exception) {
        affirm condition, null, exception
    }

    /**
     * Affirms that the specified condition is false, if it is not, the specified Exception is thrown with no message.
     *
     * @param condition the condition being affirmed
     * @param exception the exception type, defaults to IllegalArgumentException
     */
    static void affirmNot(boolean condition, Class<? extends Exception> exception) {
        affirm !condition, null, exception
    }
}
