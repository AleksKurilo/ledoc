package dk.ledocsystem.service.impl.excel.sheets.customers;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.service.api.CustomerService;
import dk.ledocsystem.service.api.dto.outbound.customer.ShortCustomerExportDTO;
import dk.ledocsystem.service.impl.excel.sheets.EntitySheet;
import dk.ledocsystem.service.impl.excel.sheets.Row;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class ShortCustomersEntitySheet implements EntitySheet {

    private final CustomerService customerService;
    private Predicate predicate;
    private String name;

    @Override
    public List<String> getHeaders() {
        return Arrays.asList("NAME", "COMPANY EMAIL");
    }

    @Override
    public List<Row> getRows() {
        return customerService.getAllForExportShort(predicate)
                .stream()
                .map(ShortCustomerExportDTO::getFields)
                .map(Row::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return name;
    }
}
