package hu.webarticum.miniconnect.jdbcadapter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.tool.result.DefaultValueInterpreter;
import hu.webarticum.miniconnect.tool.result.StoredColumnHeader;
import hu.webarticum.miniconnect.util.data.ByteString;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class JdbcAdapterResultSet implements MiniResultSet {
    
    private static final Map<Integer, Class<?>> TYPE_MAPPING =
            Collections.synchronizedMap(new HashMap<>());
    static {
        TYPE_MAPPING.put(Types.BIT, Boolean.class);
        TYPE_MAPPING.put(Types.TINYINT, Integer.class);
        TYPE_MAPPING.put(Types.SMALLINT, Integer.class);
        TYPE_MAPPING.put(Types.INTEGER, Integer.class);
        TYPE_MAPPING.put(Types.BIGINT, Long.class);
        TYPE_MAPPING.put(Types.NUMERIC, BigDecimal.class);
        TYPE_MAPPING.put(Types.DECIMAL, BigDecimal.class);
        TYPE_MAPPING.put(Types.REAL, Float.class);
        TYPE_MAPPING.put(Types.FLOAT, Double.class);
        TYPE_MAPPING.put(Types.DOUBLE, Double.class);
        TYPE_MAPPING.put(Types.BINARY, ByteString.class);
        TYPE_MAPPING.put(Types.VARBINARY, ByteString.class);
        TYPE_MAPPING.put(Types.LONGVARBINARY, ByteString.class);
        TYPE_MAPPING.put(Types.CHAR, String.class);
        TYPE_MAPPING.put(Types.NCHAR, String.class);
        TYPE_MAPPING.put(Types.VARCHAR, String.class);
        TYPE_MAPPING.put(Types.NVARCHAR, String.class);
        TYPE_MAPPING.put(Types.LONGVARCHAR, String.class);
        TYPE_MAPPING.put(Types.LONGNVARCHAR, String.class);
        
        // FIXME
        TYPE_MAPPING.put(Types.BLOB, ByteString.class);
        TYPE_MAPPING.put(Types.CLOB, String.class);
        TYPE_MAPPING.put(Types.NCLOB, String.class);
        
        // TODO: more
        // TODO: settings, encodings etc.
        
    }
    
    
    private final ImmutableList<Class<?>> javaTypes;
    
    private final ImmutableList<DefaultValueInterpreter> interpreters;
    
    private final ImmutableList<MiniColumnHeader> columnHeaders;
    
    private final Statement jdbcStatement;
    
    private final ResultSet jdbcResultSet;
    
    
    public JdbcAdapterResultSet(Statement jdbcStatement, ResultSet jdbcResultSet) {
        this.jdbcStatement = jdbcStatement;
        this.jdbcResultSet = jdbcResultSet;
        this.javaTypes = extractJavaTypes();
        this.interpreters = javaTypes.map(DefaultValueInterpreter::new);
        this.columnHeaders = extractColumnHeaders();
    }

    private final ImmutableList<Class<?>> extractJavaTypes() {
        try {
            return extractJavaTypesThrowing();
        } catch (SQLException e) {
            throw new UncheckedSqlException(e);
        }
    }
    
    
    private final ImmutableList<Class<?>> extractJavaTypesThrowing() throws SQLException {
        ResultSetMetaData jdbcMetaData = jdbcResultSet.getMetaData();
        int columnCount = jdbcMetaData.getColumnCount();
        List<Class<?>> resultBuilder = new ArrayList<>(columnCount);
        for (int c = 1; c <= columnCount; c++) {
            resultBuilder.add(extractJavaTypeThrowing(jdbcMetaData, c));
        }
        return new ImmutableList<>(resultBuilder);
    }
    
    private Class<?> extractJavaTypeThrowing(
            ResultSetMetaData jdbcMetaData, int c) throws SQLException {
        
        return getJavaTypeOf(jdbcMetaData.getColumnType(c));
    }
    
    // FIXME
    private Class<?> getJavaTypeOf(int jdbcType) {
        if (!TYPE_MAPPING.containsKey(jdbcType)) {
            throw new IllegalArgumentException("Unsupported type flag: " + jdbcType);
        }
        
        return TYPE_MAPPING.get(jdbcType);
    }
    
    private ImmutableList<MiniColumnHeader> extractColumnHeaders() {
        try {
            return extractColumnHeadersThrowing();
        } catch (SQLException e) {
            throw new UncheckedSqlException(e);
        }
    }
    
    private ImmutableList<MiniColumnHeader> extractColumnHeadersThrowing() throws SQLException {
        ResultSetMetaData jdbcMetaData = jdbcResultSet.getMetaData();
        int columnCount = jdbcMetaData.getColumnCount();
        List<MiniColumnHeader> resultBuilder = new ArrayList<>(columnCount);
        for (int i = 0; i < columnCount; i++) {
            int c = i + 1;
            String name = jdbcMetaData.getColumnLabel(c);
            MiniValueDefinition valueDefinition = interpreters.get(i).definition();
            resultBuilder.add(new StoredColumnHeader(name, valueDefinition));
        }
        return new ImmutableList<>(resultBuilder);
    }
    

    @Override
    public ImmutableList<MiniColumnHeader> columnHeaders() {
        return columnHeaders;
    }

    @Override
    public ImmutableList<MiniValue> fetch() {
        try {
            return fetchThrowing();
        } catch (SQLException e) {
            throw new UncheckedSqlException(e);
        }
    }
    
    private ImmutableList<MiniValue> fetchThrowing() throws SQLException {
        if (!jdbcResultSet.next()) {
            return null;
        }
        
        return extractRowThrowing();
    }
    
    // TODO
    private ImmutableList<MiniValue> extractRowThrowing() throws SQLException {
        int columnCount = jdbcResultSet.getMetaData().getColumnCount();
        List<MiniValue> resultBuilder = new ArrayList<>(columnCount);
        for (int i = 0; i < columnCount; i++) {
            int c = i + 1;
            DefaultValueInterpreter interpreter = interpreters.get(i);
            Class<?> mappingType = javaTypes.get(i);
            // XXX
            if (mappingType == ByteString.class) {
                mappingType = byte[].class;
            }
            Object content = jdbcResultSet.getObject(c, mappingType);
            MiniValue value = interpreter.encode(content);
            resultBuilder.add(value);
        }
        return new ImmutableList<>(resultBuilder);
    }

    @Override
    public void close() {
        try {
            jdbcResultSet.close();
            jdbcStatement.close();
        } catch (SQLException e) {
            throw new UncheckedIOException(new IOException("Unexpected SQLException", e));
        }
    }

}
