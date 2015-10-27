package com.stehno.vanilla.jdbc

import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.*

/**
 * FIXME: document me
 */
@Target(value = [ElementType.FIELD, ElementType.METHOD])
@Retention(RetentionPolicy.SOURCE)
@Documented
@GroovyASTTransformationClass(classes = [JdbcMapperTransform])
@interface JdbcMapper {

    /**
     *
     * @return
     */
    Class value()

    /**
     * The ResultSetMapper DSL closure.
     */
    Class config() default {}

    /**
     * FIXME: Document
     */
    MappingStyle style() default MappingStyle.IMPLICIT
}