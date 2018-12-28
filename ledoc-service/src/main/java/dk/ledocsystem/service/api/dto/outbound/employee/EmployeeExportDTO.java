package dk.ledocsystem.service.api.dto.outbound.employee;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public class EmployeeExportDTO {

    String name;
    String title;
    String username;
    String cellPhone;
    String phoneNumber;
    String locationNames;

    public List<String> getFields() {
        return Stream.of(name, title, username, cellPhone, phoneNumber, locationNames)
                .map(Strings::nullToEmpty)
                .collect(Collectors.toList());
    }
}
