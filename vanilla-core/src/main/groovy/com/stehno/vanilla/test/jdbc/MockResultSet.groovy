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
    int holdability
    int type
    int concurrency
    SQLWarning warnings
    String cursorName

    // FIXME: throw exception for accessingn a closed RS

    private int currentRow = -1

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
        return getObject(columnIndex, String)
    }

    @Override
    boolean getBoolean(int columnIndex) throws SQLException {
        return getObject(columnIndex, boolean)
    }

    @Override
    byte getByte(int columnIndex) throws SQLException {
        return getObject(columnIndex, byte)
    }

    @Override
    short getShort(int columnIndex) throws SQLException {
        return getObject(columnIndex, short)
    }

    @Override
    int getInt(int columnIndex) throws SQLException {
        return getObject(columnIndex, int)
    }

    @Override
    long getLong(int columnIndex) throws SQLException {
        return getObject(columnIndex, long)
    }

    @Override
    float getFloat(int columnIndex) throws SQLException {
        return getObject(columnIndex, float)
    }

    @Override
    double getDouble(int columnIndex) throws SQLException {
        return getObject(columnIndex, double)
    }

    @Override
    BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        return getObject(columnIndex, BigDecimal)
    }

    @Override
    byte[] getBytes(int columnIndex) throws SQLException {
        return getObject(columnIndex, Object) as byte[]
    }

    @Override
    Date getDate(int columnIndex) throws SQLException {
        return getObject(columnIndex, Date)
    }

    @Override
    Time getTime(int columnIndex) throws SQLException {
        return getObject(columnIndex, Time)
    }

    @Override
    Timestamp getTimestamp(int columnIndex) throws SQLException {
        return getObject(columnIndex, Timestamp)
    }

    @Override
    InputStream getAsciiStream(int columnIndex) throws SQLException {
        return getObject(columnIndex, InputStream)
    }

    @Override
    InputStream getUnicodeStream(int columnIndex) throws SQLException {
        return getObject(columnIndex, InputStream)
    }

    @Override
    InputStream getBinaryStream(int columnIndex) throws SQLException {
        return getObject(columnIndex, InputStream)
    }

    @Override
    String getString(String columnLabel) throws SQLException {
        return getObject(columnLabel, String)
    }

    @Override
    boolean getBoolean(String columnLabel) throws SQLException {
        return getObject(columnLabel, boolean)
    }

    @Override
    byte getByte(String columnLabel) throws SQLException {
        return getObject(columnLabel, byte)
    }

    @Override
    short getShort(String columnLabel) throws SQLException {
        return getObject(columnLabel, short)
    }

    @Override
    int getInt(String columnLabel) throws SQLException {
        return getObject(columnLabel, int)
    }

    @Override
    long getLong(String columnLabel) throws SQLException {
        return getObject(columnLabel, long)
    }

    @Override
    float getFloat(String columnLabel) throws SQLException {
        return getObject(columnLabel, float)
    }

    @Override
    double getDouble(String columnLabel) throws SQLException {
        return getObject(columnLabel, double)
    }

    @Override
    BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        return getObject(columnLabel, BigDecimal)
    }

    @Override
    byte[] getBytes(String columnLabel) throws SQLException {
        return getObject(columnLabel, Object) as byte[]
    }

    @Override
    Date getDate(String columnLabel) throws SQLException {
        return getObject(columnLabel, Date)
    }

    @Override
    Time getTime(String columnLabel) throws SQLException {
        return getObject(columnLabel, Time)
    }

    @Override
    Timestamp getTimestamp(String columnLabel) throws SQLException {
        return getObject(columnLabel, Timestamp)
    }

    @Override
    InputStream getAsciiStream(String columnLabel) throws SQLException {
        return getObject(columnLabel, InputStream)
    }

    @Override
    InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return getObject(columnLabel, InputStream)
    }

    @Override
    InputStream getBinaryStream(String columnLabel) throws SQLException {
        return getObject(columnLabel, InputStream)
    }

    @Override
    void clearWarnings() throws SQLException {
        warnings = null
    }

    @Override
    ResultSetMetaData getMetaData() throws SQLException {
        throw new UnsupportedOperationException('getMetaData() is not supported.')
    }

    @Override
    Object getObject(int columnIndex) throws SQLException {
        return getObject(columnIndex, Object)
    }

    @Override
    Object getObject(String columnLabel) throws SQLException {
        return getObject(columnLabel, Object)
    }

    @Override
    int findColumn(String columnLabel) throws SQLException {
        return columnNames.indexOf(columnLabel)
    }

    @Override
    Reader getCharacterStream(int columnIndex) throws SQLException {
        return getObject(columnIndex, Reader)
    }

    @Override
    Reader getCharacterStream(String columnLabel) throws SQLException {
        return getObject(columnLabel, Reader)
    }

    @Override
    BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return getObject(columnIndex, BigDecimal)
    }

    @Override
    BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return getObject(columnLabel, BigDecimal)
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
        assertNotClosed()
        return currentRow + 1
    }

    @Override
    boolean absolute(int row) throws SQLException {
        assertNotClosed()
        assertRowBounds()

        currentRow = row

        return currentRow > 0 && currentRow < rows.size()
    }

    @Override
    boolean relative(int count) throws SQLException {
        assertNotClosed()
        assertRowBounds()

        currentRow = currentRow + count

        return currentRow >= 0 && currentRow < rows.size()
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
    boolean rowUpdated() throws SQLException {
        return false // TOOD: this should probably be tied to update calls
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
        update(columnLabel, reader)
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
        // nothing
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
        throw new SQLFeatureNotSupportedException('getObject(int, Map) is not supported.')
    }

    @Override
    Ref getRef(int columnIndex) throws SQLException {
        return getObject(columnIndex, Ref)
    }

    @Override
    Blob getBlob(int columnIndex) throws SQLException {
        return getObject(columnIndex, Blob)
    }

    @Override
    Clob getClob(int columnIndex) throws SQLException {
        return getObject(columnIndex, Clob)
    }

    @Override
    Array getArray(int columnIndex) throws SQLException {
        return getObject(columnIndex, Array)
    }

    @Override
    Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        throw new SQLFeatureNotSupportedException('getObject(String, Map) is not supported.')
    }

    @Override
    Ref getRef(String columnLabel) throws SQLException {
        return getObject(columnLabel, Ref)
    }

    @Override
    Blob getBlob(String columnLabel) throws SQLException {
        return getObject(columnLabel, Blob)
    }

    @Override
    Clob getClob(String columnLabel) throws SQLException {
        return getObject(columnLabel, Clob)
    }

    @Override
    Array getArray(String columnLabel) throws SQLException {
        return getObject(columnLabel, Array)
    }

    @Override
    Date getDate(int columnIndex, Calendar cal) throws SQLException {
        return getObject(columnIndex, Date)
    }

    @Override
    Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return getObject(columnLabel, Date)
    }

    @Override
    Time getTime(int columnIndex, Calendar cal) throws SQLException {
        return getObject(columnIndex, Time)
    }

    @Override
    Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return getObject(columnLabel, Time)
    }

    @Override
    Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        return getObject(columnIndex, Timestamp)
    }

    @Override
    Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return getObject(columnLabel, Timestamp)
    }

    @Override
    URL getURL(int columnIndex) throws SQLException {
        return getObject(columnIndex, URL)
    }

    @Override
    URL getURL(String columnLabel) throws SQLException {
        return getObject(columnLabel, URL)
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
        return getObject(columnIndex, RowId)
    }

    @Override
    RowId getRowId(String columnLabel) throws SQLException {
        return getObject(columnLabel, RowId)
    }

    @Override
    void updateRowId(int columnIndex, RowId x) throws SQLException {
        update columnIndex, x
    }

    @Override
    void updateRowId(String columnLabel, RowId x) throws SQLException {
        update columnLabel, x
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
        return getObject(columnIndex, NClob)
    }

    @Override
    NClob getNClob(String columnLabel) throws SQLException {
        return getObject(columnLabel, NClob)
    }

    @Override
    SQLXML getSQLXML(int columnIndex) throws SQLException {
        return getObject(columnIndex, SQLXML)
    }

    @Override
    SQLXML getSQLXML(String columnLabel) throws SQLException {
        return getObject(columnLabel, SQLXML)
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
        return getObject(columnIndex, String)
    }

    @Override
    String getNString(String columnLabel) throws SQLException {
        return getObject(columnLabel, String)
    }

    @Override
    Reader getNCharacterStream(int columnIndex) throws SQLException {
        return getObject(columnIndex, Reader)
    }

    @Override
    Reader getNCharacterStream(String columnLabel) throws SQLException {
        return getObject(columnLabel, Reader)
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
        update(columnLabel, reader)
    }

    @Override
    void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        update(columnIndex, inputStream)
    }

    @Override
    void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        update(columnLabel, inputStream)
    }

    @Override
    void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        update(columnIndex, reader)
    }

    @Override
    void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        update(columnLabel, reader)
    }

    @Override
    void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        update(columnIndex, reader)
    }

    @Override
    void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        update(columnLabel, reader)
    }

    @Override
    void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        update(columnLabel, reader)
    }

    @Override
    void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        update(columnIndex, x)
    }

    @Override
    void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        update(columnLabel, x)
    }

    @Override
    void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        update(columnLabel, reader)
    }

    @Override
    void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        update(columnIndex, inputStream)
    }

    @Override
    void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        update(columnLabel, inputStream)
    }

    @Override
    void updateClob(int columnIndex, Reader reader) throws SQLException {
        update(columnIndex, reader)
    }

    @Override
    void updateClob(String columnLabel, Reader reader) throws SQLException {
        update(columnLabel, reader)
    }

    @Override
    void updateNClob(int columnIndex, Reader reader) throws SQLException {
        update(columnIndex, reader)
    }

    @Override
    void updateNClob(String columnLabel, Reader reader) throws SQLException {
        update(columnLabel, reader)
    }

    @Override
    def <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        assertNotClosed()
        assertRowBounds()
        assertValidIndex(columnIndex)
        rows[currentRow][columnIndex - 1]?.asType(type) ?: null
    }

    @Override
    def <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        getObject(columnNames.indexOf(columnLabel) + 1, type)
    }

    @Override
    def <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException('unwrap(Class) is not supported')
    }

    @Override
    boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException('isWrapperFor(Class) is not supported')
    }

    private void assertValidIndex(int index) {
        if (index < 1 || index > columnNames.size()) throw new IndexOutOfBoundsException()
    }

    private void assertNotClosed() {
        if (closed) throw new SQLException('ResultSet-Closed')
    }

    private void assertRowBounds() {
        if (currentRow < 0 || currentRow >= rows.size()) throw new SQLException("Current row out of bounds: ${currentRow}")
    }
}