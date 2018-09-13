package dk.ledocsystem.ledoc.excel.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface Sheet {

    default List<String> getHeaders() {return Collections.singletonList("");}

    default String getQuery() {return "";}

    default String getName() {return "";}
}
