package com.stehno.vanilla.overlap

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import spock.lang.Specification

class OverlappableTraitSpec extends Specification {

    def 'overlaps: positive'(){
        when:
        def objA = new SomeObject('abe', 10..100, 100)
        def objB = new SomeObject('abel', 75..123, 100)

        then:
        objA.overlaps(objB)
    }

    def 'overlaps: negative'(){
        when:
        def objA = new SomeObject('abe', 10..100, 100)
        def objB = new SomeObject('abel', 75..123, 101)

        then:
        !objA.overlaps(objB)
    }
}

@Canonical
class SomeObject implements Overlappable {

    String name
    IntRange span
    int value

    @Override
    List getLobes() {
        [new StringLobe(name), span, new ComparableLobe(value)]
    }
}

@Canonical @EqualsAndHashCode
class StringLobe implements Lobe {

    String string

    @Override
    boolean overlaps(Lobe other) {
        string.contains(other.string) || other.string.contains(string)
    }
}