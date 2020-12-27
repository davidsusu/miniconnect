package hu.webarticum.miniconnect.util.lab.dummy;

import java.util.ArrayList;
import java.util.List;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniResultSet;

public class DummyResult implements MiniResult {
    
    private final boolean success;
    
    private final String errorMessage;
    
    private final DummyResultSet resultSet;
    

    public DummyResult() {
        this(true, "", new DummyResultSet());
    }

    public DummyResult(String errorMessage) {
        this(false, errorMessage, new DummyResultSet());
    }

    public DummyResult(DummyResultSet resultSet) {
        this(true, "", resultSet);
    }
    
    public DummyResult(boolean success, String errorMessage, DummyResultSet resultSet) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.resultSet = resultSet;
    }
    
    
    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String errorMessage() {
        return errorMessage;
    }

    @Override
    public List<String> warnings() {
        return new ArrayList<>();
    }

    @Override
    public MiniResultSet resultSet() {
        return resultSet;
    }

}
