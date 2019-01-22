package dk.ledocsystem.service.api;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.service.api.dto.inbound.customer.CustomerCreateDTO;
import dk.ledocsystem.service.api.dto.inbound.customer.CustomerEditDTO;
import dk.ledocsystem.service.api.dto.outbound.customer.FullCustomerExportDTO;
import dk.ledocsystem.service.api.dto.outbound.customer.ShortCustomerExportDTO;
import dk.ledocsystem.service.api.dto.outbound.customer.GetCustomerDTO;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface CustomerService extends DomainService<GetCustomerDTO> {

    /**
     * Creates new {@link dk.ledocsystem.data.model.Customer}, using the data from {@code customerCreateDTO}.
     *
     * @param customerCreateDTO Customer details
     * @param creatorDetails    Creator
     * @return Newly created {@link GetCustomerDTO customer}
     */
    GetCustomerDTO createCustomer(CustomerCreateDTO customerCreateDTO, UserDetails creatorDetails);

    /**
     * Updates the properties of the customer with the given ID with properties of {@code customerCreateDTO}.
     *
     * @param customerEditDTO New properties of the customer
     * @return Updated {@link GetCustomerDTO customer}
     */
    GetCustomerDTO updateCustomer(CustomerEditDTO customerEditDTO);

    /**
     * Changes the archived status according to {@code archived}.
     */
    void changeArchivedStatus(Long customerId, Boolean archived);

    /**
     * @param username Username
     * @return Customer of the user with the given name
     */
    GetCustomerDTO getByUsername(String username);

    List<ShortCustomerExportDTO> getAllForExportShort(Predicate predicate);

    List<FullCustomerExportDTO> getAllForExportFull(Predicate predicate);

    Workbook exportToExcelShort(Predicate predicate);

    Workbook exportToExcelFull(Predicate predicate);
}
