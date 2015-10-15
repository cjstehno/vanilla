package com.stehno.vanilla.jdbc

/**
 * FIXME: document me
 */
interface ResultSetMapperBuilderDsl {

    FieldMapping map(String propertyName)

    void ignore(String... propertyNames)
}
