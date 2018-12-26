package dk.ledocsystem.service.api;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.security.UserAuthorities;
import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.ChangePasswordDTO;
import dk.ledocsystem.service.api.dto.inbound.employee.EmployeeCreateDTO;
import dk.ledocsystem.service.api.dto.inbound.employee.EmployeeDTO;
import dk.ledocsystem.service.api.dto.inbound.employee.EmployeeFollowDTO;
import dk.ledocsystem.service.api.dto.inbound.review.ReviewDTO;
import dk.ledocsystem.service.api.dto.outbound.employee.EmployeeExportDTO;
import dk.ledocsystem.service.api.dto.outbound.employee.EmployeePreviewDTO;
import dk.ledocsystem.service.api.dto.outbound.employee.EmployeeSummary;
import dk.ledocsystem.service.api.dto.outbound.employee.GetEmployeeDTO;
import dk.ledocsystem.service.api.dto.outbound.employee.GetFollowedEmployeeDTO;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface EmployeeService extends CustomerBasedDomainService<GetEmployeeDTO> {

    /**
     * Creates new {@link dk.ledocsystem.data.model.employee.Employee}, using the data from {@code employeeCreateDTO}.
     *
     * @param employeeCreateDTO Employee properties
     * @param creatorDetails    Creator
     * @return Newly created {@link GetEmployeeDTO employee}
     */
    GetEmployeeDTO createEmployee(EmployeeCreateDTO employeeCreateDTO, UserDetails creatorDetails);

    /**
     * Creates new {@link dk.ledocsystem.data.model.employee.Employee}, using the data from {@code employeeCreateDTO},
     * and assigns {@code customer} to it.
     *
     * @param employeeCreateDTO Employee properties
     * @param customerId        ID of the customer - the owner of employee
     * @param creatorDetails    Creator
     * @return Newly created {@link GetEmployeeDTO employee}
     */
    GetEmployeeDTO createEmployee(EmployeeCreateDTO employeeCreateDTO, Long customerId, UserDetails creatorDetails);

    /**
     * Creates new {@link dk.ledocsystem.data.model.employee.Employee point of contact}, using the data from {@code employeeCreateDTO}.
     *
     * @param employeeCreateDTO Point of contact properties
     * @param creatorDetails    Creator
     * @return Newly created {@link GetEmployeeDTO point of contact}
     */
    GetEmployeeDTO createPointOfContact(EmployeeCreateDTO employeeCreateDTO, UserDetails creatorDetails);

    /**
     * Updates the properties of the employee with the given ID with properties of {@code employeeCreateDTO}.
     *
     * @param employeeDTO New properties of the employee
     * @param currentUser Current user
     * @return Updated {@link GetEmployeeDTO employee}
     */
    GetEmployeeDTO updateEmployee(EmployeeDTO employeeDTO, UserDetails currentUser);

    /**
     * Changes password of the user with given email.
     *
     * @param username    Username identifying the user
     * @param newPassword New password
     */
    void changePassword(String username, String newPassword);

    /**
     * Changes the archived status according to data from {@code archivedStatusDTO}.
     */
    void changeArchivedStatus(Long employeeId, ArchivedStatusDTO archivedStatusDTO, UserDetails currentUser);

    /**
     * Changes password fot the given employee.
     */
    void changePassword(Long employeeId, ChangePasswordDTO changePasswordDTO);

    /**
     * Grants the provided {@link UserAuthorities authorities} to employee.
     *
     * @param employeeId  ID of the employee
     * @param authorities Authorities
     */
    void grantAuthorities(Long employeeId, UserAuthorities authorities);

    /**
     * Revokes the provided {@link UserAuthorities authorities} from employee.
     *
     * @param employeeId  ID of the employee
     * @param authorities Authorities
     */
    void revokeAuthorities(Long employeeId, UserAuthorities authorities);

    /**
     * Performs review of the given employee.
     *
     * @param employeeId ID of the employee
     * @param reviewDTO  Information about performed review
     */
    void performReview(Long employeeId, ReviewDTO reviewDTO, UserDetails currentUser);

    List<EmployeeSummary> getAllNamesByCustomer(Long customerId);

    Optional<GetEmployeeDTO> getByUsername(String username);

    boolean existsByUsername(String username);

    Page<GetEmployeeDTO> getNewEmployees(UserDetails user, Pageable pageable);

    Page<GetEmployeeDTO> getNewEmployees(UserDetails user, Pageable pageable, Predicate predicate);

    Optional<EmployeePreviewDTO> getPreviewDtoById(Long employeeId, boolean isSaveLog, UserDetails currentUser);

    void follow(Long employeeId, UserDetails currentUser, EmployeeFollowDTO employeeFollowDTO);

    Page<GetFollowedEmployeeDTO> getFollowedEmployees(Long employeeId, Pageable pageable);

    List<EmployeeExportDTO> getAllForExport(UserDetails user, Predicate predicate, boolean isNew);

    Workbook exportToExcel(UserDetails currentUserDetails, Predicate predicate, boolean isNew, boolean isArchived);
}
