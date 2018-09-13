package dk.ledocsystem.ledoc.excel.model.locations;

import dk.ledocsystem.ledoc.excel.model.Sheet;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractLocationsSheet implements Sheet {

    @Override
    public List<String> getHeaders() {
        return Arrays.asList("NAME", "RESPONSIBLE");
    }
}
