package com.stehno.vanilla.test.jdbc

/**
 * Defines the operations available to the ResultSet DSL - used to define MockResultSet configurations.
 */
interface ResultSetDsl {

    void columns(String... colNames)

    void data(Object... colValues)

    void object(Object objValues)

    void map(Map<String, Object> mapValues)
}
