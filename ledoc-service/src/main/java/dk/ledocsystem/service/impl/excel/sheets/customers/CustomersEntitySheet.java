package dk.ledocsystem.service.impl.excel.sheets.customers;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.service.api.CustomerService;
import dk.ledocsystem.service.impl.excel.sheets.EntitySheet;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class CustomersEntitySheet implements EntitySheet {

    private final CustomerService customerService;
    private Predicate predicate;
    private String name;

    @Override
    public List<String> getHeaders() {
        return Arrays.asList("NAME", "COMPANY EMAIL");
    }

    @Override
    public List<List<String>> getRows() {
        return customerService.getAllForExport(predicate);
    }

    @Override
    public String getName() {
        return name;
    }
}
