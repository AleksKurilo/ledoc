package dk.ledocsystem.service.api.dto.outbound.document;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return Stream.of(name, category, subcategory, trades, version, dueDate, locationNames, responsible)
                .map(Strings::nullToEmpty)
                .collect(Collectors.toList());
    }
}
