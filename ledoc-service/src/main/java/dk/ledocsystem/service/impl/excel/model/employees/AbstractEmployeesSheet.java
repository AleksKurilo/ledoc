package dk.ledocsystem.service.impl.excel.model.employees;

import dk.ledocsystem.service.impl.excel.model.Sheet;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractEmployeesSheet implements Sheet {

    @Override
    public List<String> getHeaders() {
        return Arrays.asList("NAME", "TITLE", "DUE_DATE", "USERNAME", "CELL_PHONE", "PHONE_NUMBER", "LOCATIONS");
    }
}
