package com.stehno.vanilla.jdbc.sql.annotation

import com.stehno.vanilla.jdbc.sql.ParameterExtractor
import com.stehno.vanilla.jdbc.sql.transform.InjectSqlUpdateTransform
import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.*

/**
 * FIMXE: document...
 *
 * <boolean|int|void> someUpdateMethod(params...)
 */
@Target(value = [ElementType.METHOD]) @Retention(RetentionPolicy.SOURCE)
@Documented @GroovyASTTransformationClass(classes = [InjectSqlUpdateTransform])
@interface InjectSqlUpdate {

    /**
     * The string of SQL.
     */
    String value()

    /**
     * The name of the instance variable containing a javax.sql.DataSource or groovy.sql.Sql instance to be used as the
     * database connection provider.
     *
     * If none is specified, a search of the enclosing class will search first for an instance of groovy.sql.Sql and then for
     * an instance of javax.sql.DataSource. If neither is found, an exception will be thrown and compilation will fail.
     */
    String source() default ''

    /**
     * The ParameterExtractor used to convert the method input arguments into the SQL input parameters.
     */
    // DefaultParameterExtractor?
    Class<? extends ParameterExtractor> extractor() default {}
}