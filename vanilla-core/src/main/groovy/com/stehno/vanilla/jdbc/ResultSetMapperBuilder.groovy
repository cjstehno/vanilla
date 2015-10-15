package com.stehno.vanilla.jdbc

import static com.stehno.vanilla.jdbc.MappingStyle.IMPLICIT

/**
 * FIXME: document me
 */
class ResultSetMapperBuilder implements ResultSetMapperBuilderDsl {

    final Class mappedType
    final MappingStyle style
    private final Collection<String> ignoredNames = []
    private final Map<String, FieldMapping> mappings = [:]

    ResultSetMapperBuilder(final Class mappedType, final MappingStyle style) {
        this.mappedType = mappedType
        this.style = style
    }

    static ResultSetMapper mapper(Class mappedType, MappingStyle style = IMPLICIT, @DelegatesTo(ResultSetMapperBuilderDsl) Closure closure) {
        ResultSetMapperBuilder builder = new ResultSetMapperBuilder(mappedType, style)

        closure.delegate = builder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()

        builder.build()
    }

    FieldMapping findMapping(String propertyName) {
        mappings[propertyName]
    }

    Collection<String> ignored() {
        ignoredNames.asImmutable()
    }

    boolean isIgnored(String propertyName) {
        propertyName in ignoredNames
    }

    Collection<FieldMapping> mappings() {
        mappings.values().asImmutable()
    }

    ResultSetMapper build(){
        new ResultSetMapper(this)
    }

    FieldMapping map(String propertyName) {
        FieldMapping mapping = new FieldMapping(propertyName)
        mappings[propertyName] = mapping
        mapping
    }

    void ignore(String... propertyNames) {
        this.ignoredNames.addAll(propertyNames)
    }
}

