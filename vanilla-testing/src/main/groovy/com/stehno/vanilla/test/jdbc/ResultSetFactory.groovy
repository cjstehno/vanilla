/*
 * Copyright (c) 2015 Christopher J. Stehno
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

import java.sql.*

/**
 * Created by cjstehno on 10/4/15.
 */
class ResultSetFactory {

    // FIXME: ResultSetDsl
    /*
        resultSet {
            columns 'a',  'b',  'c'
            row 123 , 3,  5
            row person
            row [:]
        }
     */

    static ResultSet resultSet(@DelegatesTo(ResultSetFactory) Closure closure) {
        ResultSetFactory factory = new ResultSetFactory()
        closure.delegate = factory
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        factory
    }

    void columns(String... colNames) {

    }

    void row(Object... colValues) {}

    void object(Object colValueObject) {}

    void map(Map<String, Object> colValueMap) {}
}

class FakeResultSet implements ResultSet {

    private boolean closed
    private final List<String> columnNames = []
    private final List<Map<String, Object>> rows = [:]
    private int currentRow = -1

    private void assertNotClosed() {
        if (closed) throw new SQLException('ResultSet-Closed')
    }

    private String columnName(int index) {
        columnNames[index]
    }

    @Override
    boolean next() throws SQLException {
        assertNotClosed()
        // FIXME; more here
        return false
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
        return getString(columnName(columnIndex))
    }

    @Override
    boolean getBoolean(int columnIndex) throws SQLException {
        return getBoolean(columnName(columnIndex))
    }

    @Override
    byte getByte(int columnIndex) throws SQLException {
        return getByte(columnName(columnIndex))
    }

    @Override
    short getShort(int columnIndex) throws SQLException {
        return getShort(columnName(columnIndex))
    }

    @Override
    int getInt(int columnIndex) throws SQLException {
        return getInt(columnName(columnIndex))
    }

    @Override
    long getLong(int columnIndex) throws SQLException {
        return getLong(columnName(columnIndex))
    }

    @Override
    float getFloat(int columnIndex) throws SQLException {
        return getFloat(columnName(columnIndex))
    }

    @Override
    double getDouble(int columnIndex) throws SQLException {
        return getDouble(columnName(columnIndex))
    }

