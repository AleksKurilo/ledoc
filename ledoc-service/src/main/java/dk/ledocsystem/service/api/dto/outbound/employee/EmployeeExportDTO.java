package dk.ledocsystem.service.api.dto.outbound.employee;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        Class<? extends EmployeeExportDTO> componentClass = getClass();
        Field[] fields = componentClass.getDeclaredFields();
        List<String> lines = new ArrayList<>(fields.length);

        Arrays.stream(fields)
                .forEach(
                        field -> {
                            field.setAccessible(true);
                            try {
                                lines.add(field.get(this) != null ? field.get(this).toString() : "");
                            } catch (final IllegalAccessException e) {
                                lines.add("");
                            }
                        });

        return lines;
    }
}
