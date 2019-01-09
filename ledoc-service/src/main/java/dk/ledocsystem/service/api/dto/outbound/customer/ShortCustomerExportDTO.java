package dk.ledocsystem.service.api.dto.outbound.customer;

import com.google.common.base.Strings;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class ShortCustomerExportDTO {

    private String name;

    private String companyEmail;

    public List<String> getFields() {
        return Stream.of(name, companyEmail).map(Strings::nullToEmpty).collect(Collectors.toList());
    }
}
