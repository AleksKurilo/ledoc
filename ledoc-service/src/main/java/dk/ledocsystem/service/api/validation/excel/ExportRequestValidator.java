package dk.ledocsystem.service.api.validation.excel;

import dk.ledocsystem.service.impl.excel.model.Table;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Set;

class ExportRequestValidator implements ConstraintValidator<ExportRequest, dk.ledocsystem.service.impl.excel.model.ExportRequest> {

    @Override
    public boolean isValid(dk.ledocsystem.service.impl.excel.model.ExportRequest value, ConstraintValidatorContext context) {
        List<Table> allowedTables = value.getModule().getTables();
        Set<Table> requestedTables = value.getTables();
        return allowedTables.containsAll(requestedTables);
    }
}
