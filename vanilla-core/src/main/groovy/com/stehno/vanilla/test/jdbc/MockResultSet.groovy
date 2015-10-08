package com.stehno.vanilla.test.jdbc

import groovy.transform.TupleConstructor
import groovy.transform.TypeChecked

import java.sql.*

/**
 * FIXME: document me
 */
@TypeChecked @TupleConstructor
class MockResultSet implements ResultSet {

    final List<String> columnNames
    final List<Object[]> rows

    // TODO: fetch direction and size just store the values - what should they do?
    int fetchDirection
    int fetchSize
    boolean closed
    SQLWarning warnings

    // FIXME: throw exception for accessingn a closed RS

    private int currentRow = -1

    private void assertNotClosed() {
        if (closed) throw new SQLException('ResultSet-Closed')
    }

    private Object data(int index) {
        assertNotClosed()
        assertRowBounds()
        rows[currentRow][index]
    }

    private void assertRowBounds() {
        if (currentRow < 0 || currentRow >= rows.size()) throw new SQLException("Current row out of bounds: ${currentRow}")
    }

    private Object data(String colName) {
        assertNotClosed()
        assertRowBounds()
        rows[currentRow][columnNames.indexOf(colName)]
    }

    private void update(int index, Object value) {
        assertNotClosed()
        assertRowBounds()
        rows[currentRow][index] = value
    }

    private void update(String colName, Object value) {
        assertNotClosed()
        assertRowBounds()
        rows[currentRow][columnNames.indexOf(colName)] = value
    }

    @Override
    boolean next() throws SQLException {
        assertNotClosed()

        if (currentRow + 1 < rows.size()) {
            currentRow++
            return true
        } else {
            return false
        }
    }

    @Override
    void close() throws SQLException {
        closed = true
    }

    @Override
    boolean wasNull() throws SQLException {
        return false
    }

    @Override
    String getString(int columnIndex) throws SQLException {
        return data(columnIndex) as String
    }

    @Override
    boolean getBoolean(int columnIndex) throws SQLException {
        return data(columnIndex) as boolean
    }

    @Override
    byte getByte(int columnIndex) throws SQLException {
        return data(columnIndex) as byte
    }

    @Override
    short getShort(int columnIndex) throws SQLException {
        return data(columnIndex) as short
    }

    @Override
    int getInt(int columnIndex) throws SQLException {
        return data(columnIndex) as int
    }

    @Override
    long getLong(int columnIndex) throws SQLException {
        return data(columnIndex) as long
    }

    @Override
    float getFloat(int columnIndex) throws SQLException {
        return data(columnIndex) as float
    }

    @Override
    double getDouble(int columnIndex) throws SQLException {
        return data(columnIndex) as double
    }

