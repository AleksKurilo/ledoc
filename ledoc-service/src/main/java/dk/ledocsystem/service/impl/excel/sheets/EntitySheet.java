package dk.ledocsystem.service.impl.excel.sheets;

import java.util.List;

public interface EntitySheet {

    List<String> getHeaders();

    List<Row> getRows();

    String getName();
}
