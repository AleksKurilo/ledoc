package dk.ledocsystem.service.api.dto.outbound.equipment;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return Stream.of(name, categoryName, idNumber, serialNumber, homeLocation, currentLocation, reviewResponsible,
                loanStatus, status, nextReviewDate, supplier, reviewStatus, mustBeReviewed, authenticationType,
                responsible, localId)
                .map(Strings::nullToEmpty)
                .collect(Collectors.toList());
    }
}
