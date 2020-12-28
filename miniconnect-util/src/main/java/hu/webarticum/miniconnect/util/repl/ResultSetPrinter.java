package hu.webarticum.miniconnect.util.repl;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.util.value.XxxValueEncoder;

public class ResultSetPrinter {
    
    private static final String NULL_PLACEHOLDER = "[NULL]";
    

    public void print(MiniResultSet resultSet, PrintStream out) {


        // TODO
        

        out.println();

        out.println("---------------------");
        
        List<MiniColumnHeader> columnHeaders = resultSet.columnHeaders();
        for (MiniColumnHeader columnHeader : columnHeaders) {
            out.print(columnHeader.name() + " | ");
        }
        out.println();

        out.println("---------------------");
        
        int rowLength = columnHeaders.size();
        for (List<MiniValue> row : resultSet) {
            for (int i = 0; i < rowLength; i++) {
                MiniValue value = row.get(i);
                MiniColumnHeader columnHeader = columnHeaders.get(i);
                out.print(stringifyValue(columnHeader, value));
                out.print(", ");
            }
            out.println();
        }
        
        out.println();
        
    }
    
    public String stringifyValue(MiniColumnHeader columnHeader, MiniValue value) {
        if (!value.isNull()) {
            return new XxxValueEncoder(columnHeader).decode(value).toString();
        } else {
            return NULL_PLACEHOLDER;
        }
    }
    
}
