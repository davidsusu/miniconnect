package hu.webarticum.miniconnect.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import hu.webarticum.miniconnect.api.MiniResult;

public class MiniJdbcStatement extends AbstractJdbcStatement {

    public MiniJdbcStatement(MiniJdbcConnection connection) {
        super(connection);
    }


    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        // TODO
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        // TODO
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return false; // TODO
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        return null; // TODO
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return 0; // TODO
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return 0; // TODO
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return 0; // TODO
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return 0; // TODO
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        
        // FIXME
        
        MiniResult result = getConnection().getMiniSession().execute(sql);
        if (!result.success()) {
            throw new SQLException(
                    result.errorMessage(),
                    result.sqlState(),
                    0); // FIXME result.errorCode());
        }
        
        boolean hasResultSet = result.hasResultSet();
        MiniJdbcResultSet jdbcResultSet =
                hasResultSet ?
                new MiniJdbcResultSet(this, result.resultSet()) :
                null;
        ResultHolder resultHolder = new ResultHolder(result, jdbcResultSet);
        setCurrentResult(resultHolder);
        return hasResultSet;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return false; // TODO
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return false; // TODO
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return false; // TODO
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        // TODO
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return null; // TODO
    }

    @Override
    public void clearBatch() throws SQLException {
        // TODO
    }

}
