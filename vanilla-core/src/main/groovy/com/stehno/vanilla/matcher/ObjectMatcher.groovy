package com.stehno.vanilla.matcher

import com.stehno.vanilla.mapper.ObjectMapperDsl

import static groovy.lang.Closure.DELEGATE_FIRST

/**
 * Created by cjstehno on 2/29/16.
 */
class ObjectMatcher implements ObjectMatcherDsl {

    private final Map<Class, Object> classMatchers = [:]
    private final Map<String, Object> propertyMatchers = [:]
    private final Class target

    private ObjectMatcher(final Class target) {
        this.target = target
    }

    static ObjectMatcher matcher(Class target, @DelegatesTo(value = ObjectMapperDsl, strategy = DELEGATE_FIRST) Closure closure) {
        def matcher = new ObjectMatcher(target)

        closure.delegate = matcher
        closure.resolveStrategy = DELEGATE_FIRST
        closure()

        matcher
    }

    ObjectMatcher typeMatchers(Map<Class, Object> matchers, boolean clean = false) {
        if (clean) {
            classMatchers.clear()
        }

        classMatchers.putAll(matchers)
        this
    }

    ObjectMatcher typeMatcher(Class type, Object matcher) {
        classMatchers.put(type, matcher)
        this
    }

    ObjectMatcher propertyMatchers(Map<String, Object> matchers) {
        propertyMatchers.putAll(matchers)
        this
    }

    ObjectMatcher propertyMatcher(String name, Object matcher) {
        propertyMatchers.put(name, matcher)
        this
    }

    boolean matches(final Map<String, Object> overrides = [:], final Object obj) {
        // apply any call-based property overrides
        def activePropertyMatchers = propertyMatchers + overrides

        target.metaClass.properties.findAll { MetaProperty p -> p.setter }.every { MetaProperty p ->
            def matcher = activePropertyMatchers[p.name] ?: classMatchers[p.type]
            return matcher ? callMatcher(matcher, obj."get${p.name.capitalize()}"()) : true
        }
    }

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


