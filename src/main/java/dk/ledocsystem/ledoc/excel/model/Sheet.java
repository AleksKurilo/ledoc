package dk.ledocsystem.ledoc.excel.model;

import java.util.List;

public interface Sheet {

    List<String> getHeaders();

    String getQuery();

    String getName();
}
