package com.stehno.vanilla.jdbc

import groovy.transform.TypeChecked

import java.sql.ResultSet

import static com.stehno.vanilla.jdbc.MappingStyle.IMPLICIT

/**
 * FIXME: document me
 */
@TypeChecked
class ResultSetMapper {

    // FIXME: using should be for transforming or extracting
    // FIXME: having from and using should be considered

    private static final Collection<String> DEFAULT_IGNORED = ['class'].asImmutable()
    private final ResultSetMapperBuilder builder

    ResultSetMapper(ResultSetMapperBuilder builder) {
        this.builder = builder
    }

    // FIXME: document and test this "as RowMapper" - similar for ResultSetExtractor?
    def mapRow(ResultSet rs, int rownum){
        call rs
    }

    def call(ResultSet rs) {
        def instanceProps = [:]
        MetaClass mappedMeta = builder.mappedType.metaClass

        if (builder.style == IMPLICIT) {
            def ignored = DEFAULT_IGNORED + builder.ignored()

            mappedMeta.properties.findAll { MetaProperty mp ->
                !(mp.name in ignored) && isWritable(mappedMeta, mp.name, mp.type)
            }.each { MetaProperty mp ->
                FieldMapping mapping = builder.findMapping(mp.name)
                if (mapping) {
                    instanceProps[mp.name] = mapping.extractor.call(rs)
                } else {
                    throw new IllegalArgumentException("Missing mapping for field (${mp.name}).")
                }
            }

        } else {
            // loop through mappings and map data
            builder.mappings().each { FieldMapping mapping ->
                instanceProps[mapping.propertyName] = mapping.extractor.call(rs)
            }
        }

        builder.mappedType.newInstance(instanceProps)
    }

    private static boolean isWritable(MetaClass meta, String name, Class argType) {
        return meta.getMetaMethod(MetaProperty.getSetterName(name), [argType] as Object[])
    }
}
