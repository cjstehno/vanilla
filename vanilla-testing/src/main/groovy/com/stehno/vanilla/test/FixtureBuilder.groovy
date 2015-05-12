package com.stehno.vanilla.test

import groovy.transform.Immutable

import static groovy.lang.Closure.DELEGATE_FIRST

/**
 * Builder used to simplify the reuse of testing fixtures based on maps of data.
 *
 * <pre>
 *     Fixture fixture = define {
 *         fix 'MyFixture', [ name:'Alpha', number: 42]
 *         fix 'OtherFixture', [name:'Bravo', number:56]
 *     }
 * </pre>
 */
class FixtureBuilder {

    private final fixtures = [:]

    /**
     * Used to define the fixture maps.
     *
     * @param closure the DSL style closure for creating the fixtures (using "fix" method calls).
     *
     * @return the configured Fixture object
     */
    static Fixture define(@DelegatesTo(FixtureBuilder) Closure closure) {
        def builder = new FixtureBuilder()
        closure.delegate = builder
        closure.resolveStrategy = DELEGATE_FIRST
        closure()
        builder.build()
    }

    /**
     * Called to configure a fixture on the builder.
     *
     * @param fix the identifier for the fixture (must be usable as a map key)
     * @param attrs the map of attributes for the fixture
     * @returns an instance of the builder for chained use
     */
    FixtureBuilder fix(Object fix, Map attrs){
        fixtures[fix] = attrs.asImmutable()
        this
    }

    /**
     * Used to build the resulting Fixture object. This only needs to be called if you are using the builder-style. The DSL will do this
     * automatically.
     *
     * @return the configured Fixture.
     */
    Fixture build(){
        new Fixture(fixtures.asImmutable())
    }
}

/**
 * The immutable container for the configured fixtures.
 */
@Immutable
class Fixture {

    /**
     * The raw map of fixtures. Generally, you should not access this directly; however, it is available.
     */
    Map fixtures

    /**
     * Used to retrieve a field with the given name from the specified fixture.
     *
     * @param name the field name
     * @param fix the fixture key
     * @return the value of the fixture field
     */
    def field(String name, Object fix = null) {
        map(fix)[name]
    }

    /**
     * Used to retrieve the immutable map of fixture data for the specified fixture. If no fixture key is specified, the first configured fixture will be
     * returned. If no fixtures are found, an assertion error is thrown.
     *
     * @param fix the fixture key
     * @return the map of fixture data
     */
    Map<String, Object> map(Object fix = null) {
        def fixture = fix ? fixtures[fix] : (fixtures ? fixtures.entrySet().first().value : null )
        assert fixture, "Fixture($fix) does not exist."
        fixture
    }

    /**
     * Instantiates an instance of the specified type using the fixture data from the designated fixture. The class must allow a map-based
     * constructor.
     *
     * @param type the type to be instantiated
     * @param fix the fixture key
     * @return a new instance of the specified type populated with the fixture data
     */
    def object(Class type, Object fix = null) {
        type.newInstance(map(fix))
    }

    /**
     * Used to verify that the contents of the given object match the properties defined by the specified fixture. Only the properties defined in
     * the fixture will be tested.
     *
     * @param actual the instance to be tested
     * @param fix the fixture key
     * @returns true if all the properties match the expected fixture values
     */
    boolean verify(actual, Object fix = null) {
        map(fix).every { name, val->
            actual[name] == val
        }
    }
}