/*
 * Copyright (C) 2017 Christopher J. Stehno <chris@stehno.com>
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
package com.stehno.vanilla.matcher

import groovy.transform.TypeChecked

/**
 * Defines the DSL for the ObjectMatcher
 */
@TypeChecked
interface ObjectMatcherDsl {

    /**
     * Configures specific matchers (Closures) to be used for specific property types.
     * The provided matcher configurations will be appended (and may overwrite) the default matcher
     * set; however, if the clean parameter is specified as "true", the provided map of matchers will
     * be used as the inclusive configuration set.
     *
     * The matching closure must accept one argument, the object being matched.
     *
     * @param matchers the type matchers to be used
     * @param clean whether or not to replace all existing matchers with the provided set (defaults to false)
     * @return the ObjectMatcherDsl instance
     */
    ObjectMatcherDsl typeMatchers(Map<Class, Object> matchers, boolean clean)

    ObjectMatcherDsl typeMatchers(Map<Class, Object> matchers)

    ObjectMatcherDsl typeMatcher(Class type, Object matcher)

    /**
     * Configures matchers for specific properties of the object being matched. Specific property matchers will override any
     * configured type matchers.
     *
     * The matcher closure must accept one argument, the object being matched.
     *
     * @param matchers the property matchers to be used
     * @return the ObjectMatcherDsl instance
     */
    ObjectMatcherDsl propertyMatchers(Map<String, Object> matcher)

    ObjectMatcherDsl propertyMatcher(String name, Object matcher)
}
