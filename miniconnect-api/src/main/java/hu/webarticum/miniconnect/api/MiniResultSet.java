package hu.webarticum.miniconnect.api;

import java.io.Closeable;
import java.util.List;

public interface MiniResultSet extends Closeable, Iterable<List<MiniValue>> {

    // TODO: client-server serialization...
    // public ??? columnHeaders();
    
    public List<String> columnNames();
    
    public boolean isClosed();
    
}
