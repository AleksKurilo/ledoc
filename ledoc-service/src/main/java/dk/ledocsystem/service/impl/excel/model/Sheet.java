package dk.ledocsystem.service.impl.excel.model;

import java.util.List;

public interface Sheet {

    List<String> getHeaders();

    String getQuery();

    String getName();

    default Object[] getParams() {
        return new Object[0];
    }
}
