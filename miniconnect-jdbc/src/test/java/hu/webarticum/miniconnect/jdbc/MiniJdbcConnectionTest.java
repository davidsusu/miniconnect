package hu.webarticum.miniconnect.jdbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.jdbcadapter.JdbcAdapterSession;
import hu.webarticum.miniconnect.messenger.adapter.MessengerSession;
import hu.webarticum.miniconnect.messenger.lab.dummy.DummyMessenger;

class MiniJdbcConnectionTest {
    
    // TODO: eliminate the use of DummyMessenger
    @Test
    void testResultSetWithDummyMessenger() throws Exception {
        Map<String, String> contents = new LinkedHashMap<>();
        contents.put("first", "Lorem ipsum");
        contents.put("second", "Hello World");
        contents.put("third", "Apple Banana");
        try (MiniSession session = new MessengerSession(1L, new DummyMessenger())) {
            for (Map.Entry<String, String> entry : contents.entrySet()) {
                String name = entry.getKey();
                String content = entry.getValue();
                byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
                InputStream dataSource = new ByteArrayInputStream(contentBytes);
                session.putLargeData(name, contentBytes.length, dataSource);
            }
            
            // FIXME: close?
            Connection connection = new MiniJdbcConnection(session);
            try (Statement selectStatement = connection.createStatement()) {
                boolean executeResult = selectStatement.execute("SELECT * FROM data");
                assertThat(executeResult).isTrue();
                
                try (ResultSet resultSet = selectStatement.getResultSet()) {
                    assertThat(resultSet.findColumn("id")).isEqualTo(1);
                    assertThat(resultSet.findColumn("name")).isEqualTo(3);
                    assertThatThrownBy(() -> resultSet.findColumn("xxxxx"))
                            .isInstanceOf(SQLException.class);
                    
                    ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                    assertThat(resultSetMetaData.getColumnCount()).isEqualTo(5);
                    assertThat(resultSetMetaData.getColumnLabel(1)).isEqualTo("id");
                    assertThat(resultSetMetaData.getColumnLabel(2)).isEqualTo("created_at");
                    assertThat(resultSetMetaData.getColumnLabel(3)).isEqualTo("name");
                    assertThat(resultSetMetaData.getColumnLabel(4)).isEqualTo("length");
                    assertThat(resultSetMetaData.getColumnLabel(5)).isEqualTo("content");
                    
                    boolean next1Result = resultSet.next();
                    assertThat(next1Result).isTrue();
                    assertThat(resultSet.getLong(1)).isEqualTo(1L);
                    assertThat(resultSet.getString(3)).isEqualTo("first");
                    assertThat(resultSet.getString(5)).isEqualTo("Lorem ipsum");
                    
                    boolean next2Result = resultSet.next();
                    assertThat(next2Result).isTrue();
                    assertThat(resultSet.getLong(1)).isEqualTo(2L);
                    assertThat(resultSet.getString(3)).isEqualTo("second");
                    assertThat(resultSet.getString(5)).isEqualTo("Hello World");
                    
                    boolean next3Result = resultSet.next();
                    assertThat(next3Result).isTrue();
                    assertThat(resultSet.getLong(1)).isEqualTo(3L);
                    assertThat(resultSet.getString(3)).isEqualTo("third");
                    assertThat(resultSet.getString(5)).isEqualTo("Apple Banana");
                    
                    boolean next4Result = resultSet.next();
                    assertThat(next4Result).isFalse();
                }
            }
        }
    }

    @Test
    void testStatementBatch() throws Exception {
        try (
                Connection baseConnection = createInMemoryConnection();
                MiniSession miniSession = new JdbcAdapterSession(baseConnection);
                Connection connection = new MiniJdbcConnection(miniSession);
                ) {
            
            // TODO
            
        }
    }

    @Test
    void testPreparedStatement() throws Exception {
        try (
                Connection baseConnection = createInMemoryConnection();
                MiniSession miniSession = new JdbcAdapterSession(baseConnection);
                Connection connection = new MiniJdbcConnection(miniSession);
                ) {
            
            // TODO
            
        }
    }

    @Test
    void testPreparedStatementBatch() throws Exception {
        try (
                Connection baseConnection = createInMemoryConnection();
                MiniSession miniSession = new JdbcAdapterSession(baseConnection);
                Connection connection = new MiniJdbcConnection(miniSession);
                ) {
            
            // TODO
            
        }
    }
    
    private Connection createInMemoryConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
    }
    
}
