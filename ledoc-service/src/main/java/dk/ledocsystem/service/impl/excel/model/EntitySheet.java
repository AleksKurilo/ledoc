package dk.ledocsystem.service.impl.excel.model;

import java.util.List;

public interface EntitySheet {

    List<String> getHeaders();

    List<List<String>> getRows();

    String getName();
}