    @Override
    BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        return getBigDecimal(columnName(columnIndex), scale)
    }

    @Override
    byte[] getBytes(int columnIndex) throws SQLException {
        return getBytes(columnName(columnIndex))
    }

    @Override
    Date getDate(int columnIndex) throws SQLException {
        return getDate(columnName(columnIndex))
    }

    @Override
    Time getTime(int columnIndex) throws SQLException {
        return getTime(columnName(columnIndex))
    }

    @Override
    Timestamp getTimestamp(int columnIndex) throws SQLException {
        return getTimestamp(columnName(columnIndex))
    }

    @Override
    InputStream getAsciiStream(int columnIndex) throws SQLException {
        return getAsciiStream(columnName(columnIndex))
    }

    @Override
    InputStream getUnicodeStream(int columnIndex) throws SQLException {
        return getUnicodeStream(columnName(columnIndex))
    }

    @Override
    InputStream getBinaryStream(int columnIndex) throws SQLException {
        return getBinaryStream(columnName(columnIndex))
    }

    @Override
    String getString(String columnLabel) throws SQLException {
        return data(columnLabel) as String
    }

    @Override
    boolean getBoolean(String columnLabel) throws SQLException {
        return data(columnLabel) as Boolean
    }

    private Object data(String col) {
        assertNotClosed()
        rows[currentRow][col]
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
        return null
    }

    @Override
    byte[] getBytes(String columnLabel) throws SQLException {
        return new byte[0]
    }

    @Override
    Date getDate(String columnLabel) throws SQLException {
        return null
    }

    @Override
    Time getTime(String columnLabel) throws SQLException {
        return null
    }

    @Override
    Timestamp getTimestamp(String columnLabel) throws SQLException {
        return null
    }

    @Override
    InputStream getAsciiStream(String columnLabel) throws SQLException {
        return null
    }

    @Override
    InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return null
    }

    @Override
    InputStream getBinaryStream(String columnLabel) throws SQLException {
        return null
    }

    @Override
    SQLWarning getWarnings() throws SQLException {
        return null
    }

    @Override
    void clearWarnings() throws SQLException {

    }

    @Override
    String getCursorName() throws SQLException {
        return null
    }

    @Override
    ResultSetMetaData getMetaData() throws SQLException {
        return null
    }

    @Override
    Object getObject(int columnIndex) throws SQLException {
        return null
    }

    @Override
    Object getObject(String columnLabel) throws SQLException {
        return null
    }

    @Override
    int findColumn(String columnLabel) throws SQLException {
        return 0
    }

    @Override
    Reader getCharacterStream(int columnIndex) throws SQLException {
        return null
    }

    @Override
    Reader getCharacterStream(String columnLabel) throws SQLException {
        return null
    }

    @Override
    BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return null
    }

    @Override
    BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return null
    }

    @Override
    boolean isBeforeFirst() throws SQLException {
        return false
    }

    @Override
    boolean isAfterLast() throws SQLException {
        return false
    }

    @Override
    boolean isFirst() throws SQLException {
        return false
    }

    @Override
    boolean isLast() throws SQLException {
        return false
    }

    @Override
    void beforeFirst() throws SQLException {

    }

    @Override
    void afterLast() throws SQLException {

    }

    @Override
    boolean first() throws SQLException {
        return false
    }

    @Override
    boolean last() throws SQLException {
        return false
    }

    @Override
    int getRow() throws SQLException {
        return 0
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
        return false
    }

    @Override
    void setFetchDirection(int direction) throws SQLException {

    }

    @Override
    int getFetchDirection() throws SQLException {
        return 0
    }

    @Override
    void setFetchSize(int rows) throws SQLException {

    }

    @Override
    int getFetchSize() throws SQLException {
        return 0
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

    }

    @Override
    void updateBoolean(int columnIndex, boolean x) throws SQLException {

    }

    @Override
    void updateByte(int columnIndex, byte x) throws SQLException {

    }

    @Override
    void updateShort(int columnIndex, short x) throws SQLException {

    }

    @Override
    void updateInt(int columnIndex, int x) throws SQLException {

    }

    @Override
    void updateLong(int columnIndex, long x) throws SQLException {

    }

    @Override
    void updateFloat(int columnIndex, float x) throws SQLException {

    }

    @Override
    void updateDouble(int columnIndex, double x) throws SQLException {

    }

    @Override
    void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {

    }

    @Override
    void updateString(int columnIndex, String x) throws SQLException {

    }

    @Override
    void updateBytes(int columnIndex, byte[] x) throws SQLException {

    }

    @Override
    void updateDate(int columnIndex, Date x) throws SQLException {

    }

    @Override
    void updateTime(int columnIndex, Time x) throws SQLException {

    }

    @Override
    void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {

    }

    @Override
    void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {

    }

    @Override
    void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {

    }

    @Override
    void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {

    }

    @Override
    void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {

    }

    @Override
    void updateObject(int columnIndex, Object x) throws SQLException {

    }

    @Override
    void updateNull(String columnLabel) throws SQLException {

    }

    @Override
    void updateBoolean(String columnLabel, boolean x) throws SQLException {

    }

    @Override
    void updateByte(String columnLabel, byte x) throws SQLException {

    }

    @Override
    void updateShort(String columnLabel, short x) throws SQLException {

    }

    @Override
    void updateInt(String columnLabel, int x) throws SQLException {

    }

    @Override
    void updateLong(String columnLabel, long x) throws SQLException {

    }

    @Override
    void updateFloat(String columnLabel, float x) throws SQLException {

    }

    @Override
    void updateDouble(String columnLabel, double x) throws SQLException {

    }

    @Override
    void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {

    }

    @Override
    void updateString(String columnLabel, String x) throws SQLException {

    }

    @Override
    void updateBytes(String columnLabel, byte[] x) throws SQLException {

    }

    @Override
    void updateDate(String columnLabel, Date x) throws SQLException {

    }

    @Override
    void updateTime(String columnLabel, Time x) throws SQLException {

    }

    @Override
    void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {

    }

    @Override
    void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {

    }

    @Override
    void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {

    }

    @Override
    void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {

    }

    @Override
    void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {

    }

    @Override
    void updateObject(String columnLabel, Object x) throws SQLException {

    }

    @Override
    void insertRow() throws SQLException {

    }

    @Override
    void updateRow() throws SQLException {

    }

    @Override
    void deleteRow() throws SQLException {

    }

    @Override
    void refreshRow() throws SQLException {

    }

    @Override
    void cancelRowUpdates() throws SQLException {

    }

    @Override
    void moveToInsertRow() throws SQLException {

    }

    @Override
    void moveToCurrentRow() throws SQLException {

    }

    @Override
    Statement getStatement() throws SQLException {
        return null
    }

    @Override
    Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        return null
    }

    @Override
    Ref getRef(int columnIndex) throws SQLException {
        return null
    }

    @Override
    Blob getBlob(int columnIndex) throws SQLException {
        return null
    }

    @Override
    Clob getClob(int columnIndex) throws SQLException {
        return null
    }

    @Override
    Array getArray(int columnIndex) throws SQLException {
        return null
    }

    @Override
    Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        return null
    }

    @Override
    Ref getRef(String columnLabel) throws SQLException {
        return null
    }

    @Override
    Blob getBlob(String columnLabel) throws SQLException {
        return null
    }

    @Override
    Clob getClob(String columnLabel) throws SQLException {
        return null
    }

    @Override
    Array getArray(String columnLabel) throws SQLException {
        return null
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
        return null
    }

    @Override
    URL getURL(String columnLabel) throws SQLException {
        return null
    }

    @Override
    void updateRef(int columnIndex, Ref x) throws SQLException {

    }

    @Override
    void updateRef(String columnLabel, Ref x) throws SQLException {

    }

    @Override
    void updateBlob(int columnIndex, Blob x) throws SQLException {

    }

    @Override
    void updateBlob(String columnLabel, Blob x) throws SQLException {

    }

    @Override
    void updateClob(int columnIndex, Clob x) throws SQLException {

    }

    @Override
    void updateClob(String columnLabel, Clob x) throws SQLException {

    }

    @Override
    void updateArray(int columnIndex, Array x) throws SQLException {

    }

    @Override
    void updateArray(String columnLabel, Array x) throws SQLException {

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
    boolean isClosed() throws SQLException {
        return false
    }

    @Override
    void updateNString(int columnIndex, String nString) throws SQLException {

    }

    @Override
    void updateNString(String columnLabel, String nString) throws SQLException {

    }

    @Override
    void updateNClob(int columnIndex, NClob nClob) throws SQLException {

    }

    @Override
    void updateNClob(String columnLabel, NClob nClob) throws SQLException {

    }

    @Override
    NClob getNClob(int columnIndex) throws SQLException {
        return null
    }

    @Override
    NClob getNClob(String columnLabel) throws SQLException {
        return null
    }

    @Override
    SQLXML getSQLXML(int columnIndex) throws SQLException {
        return null
    }

    @Override
    SQLXML getSQLXML(String columnLabel) throws SQLException {
        return null
    }

    @Override
    void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {

    }

    @Override
    void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {

    }

    @Override
    String getNString(int columnIndex) throws SQLException {
        return null
    }

    @Override
    String getNString(String columnLabel) throws SQLException {
        return null
    }

    @Override
    Reader getNCharacterStream(int columnIndex) throws SQLException {
        return null
    }

    @Override
    Reader getNCharacterStream(String columnLabel) throws SQLException {
        return null
    }

    @Override
    void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {

    }

    @Override
    void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {

    }

    @Override
    void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {

    }

    @Override
    void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {

    }

    @Override
    void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {

    }

    @Override
    void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {

    }

    @Override
    void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {

    }

    @Override
    void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {

    }

    @Override
    void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {

    }

    @Override
    void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {

    }

    @Override
    void updateClob(int columnIndex, Reader reader, long length) throws SQLException {

    }

    @Override
    void updateClob(String columnLabel, Reader reader, long length) throws SQLException {

    }

    @Override
    void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {

    }

    @Override
    void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {

    }

    @Override
    void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {

    }

    @Override
    void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {

    }

    @Override
    void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {

    }

    @Override
    void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {

    }

    @Override
    void updateCharacterStream(int columnIndex, Reader x) throws SQLException {

    }

    @Override
    void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {

    }

    @Override
    void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {

    }

    @Override
    void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {

    }

    @Override
    void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {

    }

    @Override
    void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {

    }

    @Override
    void updateClob(int columnIndex, Reader reader) throws SQLException {

    }

    @Override
    void updateClob(String columnLabel, Reader reader) throws SQLException {

    }

    @Override
    void updateNClob(int columnIndex, Reader reader) throws SQLException {

    }

    @Override
    void updateNClob(String columnLabel, Reader reader) throws SQLException {

    }

    @Override
    def <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        return null
    }

    @Override
    def <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        return null
    }

    @Override
    def <T> T unwrap(Class<T> iface) throws SQLException {
        return null
    }

    @Override
    boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false
    }
}