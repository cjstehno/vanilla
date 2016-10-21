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
package com.stehno.vanilla.matcher

import com.stehno.vanilla.mapper.ObjectMapperDsl

import static groovy.lang.Closure.DELEGATE_FIRST

/**
 * An Object Matcher is a utility for determining whether or not an Object of a specified type matches a set of criteria configured via
 * DSL.
 *
 * A matcher could implement actual equivalence, or it could ensure that an object is within a specified set of tolerance levels.
 *
 * An object matcher is defined using a simple DSL:
 *
 * <code><pre>
 *     ObjectMatcher matcher = ObjectMatcher.matcher(Person){
 *         typeMatcher String, { v-> v != null }
 *         propertyMatchers([
 *             age: { v -> v > 18 && v < 40 },
 *             employed: { v-> v }
 *         ])
 *     }
 *
 *     boolean matches = matcher.matches(somePerson)
 *     float score = matcher.score(somePerson)
 * </pre></code>
 *
 * All properties and property types defined in the configuration will be used as the basis of match comparison, all others will
 * be ignored.
 */
class ObjectMatcher implements ObjectMatcherDsl {

    private final Map<Class, Object> classMatchers = [:]
    private final Map<String, Object> propertyMatchers = [:]
    private final Class target

    private ObjectMatcher(final Class target) {
        this.target = target
    }

    /**
     * Configures an ObjectMapper for the specified target type using the DSL.
     *
     * @param target the target type
     * @param closure the DSL configuration closure
     * @return the configured ObjectMapper instance
     */
    static ObjectMatcher matcher(Class target, @DelegatesTo(value = ObjectMapperDsl, strategy = DELEGATE_FIRST) Closure closure) {
        def matcher = new ObjectMatcher(target)

        closure.delegate = matcher
        closure.resolveStrategy = DELEGATE_FIRST
        closure()

        matcher
    }

    @Override
    ObjectMatcher typeMatchers(Map<Class, Object> matchers, boolean clean = false) {
        if (clean) {
            classMatchers.clear()
        }

        classMatchers.putAll(matchers)
        this
    }

    @Override
    ObjectMatcher typeMatcher(Class type, Object matcher) {
        classMatchers.put(type, matcher)
        this
    }

    @Override
    ObjectMatcher propertyMatchers(Map<String, Object> matchers) {
        propertyMatchers.putAll(matchers)
        this
    }

    @Override
    ObjectMatcher propertyMatcher(String name, Object matcher) {
        propertyMatchers.put(name, matcher)
        this
    }

    /**
     * Used to determine whether or not the specified object matches the configured criteria. Configured match criteria may be overridden
     * at runtime for this call by specifying the property name and criteria in the overrides map.
     *
     * @param overrides an optional Map of property names to criteria closures which will override existing config with the same property name
     * @param obj the object being tested
     * @return true if the object being tested matched the configured criteria
     */
    boolean matches(final Map<String, Object> overrides = [:], final Object obj) {
        // apply any call-based property overrides
        def activePropertyMatchers = propertyMatchers + overrides

        target.metaClass.properties.findAll { MetaProperty p -> p.setter }.every { MetaProperty p ->
            def matcher = activePropertyMatchers[p.name] ?: classMatchers[p.type]
            return matcher ? callMatcher(matcher, obj."get${p.name.capitalize()}"()) : true
        }
    }

    /**
     * Used to calculate the match percentage for the specified object. This percentage is based on the number of test criteria and
     * the number of test criteria which are true.
     *
     * @param overrides an optional Map of property names to criteria closures which will override existing config with the same property name
     * @param obj the object being tested
     * @return the match percentage
     */
    float score(final Map<String, Object> overrides = [:], Object obj) {
        // apply any call-based property overrides
        def activePropertyMatchers = propertyMatchers + overrides

        int hits = 0
        int count = 0

        target.metaClass.properties.findAll { MetaProperty p -> p.setter }.each { MetaProperty p ->
            def matcher = activePropertyMatchers[p.name] ?: classMatchers[p.type]
            if (matcher) {
                boolean m = callMatcher(matcher, obj."get${p.name.capitalize()}"())
                if (m) {
                    hits++
                }
                count++

            } else {
                true
            }
        }

        (hits as float) / (count as float)
    }

    private boolean callMatcher(final Object matcher, final Object object) {
        if (matcher instanceof ObjectMatcher) {
            return matcher.matches(object)

        } else if (matcher instanceof Closure) {
            return matcher.call(object)

        } else {
            throw new IllegalArgumentException("Matchers of type ${matcher.class} are not supported.")
        }
    }
}