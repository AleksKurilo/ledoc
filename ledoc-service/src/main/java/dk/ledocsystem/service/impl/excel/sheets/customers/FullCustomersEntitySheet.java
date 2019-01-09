package dk.ledocsystem.service.impl.excel.sheets.customers;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.service.api.CustomerService;
import dk.ledocsystem.service.api.dto.outbound.customer.FullCustomerExportDTO;
import dk.ledocsystem.service.impl.excel.sheets.EntitySheet;
import dk.ledocsystem.service.impl.excel.sheets.Row;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class FullCustomersEntitySheet implements EntitySheet {

    private final CustomerService customerService;
    private Predicate predicate;
    private String name;

    @Override
    public List<String> getHeaders() {
        return Arrays.asList("NAME", "CVR", "CREATION DATE", "ACTIVE SUPPLIERS", "ALL SUPPLIERS", "ACTIVE EMPLOYEES",
                "ALL EMPLOYEES", "ACTIVE DOCUMENTS", "ALL DOCUMENTS", "ACTIVE EQUIPMENT", "ALL EQUIPMENT",
                "REVIEW TEMPLATES", "EMPLOYEE REVIEW TEMPLATES", "LOCATIONS", "PHONE NUMBER", "COMPANY EMAIL",
                "POSTAL CODE", "CITY", "STREET", "BUILDING NUMBER", "DISTRICT", "POINT OF CONTACT");
    }

    @Override
    public List<Row> getRows() {
        return customerService.getAllForExportFull(predicate)
                .stream()
                .map(FullCustomerExportDTO::getFields)
                .map(Row::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return name;
    }
}