    @Override
    BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        return data(columnIndex) as BigDecimal
    }

    @Override
    byte[] getBytes(int columnIndex) throws SQLException {
        return data(columnIndex) as byte[]
    }

    @Override
    Date getDate(int columnIndex) throws SQLException {
        return data(columnIndex) as Date
    }

    @Override
    Time getTime(int columnIndex) throws SQLException {
        return data(columnIndex) as Time
    }

    @Override
    Timestamp getTimestamp(int columnIndex) throws SQLException {
        return data(columnIndex) as Timestamp
    }

    @Override
    InputStream getAsciiStream(int columnIndex) throws SQLException {
        return data(columnIndex) as InputStream
    }

    @Override
    InputStream getUnicodeStream(int columnIndex) throws SQLException {
        return data(columnIndex) as InputStream
    }

    @Override
    InputStream getBinaryStream(int columnIndex) throws SQLException {
        return data(columnIndex) as InputStream
    }

    @Override
    String getString(String columnLabel) throws SQLException {
        return data(columnLabel) as String
    }

    @Override
    boolean getBoolean(String columnLabel) throws SQLException {
        return data(columnLabel) as boolean
    }

    @Override
    byte getByte(String columnLabel) throws SQLException {
        return data(columnLabel) as byte
    }

    @Override
    short getShort(String columnLabel) throws SQLException {
        return data(columnLabel) as short
    }

    @Override
    int getInt(String columnLabel) throws SQLException {
        return data(columnLabel) as int
    }

    @Override
    long getLong(String columnLabel) throws SQLException {
        return data(columnLabel) as long
    }

    @Override
    float getFloat(String columnLabel) throws SQLException {
        return data(columnLabel) as float
    }

    @Override
    double getDouble(String columnLabel) throws SQLException {
        return data(columnLabel) as double
    }

    @Override
    BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        return data(columnLabel) as BigDecimal
    }

    @Override
    byte[] getBytes(String columnLabel) throws SQLException {
        return data(columnLabel) as byte[]
    }

    @Override
    Date getDate(String columnLabel) throws SQLException {
        return data(columnLabel) as Date
    }

    @Override
    Time getTime(String columnLabel) throws SQLException {
        return data(columnLabel) as Time
    }

    @Override
    Timestamp getTimestamp(String columnLabel) throws SQLException {
        return data(columnLabel) as Timestamp
    }

    @Override
    InputStream getAsciiStream(String columnLabel) throws SQLException {
        return data(columnLabel) as InputStream
    }

    @Override
    InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return data(columnLabel) as InputStream
    }

    @Override
    InputStream getBinaryStream(String columnLabel) throws SQLException {
        return data(columnLabel) as InputStream
    }

    @Override
    void clearWarnings() throws SQLException {
        warnings = null
    }

    @Override
    String getCursorName() throws SQLException {
        throw new UnsupportedOperationException('getCursorName() not supported')
    }

    @Override
    ResultSetMetaData getMetaData() throws SQLException {
        return null
    }

    @Override
    Object getObject(int columnIndex) throws SQLException {
        return data(columnIndex)
    }

    @Override
    Object getObject(String columnLabel) throws SQLException {
        return data(columnLabel)
    }

    @Override
    int findColumn(String columnLabel) throws SQLException {
        return columnNames.indexOf(columnLabel)
    }

    @Override
    Reader getCharacterStream(int columnIndex) throws SQLException {
        return data(columnIndex) as Reader
    }

    @Override
    Reader getCharacterStream(String columnLabel) throws SQLException {
        return data(columnLabel) as Reader
    }

    @Override
    BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return data(columnIndex) as BigDecimal
    }

    @Override
    BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return data(columnLabel) as BigDecimal
    }

    @Override
    boolean isBeforeFirst() throws SQLException {
        return currentRow < 0
    }

    @Override
    boolean isAfterLast() throws SQLException {
        return currentRow >= rows.size()
    }

    @Override
    boolean isFirst() throws SQLException {
        return currentRow == 0
    }

    @Override
    boolean isLast() throws SQLException {
        return currentRow == rows.size() - 1
    }

    @Override
    void beforeFirst() throws SQLException {
        currentRow = -1
    }

    @Override
    void afterLast() throws SQLException {
        currentRow = rows.size()
    }

    @Override
    boolean first() throws SQLException {
        return currentRow == 0
    }

    @Override
    boolean last() throws SQLException {
        return currentRow == rows.size() - 1
    }

    @Override
    int getRow() throws SQLException {
        return currentRow
    }

    @Override
    boolean absolute(int row) throws SQLException {
        return false
    }

    @Override
    boolean relative(int rows) throws SQLException {
        return false
    }

    @Override
    boolean previous() throws SQLException {
        assertNotClosed()

        if (currentRow - 1 >= 0) {
            currentRow--
            return true
        } else {
            return false
        }
    }

    @Override
    int getType() throws SQLException {
        return 0
    }

    @Override
    int getConcurrency() throws SQLException {
        return 0
    }

    @Override
    boolean rowUpdated() throws SQLException {
        return false
    }

    @Override
    boolean rowInserted() throws SQLException {
        return false
    }

    @Override
    boolean rowDeleted() throws SQLException {
        return false
    }

    @Override
    void updateNull(int columnIndex) throws SQLException {
        update(columnIndex, null)
    }

    @Override
    void updateBoolean(int columnIndex, boolean x) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateByte(int columnIndex, byte x) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateShort(int columnIndex, short x) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateInt(int columnIndex, int x) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateLong(int columnIndex, long x) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateFloat(int columnIndex, float x) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateDouble(int columnIndex, double x) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateString(int columnIndex, String x) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateBytes(int columnIndex, byte[] x) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateDate(int columnIndex, Date x) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateTime(int columnIndex, Time x) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateObject(int columnIndex, Object x) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateNull(String columnLabel) throws SQLException {
        update(columnLabel, null)
    }

    @Override
    void updateBoolean(String columnLabel, boolean x) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    void updateByte(String columnLabel, byte x) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    void updateShort(String columnLabel, short x) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    void updateInt(String columnLabel, int x) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    void updateLong(String columnLabel, long x) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    void updateFloat(String columnLabel, float x) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    void updateDouble(String columnLabel, double x) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    void updateString(String columnLabel, String x) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    void updateBytes(String columnLabel, byte[] x) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    void updateDate(String columnLabel, Date x) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    void updateTime(String columnLabel, Time x) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    void updateObject(String columnLabel, Object x) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    void insertRow() throws SQLException {
        throw new UnsupportedOperationException('insertRow() not supported')
    }

    @Override
    void updateRow() throws SQLException {
        throw new UnsupportedOperationException('updateRow() not supported')
    }

    @Override
    void deleteRow() throws SQLException {
        throw new UnsupportedOperationException('deleteRow() not supported')
    }

    @Override
    void refreshRow() throws SQLException {
        throw new UnsupportedOperationException('refreshRow() not supported')
    }

    @Override
    void cancelRowUpdates() throws SQLException {
        // nothing
    }

    @Override
    void moveToInsertRow() throws SQLException {
        // nothing
    }

    @Override
    void moveToCurrentRow() throws SQLException {
        // nothing
    }

    @Override
    Statement getStatement() throws SQLException {
        throw new UnsupportedOperationException('getStatement() is not supported')
    }

    @Override
    Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        return null
    }

    @Override
    Ref getRef(int columnIndex) throws SQLException {
        return data(columnIndex) as Ref
    }

    @Override
    Blob getBlob(int columnIndex) throws SQLException {
        return data(columnIndex) as Blob
    }

    @Override
    Clob getClob(int columnIndex) throws SQLException {
        return data(columnIndex) as Clob
    }

    @Override
    Array getArray(int columnIndex) throws SQLException {
        return data(columnIndex) as Array
    }

    @Override
    Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        return null
    }

    @Override
    Ref getRef(String columnLabel) throws SQLException {
        return data(columnLabel) as Ref
    }

    @Override
    Blob getBlob(String columnLabel) throws SQLException {
        return data(columnLabel) as Blob
    }

    @Override
    Clob getClob(String columnLabel) throws SQLException {
        return data(columnLabel) as Clob
    }

    @Override
    Array getArray(String columnLabel) throws SQLException {
        return data(columnLabel) as Array
    }

    @Override
    Date getDate(int columnIndex, Calendar cal) throws SQLException {
        return null
    }

    @Override
    Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return null
    }

    @Override
    Time getTime(int columnIndex, Calendar cal) throws SQLException {
        return null
    }

    @Override
    Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return null
    }

    @Override
    Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        return null
    }

    @Override
    Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return null
    }

    @Override
    URL getURL(int columnIndex) throws SQLException {
        return data(columnIndex) as URL
    }

    @Override
    URL getURL(String columnLabel) throws SQLException {
        return data(columnLabel) as URL
    }

    @Override
    void updateRef(int columnIndex, Ref x) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateRef(String columnLabel, Ref x) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    void updateBlob(int columnIndex, Blob x) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateBlob(String columnLabel, Blob x) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    void updateClob(int columnIndex, Clob x) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateClob(String columnLabel, Clob x) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    void updateArray(int columnIndex, Array x) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateArray(String columnLabel, Array x) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    RowId getRowId(int columnIndex) throws SQLException {
        return null
    }

    @Override
    RowId getRowId(String columnLabel) throws SQLException {
        return null
    }

    @Override
    void updateRowId(int columnIndex, RowId x) throws SQLException {

    }

    @Override
    void updateRowId(String columnLabel, RowId x) throws SQLException {

    }

    @Override
    int getHoldability() throws SQLException {
        return 0
    }

    @Override
    void updateNString(int columnIndex, String nString) throws SQLException {
        update(columnIndex, nString)
    }

    @Override
    void updateNString(String columnLabel, String nString) throws SQLException {
        update(columnLabel, nString)
    }

    @Override
    void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        update(columnIndex, nClob)
    }

    @Override
    void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        update(columnLabel, nClob)
    }

    @Override
    NClob getNClob(int columnIndex) throws SQLException {
        return data(columnIndex) as NClob
    }

    @Override
    NClob getNClob(String columnLabel) throws SQLException {
        return data(columnLabel) as NClob
    }

    @Override
    SQLXML getSQLXML(int columnIndex) throws SQLException {
        return data(columnIndex) as SQLXML
    }

    @Override
    SQLXML getSQLXML(String columnLabel) throws SQLException {
        return data(columnLabel) as SQLXML
    }

    @Override
    void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        update(columnIndex, xmlObject)
    }

    @Override
    void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        update(columnLabel, xmlObject)
    }

    @Override
    String getNString(int columnIndex) throws SQLException {
        return data(columnIndex) as String
    }

    @Override
    String getNString(String columnLabel) throws SQLException {
        return data(columnLabel) as String
    }

    @Override
    Reader getNCharacterStream(int columnIndex) throws SQLException {
        return data(columnIndex) as Reader
    }

    @Override
    Reader getNCharacterStream(String columnLabel) throws SQLException {
        return data(columnLabel) as Reader
    }

    @Override
    void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        update(columnLabel, reader)
    }

    @Override
    void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException('Not supported')
    }

    @Override
    void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        throw new UnsupportedOperationException('Not supported')
    }

    @Override
    void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        throw new UnsupportedOperationException('Not supported')
    }

    @Override
    void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException('Not supported')
    }

    @Override
    void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException('Not supported')
    }

    @Override
    void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException('Not supported')
    }

    @Override
    void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException('Not supported')
    }

    @Override
    void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new UnsupportedOperationException('Not supported')
    }

    @Override
    void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException('Not supported')
    }

    @Override
    void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        throw new UnsupportedOperationException('Not supported')
    }

    @Override
    void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        throw new UnsupportedOperationException('Not supported')
    }

    @Override
    void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new UnsupportedOperationException('Not supported')
    }

    @Override
    void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        throw new UnsupportedOperationException('Not supported')
    }

    @Override
    void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        throw new UnsupportedOperationException('Not supported')
    }

    @Override
    void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException('Not supported')
    }

    @Override
    void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw new UnsupportedOperationException('Not supported')
    }

    @Override
    void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        throw new UnsupportedOperationException('Not supported')
    }

    @Override
    void updateClob(int columnIndex, Reader reader) throws SQLException {
        throw new UnsupportedOperationException('Not supported')
    }

    @Override
    void updateClob(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException('Not supported')
    }

    @Override
    void updateNClob(int columnIndex, Reader reader) throws SQLException {
        throw new UnsupportedOperationException('Not supported')
    }

    @Override
    void updateNClob(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException('Not supported')
    }

    @Override
    def <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        throw new UnsupportedOperationException('Not supported')
    }

    @Override
    def <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        throw new UnsupportedOperationException('Not supported')
    }

    @Override
    def <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException('Not supported')
    }

    @Override
    boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException('Not supported')
    }
}
