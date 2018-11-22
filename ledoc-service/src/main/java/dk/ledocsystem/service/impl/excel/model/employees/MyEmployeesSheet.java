package dk.ledocsystem.service.impl.excel.model.employees;

public class MyEmployeesSheet extends AbstractEmployeesSheet {

    private static final String QUERY = "select concat(main.employees.first_name, ' ', main.employees.last_name) as name," +
            " title, cell_phone, phone_number from main.employees where main.employees.archived is false";

    @Override
    public String getQuery() {
        return QUERY;
    }

    @Override
    public String getName() {
        return "myEmployee";
    }
}
