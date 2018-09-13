package dk.ledocsystem.ledoc.excel.model.employees;

import dk.ledocsystem.ledoc.excel.model.Sheet;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractEmployeesSheet implements Sheet {

    @Override
    public List<String> getHeaders() {
        return Arrays.asList("NAME", "TITLE", "DUE_DATE", "USERNAME", "CELL_PHONE", "PHONE_NUMBER", "LOCATIONS");
    }
}
