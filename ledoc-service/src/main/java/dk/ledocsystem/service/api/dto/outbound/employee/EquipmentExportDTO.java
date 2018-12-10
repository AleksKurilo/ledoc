package dk.ledocsystem.service.api.dto.outbound.employee;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class EquipmentExportDTO {

    String name;
    String categoryName;
    String idNumber;
    String serialNumber;
    String homeLocation;
    String currentLocation;
    String reviewResponsible;
    String loanStatus;
    String status;
    String nextReviewDate;
    String supplier;
    String reviewStatus;
    String mustBeReviewed;
    String authenticationType;
    String responsible;
    String localId;

    public List<String> getFields() {
        Class<? extends EquipmentExportDTO> componentClass = getClass();
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
