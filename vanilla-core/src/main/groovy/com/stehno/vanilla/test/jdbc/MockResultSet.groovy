/*
 * Copyright (C) 2015 Christopher J. Stehno <chris@stehno.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stehno.vanilla.test.jdbc

import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.TypeChecked

import java.sql.*

import static com.stehno.vanilla.Affirmations.affirm
import static com.stehno.vanilla.Affirmations.affirmNot
import static groovy.transform.TypeCheckingMode.SKIP

/**
 * Implementation of the java.sql.ResultSet interface useful for testing with something more concrete than mock data.
 * Generally, instances of this class will be created using the ResultSetBuilder DSL; however, it may be used directly.
 *
 * Note: this is not intended to be a fully functioning ResultSet implementation - not all features are implemented and
 * some that are implemented are simply intended to store values for later retrieval during testing.
 */
@TypeChecked @TupleConstructor @ToString(includeNames = true)
@SuppressWarnings(['MethodCount', 'DuplicateNumberLiteral', 'ClassSize'])
class MockResultSet implements ResultSet {

    final List<String> columnNames
    final List<Object[]> rows

    int fetchDirection
    int fetchSize
    boolean closed
    int holdability
    int type
    int concurrency
    SQLWarning warnings
    String cursorName

    // FIXME: needs a lot of type coersion and testing

    private int currentRow = -1

    @Override
    boolean next() throws SQLException {
        assertNotClosed()

        if (currentRow + 1 < rows.size()) {
            currentRow++
            return true
        }

        return false
    }

    @Override
    void close() throws SQLException {
        closed = true
    }

    int size() {
        rows.size()
    }

    @Override
    boolean wasNull() throws SQLException {
        unsupported 'wasNull'
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
        return getObject(columnIndex, byte) ?: 0
    }

    @Override
    short getShort(int columnIndex) throws SQLException {
        return getObject(columnIndex, short) ?: 0
    }

    @Override
    int getInt(int columnIndex) throws SQLException {
        return getObject(columnIndex, int) ?: 0
    }

    @Override
    long getLong(int columnIndex) throws SQLException {
        return getObject(columnIndex, long) ?: 0
    }

    @Override
    float getFloat(int columnIndex) throws SQLException {
        return getObject(columnIndex, float) ?: 0f
    }

    @Override
    double getDouble(int columnIndex) throws SQLException {
        return getObject(columnIndex, double) ?: 0d
    }

