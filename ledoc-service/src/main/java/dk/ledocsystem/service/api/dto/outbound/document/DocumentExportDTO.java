package dk.ledocsystem.service.api.dto.outbound.document;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class DocumentExportDTO {

    String name;
    String category;
    String subcategory;
    String trades;
    String version;
    String dueDate;
    String locationNames;
    String responsible;

    public List<String> getFields() {
        Class<? extends DocumentExportDTO> componentClass = getClass();
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
