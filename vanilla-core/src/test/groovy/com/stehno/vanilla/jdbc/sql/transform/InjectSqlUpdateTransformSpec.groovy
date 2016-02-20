package com.stehno.vanilla.jdbc.sql.transform

import spock.lang.Specification

class InjectSqlUpdateTransformSpec extends Specification {

    private final GroovyShell shell = new GroovyShell()

    def 'simple inject'(){

        when:
        def result = shell.evaluate('''
            package testing

            import com.stehno.vanilla.jdbc.sql.annotation.InjectSqlUpdate
            import groovy.sql.Sql

            class Foo {

                Sql sl

                @InjectSqlUpdate('update abc set a=:a, b=:b where c=:c')
                int updateAbc(int a, String b, long c){}
            }
        ''')
    }
}