    @Override
    BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        BigDecimal value = getObject(columnIndex, BigDecimal)
        value.scale = scale
        return value
    }

    @Override
    byte[] getBytes(int columnIndex) throws SQLException {
        return getObject(columnIndex, Object) as byte[]
    }

    @Override
    Date getDate(int columnIndex) throws SQLException {
        getDate(columnIndex, Calendar.instance)
    }

    @Override
    Time getTime(int columnIndex) throws SQLException {
        return getTime(columnIndex, Calendar.instance)
    }

    @Override
    Timestamp getTimestamp(int columnIndex) throws SQLException {
        return getTimestamp(columnIndex, Calendar.instance)
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
        return getObject(columnLabel, byte) ?: 0
    }

    @Override
    short getShort(String columnLabel) throws SQLException {
        return getObject(columnLabel, short) ?: 0
    }

    @Override
    int getInt(String columnLabel) throws SQLException {
        return getObject(columnLabel, int) ?: 0
    }

    @Override
    long getLong(String columnLabel) throws SQLException {
        return getObject(columnLabel, long) ?: 0
    }

    @Override
    float getFloat(String columnLabel) throws SQLException {
        return getObject(columnLabel, float) ?: 0f
    }

    @Override
    double getDouble(String columnLabel) throws SQLException {
        return getObject(columnLabel, double) ?: 0d
    }

    @Override
    BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        return getBigDecimal(columnIndex(columnLabel), scale)
    }

    @Override
    byte[] getBytes(String columnLabel) throws SQLException {
        return getObject(columnLabel, Object) as byte[]
    }

    @Override
    Date getDate(String columnLabel) throws SQLException {
        return getDate(columnIndex(columnLabel))
    }

    @Override
    Time getTime(String columnLabel) throws SQLException {
        return getTime(columnIndex(columnLabel))
    }

    @Override
    Timestamp getTimestamp(String columnLabel) throws SQLException {
        return getTimestamp(columnIndex(columnLabel))
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
        unsupported 'getMetaData()'
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
        return columnIndex(columnLabel)
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
        currentRow = 0
        return !rows.isEmpty()
    }

    @Override
    boolean last() throws SQLException {
        currentRow = rows.size() - 1
        return currentRow > -1
    }

    @Override
    int getRow() throws SQLException {
        assertNotClosed()
        return currentRow + 1
    }

    @Override
    boolean absolute(int row) throws SQLException {
        assertNotClosed()

        if (row <= 0) {
            currentRow = -1
            return false

        } else if (row > rows.size()) {
            currentRow = rows.size()
            return false

        }

        currentRow = row - 1
        return true
    }

    @Override
    boolean relative(int count) throws SQLException {
        assertNotClosed()

        currentRow = currentRow + count

        if (currentRow < 0) {
            currentRow = -1
            return false

        } else if (currentRow > rows.size()) {
            currentRow = rows.size()
            return false

        }

        return true
    }

    @Override
    boolean previous() throws SQLException {
        assertNotClosed()

        currentRow--

        if (currentRow < -1) {
            currentRow = -1
        }

        return currentRow > -1 && currentRow < rows.size()
    }

    @Override
    boolean rowUpdated() throws SQLException {
        unsupported 'rowUpdated()'
    }

    @Override
    boolean rowInserted() throws SQLException {
        unsupported 'rowInserted()'
    }

    @Override
    boolean rowDeleted() throws SQLException {
        unsupported 'rowDeleted()'
    }

    @Override
    void updateNull(int columnIndex) throws SQLException {
        unsupported 'updateNull(int)'
    }

    @Override
    void updateBoolean(int columnIndex, boolean x) throws SQLException {
        unsupported 'updateBoolean(int, boolean)'
    }

    @Override
    void updateByte(int columnIndex, byte x) throws SQLException {
        unsupported 'updateByte(int, byte)'
    }

    @Override
    void updateShort(int columnIndex, short x) throws SQLException {
        unsupported 'updateShort(int, short)'
    }

    @Override
    void updateInt(int columnIndex, int x) throws SQLException {
        unsupported 'updateInt(int, int)'
    }

    @Override
    void updateLong(int columnIndex, long x) throws SQLException {
        unsupported 'updateLong(int, long)'
    }

    @Override
    void updateFloat(int columnIndex, float x) throws SQLException {
        unsupported 'updateFload(int, float)'
    }

    @Override
    void updateDouble(int columnIndex, double x) throws SQLException {
        unsupported 'updateDouble(int, double)'
    }

    @Override
    void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        unsupported 'updateBigDecimal(int, BigDecimal)'
    }

    @Override
    void updateString(int columnIndex, String x) throws SQLException {
        unsupported 'updateString(int, String)'
    }

    @Override
    void updateBytes(int columnIndex, byte[] x) throws SQLException {
        unsupported 'updateBytes(int, byte[])'
    }

    @Override
    void updateDate(int columnIndex, Date x) throws SQLException {
        unsupported 'updateDate(int, Date)'
    }

    @Override
    void updateTime(int columnIndex, Time x) throws SQLException {
        unsupported 'updateDate(int, Time)'
    }

    @Override
    void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        unsupported 'updateDate(int, Timestamp)'
    }

    @Override
    void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        unsupported 'updateAsciiStream(int, InputStream, int)'
    }

    @Override
    void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        unsupported 'updateBinaryStream(int, InputStream, int)'
    }

    @Override
    void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        unsupported 'updateCharacterStream(int, Reader, int)'
    }

    @Override
    void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        unsupported 'updateObject(int, Object, int)'
    }

    @Override
    void updateObject(int columnIndex, Object x) throws SQLException {
        unsupported 'updateObject(int, Object)'
    }

    @Override
    void updateNull(String columnLabel) throws SQLException {
        unsupported 'updateNull(String)'
    }

    @Override
    void updateBoolean(String columnLabel, boolean x) throws SQLException {
        unsupported 'updateBoolean(String, boolean)'
    }

    @Override
    void updateByte(String columnLabel, byte x) throws SQLException {
        unsupported 'updateByte(String, byte)'
    }

    @Override
    void updateShort(String columnLabel, short x) throws SQLException {
        unsupported 'updateShort(String, short)'
    }

    @Override
    void updateInt(String columnLabel, int x) throws SQLException {
        unsupported 'updateInt(String, int)'
    }

    @Override
    void updateLong(String columnLabel, long x) throws SQLException {
        unsupported 'updateLong(String, long)'
    }

    @Override
    void updateFloat(String columnLabel, float x) throws SQLException {
        unsupported 'updateFloat(String, float)'
    }

    @Override
    void updateDouble(String columnLabel, double x) throws SQLException {
        unsupported 'updateDouble(String, double)'
    }

    @Override
    void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        unsupported 'updateBigDecimal(String, BigDecimal)'
    }

    @Override
    void updateString(String columnLabel, String x) throws SQLException {
        unsupported 'updateString(String, String)'
    }

    @Override
    void updateBytes(String columnLabel, byte[] x) throws SQLException {
        unsupported 'updateBytes(String, byte[])'
    }

    @Override
    void updateDate(String columnLabel, Date x) throws SQLException {
        updateDate(columnIndex(columnLabel), x)
    }

    @Override
    void updateTime(String columnLabel, Time x) throws SQLException {
        updateTime(columnIndex(columnLabel), x)
    }

    @Override
    void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        updateTimestamp(columnIndex(columnLabel), x)
    }

    @Override
    void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        unsupported 'updateAsciiStream(String, InputStreeam, int)'
    }

    @Override
    void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        unsupported 'updateBinaryStream(String, InputStream, int)'
    }

    @Override
    void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        unsupported 'updateCharacterStream(String, Reader, int)'
    }

    @Override
    void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        unsupported 'updateObject(String, Object, int)'
    }

    @Override
    void updateObject(String columnLabel, Object x) throws SQLException {
        unsupported 'updateObject(String, Object)'
    }

    @Override
    void insertRow() throws SQLException {
        unsupported 'insertRow()'
    }

    @Override
    void updateRow() throws SQLException {
        unsupported 'updateRow()'
    }

    @Override
    void deleteRow() throws SQLException {
        unsupported 'deleteRow()'
    }

    @Override
    void refreshRow() throws SQLException {
        unsupported 'refreshRow()'
    }

    @Override
    void cancelRowUpdates() throws SQLException {
        unsupported 'cancelRowUpdates()'
    }

    @Override
    void moveToInsertRow() throws SQLException {
        unsupported 'moveToInsertRow()'
    }

    @Override
    void moveToCurrentRow() throws SQLException {
        unsupported 'moveToCurrentRow()'
    }

    @Override
    Statement getStatement() throws SQLException {
        unsupported 'getStatement()'
    }

    @Override
    Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        unsupported('getObject(int, Map)')
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
        unsupported 'getObject(String, Map)'
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
        return getChrono(columnIndex, cal, Date)
    }

    @Override
    Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return getDate(columnIndex(columnLabel), cal)
    }

    @Override
    Time getTime(int columnIndex, Calendar cal) throws SQLException {
        return getChrono(columnIndex, cal, Time)
    }

    @Override
    Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return getTime(columnIndex(columnLabel), Calendar.instance)
    }

    @Override
    Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        return getChrono(columnIndex, cal, Timestamp)
    }

    @Override
    Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return getTimestamp(columnIndex(columnLabel), cal)
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
        unsupported 'updateRef(int, Ref)'
    }

    @Override
    void updateRef(String columnLabel, Ref x) throws SQLException {
        unsupported 'updateRef(String, Ref)'
    }

    @Override
    void updateBlob(int columnIndex, Blob x) throws SQLException {
        unsupported 'updateBlob(int, Blob)'
    }

    @Override
    void updateBlob(String columnLabel, Blob x) throws SQLException {
        unsupported 'updateBlob(String, Blob)'
    }

    @Override
    void updateClob(int columnIndex, Clob x) throws SQLException {
        unsupported 'updateClob(int, Clob)'
    }

    @Override
    void updateClob(String columnLabel, Clob x) throws SQLException {
        unsupported 'updateClob(String, Clob)'
    }

    @Override
    void updateArray(int columnIndex, Array x) throws SQLException {
        unsupported 'updateArray(int, Array)'
    }

    @Override
    void updateArray(String columnLabel, Array x) throws SQLException {
        unsupported 'updateArray(String, Array)'
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
        unsupported 'updateRowId(int, RowId)'
    }

    @Override
    void updateRowId(String columnLabel, RowId x) throws SQLException {
        unsupported 'updateRowInd(String, RowId)'
    }

    @Override
    void updateNString(int columnIndex, String nString) throws SQLException {
        unsupported 'updateNString(int, String)'
    }

    @Override
    void updateNString(String columnLabel, String nString) throws SQLException {
        unsupported 'updateNString(String, String)'
    }

    @Override
    void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        unsupported 'updateNClob(int, NClob)'
    }

    @Override
    void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        unsupported 'updateNClob(String, NClob)'
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
        unsupported 'getSQLXML(int)'
    }

    @Override
    SQLXML getSQLXML(String columnLabel) throws SQLException {
        unsupported 'getSQLXML(String)'
    }

    @Override
    void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        unsupported 'updateSQLXML(int, SQLXML)'
    }

    @Override
    void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        unsupported 'updateSQLXML(String, SQLXML)'
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
        unsupported 'updateNCharacterStream(int, Reader, long)'
    }

    @Override
    void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        unsupported 'updateNCharacterStream(String, Reader, long)'
    }

    @Override
    void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        unsupported 'updateAsciiStream(int, InputStream, long)'
    }

    @Override
    void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        unsupported 'updateBinaryStream(int, InputStream, long)'
    }

    @Override
    void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        unsupported 'updateCharacterStream(int, Reader, long)'
    }

    @Override
    void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        unsupported 'updateAsciiStream(String, InputStream, long)'
    }

    @Override
    void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        unsupported 'updateBinaryStream(String, InputStream, long)'
    }

    @Override
    void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        unsupported 'updateCharacterStream(String, Reader, long)'
    }

    @Override
    void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        unsupported 'updateBlob(int, InputStream, long)'
    }

    @Override
    void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        unsupported 'updateBlob(String, InputStream, long)'
    }

    @Override
    void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        unsupported 'updateClob(int, Reader, long)'
    }

    @Override
    void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        unsupported 'updateClob(String, Reader, long)'
    }

    @Override
    void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        unsupported 'updateNClob(int, Reader, long)'
    }

    @Override
    void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        unsupported 'updateNClob(String, Reader, long)'
    }

    @Override
    void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        unsupported 'updateNCharacterStream(int, Reader)'
    }

    @Override
    void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        unsupported 'updateNCharacterStream(String, Reader)'
    }

    @Override
    void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        unsupported 'updateAsciiStream(int, InputStream)'
    }

    @Override
    void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        unsupported 'updateBinaryStream(int, InputStream)'
    }

    @Override
    void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        unsupported 'updateCharacterStream(int, Reader)'
    }

    @Override
    void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        unsupported 'updateAsciiStream(String, InputStream)'
    }

    @Override
    void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        unsupported 'updateBinaryStream(String, InputStream'
    }

    @Override
    void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        unsupported 'updateCharacterStream(String, Reader)'
    }

    @Override
    void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        unsupported 'updateBlob(int, InputStream)'
    }

    @Override
    void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        unsupported 'updateBlob(String, InputStream)'
    }

    @Override
    void updateClob(int columnIndex, Reader reader) throws SQLException {
        unsupported 'updateClob(int, Reader)'
    }

    @Override
    void updateClob(String columnLabel, Reader reader) throws SQLException {
        unsupported 'updateClob(String, Reader)'
    }

    @Override
    void updateNClob(int columnIndex, Reader reader) throws SQLException {
        unsupported 'updateNClob(int, Reader)'
    }

    @Override
    void updateNClob(String columnLabel, Reader reader) throws SQLException {
        unsupported 'updateNClob(String, Reader)'
    }

    @Override
    def <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        assertions columnIndex

        def data = data(columnIndex)
        return data != null ? data.asType(type) : null
    }

    private Object data(int columnIndex) {
        rows[currentRow][columnIndex - 1]
    }

    @Override
    def <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        getObject(columnIndex(columnLabel), type)
    }

    private int columnIndex(final String columnLabel) {
        columnNames.indexOf(columnLabel) + 1
    }

    @Override
    def <T> T unwrap(Class<T> iface) throws SQLException {
        unsupported 'unwrap(Class)'
    }

    @Override
    boolean isWrapperFor(Class<?> iface) throws SQLException {
        unsupported 'isWrapperFor(Class)'
    }

    @TypeChecked(SKIP)
    private <C> C getChrono(int columnIndex, Calendar cal, Class<C> type) {
        assertions columnIndex

        Object item = data(columnIndex)

        long millis

        if (item == null) {
            return null

        } else if (type.isAssignableFrom(item.class)) {
            millis = (item.asType(type)).time

        } else if (item instanceof java.util.Date) {
            millis = (item as java.util.Date).time

        } else if (item instanceof Number) {
            millis = item as long

        } else {
            throw new IllegalArgumentException('Unsupported type conversion.')
        }

        cal.timeInMillis = millis

        return type.newInstance(cal.timeInMillis)
    }

    private void assertions(int colIndex) {
        assertNotClosed()
        assertRowBounds()
        assertValidIndex(colIndex)
    }

    private void assertValidIndex(int index) {
        affirm index >= 1 || index <= columnNames.size(), IndexOutOfBoundsException
    }

    private void assertNotClosed() {
        affirmNot closed, 'ResultSet-Closed', SQLException
    }

    private void assertRowBounds() {
        affirm currentRow >= 0 || currentRow < rows.size(), "Current row out of bounds: ${currentRow}", SQLException
    }

    private static void unsupported(String name) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("$name is not supported.")
    }
}
