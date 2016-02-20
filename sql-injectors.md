
@InjectSqlSelect
<type> select(params...)

@InjectSqlInsert
boolean insert(params...)
List<Map<String,Object> insert(params...)

@InjectSqlUpdate
int update(params...)
boolean update(params)


@InjectSqlSelect(value='', strategy=[Spring|Groovy], mapper=<mapper-class>, extractor=<extractor-class>)
* mapper is the ResultSetMapper (for row or result set - like ResultSetExtractor)
* extractor is the parameter extractor that is used to process the input params into the SQL statement
* needs to support row mappers and extractors
* input parameters
* support other variable replacement for things like ordering and limit

@InjectSqlInsert(value='', strategy=[Spring|Groovy], keys='a,b,c', extractor=<extractor-class>)
@InjectSqlUpdate(value='', strategy=[Spring|Groovy], extractor=<extractor-class>)
* needs to handle returned keys
* support other variable replacement for things like ordering and limit

- Spring-JDBC versions by separate annotation or by defined strategy provider
@InjectSpringSqlSelect
@InjectSpringSqlUpdate
-or-
@InjectSqlSelect(strategy=SPRING)

* complex object may be used as input parameter, must either have properties matching expected values or have an 'extractor' defined

thinking separate annotations with shared transformation code to keep spring code in sub-project dependent on core
might want separate insert and update annotation since the code is different
in something like Photopile with secondary inserts, the actual SQL functions would be private and called by the exposed function

- might need to concept of a ResultSetExtractor similar to springs

## InjectSqlUpdate (Groovy)

* checks the SQL string to determine insert or update

### Insert

    def keys = sql.executeInsert(sql, params)
    
- where `keys` is a list of the auto-generated column values for each inserted row

### Update

    int count = sql.executeUpdate(sql, params)

## InjectSqlUpdate (Spring)

* checks the SQL string to determine insert or update

### Insert

* extracts keys

        KeyHolder keyHolder = new GeneratedKeyHolder()

        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(
            sql, sql-types
        )
        factory.returnGeneratedKeys = true
        factory.setGeneratedKeysColumnNames(key-columns)

        int rowCount = jdbcTemplate.update(
            factory.newPreparedStatementCreator(params),
            keyHolder
        )
        
        update keys on object

### Update

    int count = jdbcTemplate.update(sql, params)


## InjectSqlSelect (Groovy)

    sql.eachRow(sql, params){ rs ->
        mapper(rs)
    }
    
## InjectSqlSelect (Spring)

    