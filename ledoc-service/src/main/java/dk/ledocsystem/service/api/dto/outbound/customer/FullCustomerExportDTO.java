package dk.ledocsystem.service.api.dto.outbound.customer;

import com.google.common.base.Strings;
import dk.ledocsystem.service.api.dto.outbound.location.GetAddressDTO;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class FullCustomerExportDTO {

    private Long id;

    private String name;

    private String cvr;

    private String companyEmail;

    private String contactPhone;

    private LocalDate dateOfCreation;

    private GetAddressDTO address = new GetAddressDTO();

    private String pointOfContact;

    private Long countOfActiveSuppliers;

    private Long countOfAllSuppliers;

    private Long countOfActiveEmployees;

    private Long countOfAllEmployees;

    private Long countOfActiveDocuments;

    private Long countOfAllDocuments;

    private Long countOfActiveEquipment;

    private Long countOfAllEquipment;

    private Long countOfReviewTemplates;

    private Long countOfEmployeeReviewTemplates;

    private Long countOfLocations;

    public List<String> getFields() {
        return Stream.of(name, cvr, dateOfCreation.toString(), countOfActiveSuppliers.toString(),
                countOfAllSuppliers.toString(), countOfActiveEmployees.toString(), countOfAllEmployees.toString(),
                countOfActiveDocuments.toString(), countOfAllDocuments.toString(), countOfActiveEquipment.toString(),
                countOfAllEquipment.toString(), countOfReviewTemplates.toString(), countOfEmployeeReviewTemplates.toString(),
                countOfLocations.toString(), contactPhone, companyEmail, address.getPostalCode(), address.getCity(),
                address.getStreet(), address.getBuildingNumber(), address.getDistrict(), pointOfContact)
                .map(Strings::nullToEmpty)
                .collect(Collectors.toList());
    }
}
