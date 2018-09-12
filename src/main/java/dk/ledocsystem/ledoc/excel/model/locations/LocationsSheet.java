package dk.ledocsystem.ledoc.excel.model.locations;

import dk.ledocsystem.ledoc.excel.model.Sheet;

import java.util.Arrays;
import java.util.List;

public class LocationsSheet implements Sheet {

    private static final String QUERY = "select main.locations.name, " +
            "concat(main.employees.first_name, ' ', main.employees.last_name) as responsible\n" +
            "from main.locations LEFT JOIN main.employees on main.locations.responsible_id = main.employees.id";

    @Override
    public List<String> getHeaders() {
        return Arrays.asList("NAME", "RESPONSIBLE");
    }

    @Override
    public String getQuery() {
        return QUERY;
    }

    @Override
    public String getName() {
        return "locations";
    }
}