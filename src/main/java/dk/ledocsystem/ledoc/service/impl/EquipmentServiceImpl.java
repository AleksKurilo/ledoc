package dk.ledocsystem.ledoc.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import dk.ledocsystem.ledoc.dto.equipment.AuthenticationTypeDTO;
import dk.ledocsystem.ledoc.dto.equipment.EquipmentCategoryCreateDTO;
import dk.ledocsystem.ledoc.dto.equipment.EquipmentCreateDTO;
import dk.ledocsystem.ledoc.dto.equipment.EquipmentEditDTO;
import dk.ledocsystem.ledoc.dto.equipment.EquipmentLoanDTO;
import dk.ledocsystem.ledoc.dto.projections.IdAndLocalizedName;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.email_notifications.EmailNotification;
import dk.ledocsystem.ledoc.model.equipment.ApprovalType;
import dk.ledocsystem.ledoc.model.equipment.AuthenticationType;
import dk.ledocsystem.ledoc.model.equipment.Equipment;
import dk.ledocsystem.ledoc.model.equipment.EquipmentCategory;
import dk.ledocsystem.ledoc.model.Location;
import dk.ledocsystem.ledoc.model.equipment.EquipmentLoan;
import dk.ledocsystem.ledoc.model.equipment.QEquipment;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.repository.AuthenticationTypeRepository;
import dk.ledocsystem.ledoc.repository.EmailNotificationRepository;
import dk.ledocsystem.ledoc.repository.EquipmentCategoryRepository;
import dk.ledocsystem.ledoc.repository.EquipmentRepository;
import dk.ledocsystem.ledoc.service.CustomerService;
import dk.ledocsystem.ledoc.service.EmployeeService;
import dk.ledocsystem.ledoc.service.EquipmentService;
import dk.ledocsystem.ledoc.service.LocationService;
import dk.ledocsystem.ledoc.util.BeanCopyUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
class EquipmentServiceImpl implements EquipmentService {

    private static final Function<Long, Predicate> CUSTOMER_EQUALS_TO =
            (customerId) -> ExpressionUtils.eqConst(QEquipment.equipment.customer.id, customerId);

    private final EquipmentRepository equipmentRepository;
    private final EquipmentCategoryRepository equipmentCategoryRepository;
    private final AuthenticationTypeRepository authenticationTypeRepository;
    private final CustomerService customerService;
    private final EmployeeService employeeService;
    private final LocationService locationService;
    private final EmailNotificationRepository emailNotificationRepository;

    @Override
    public List<Equipment> getAll() {
        return getAll(Pageable.unpaged()).getContent();
    }

    @Override
    public Page<Equipment> getAll(@NonNull Pageable pageable) {
        return getAll(null, pageable);
    }

    @Override
    public List<Equipment> getAll(@NonNull Predicate predicate) {
        return getAll(predicate, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<Equipment> getAll(Predicate predicate, @NonNull Pageable pageable) {
        Long currentCustomerId = customerService.getCurrentCustomerReference().getId();
        Predicate combinePredicate = ExpressionUtils.and(predicate, CUSTOMER_EQUALS_TO.apply(currentCustomerId));
        return equipmentRepository.findAll(combinePredicate, pageable);
    }

    @Override
    public Optional<Equipment> getById(@NonNull Long id) {
        return equipmentRepository.findById(id);
    }

    @Override
    @Transactional
    public Equipment createEquipment(@NonNull EquipmentCreateDTO equipmentCreateDTO) {
        Equipment equipment = new Equipment();
        BeanCopyUtils.copyProperties(equipmentCreateDTO, equipment, false);

        Employee creator = employeeService.getCurrentUserReference();
        Employee responsible = resolveResponsible(equipmentCreateDTO.getResponsibleId());

        equipment.setCreator(creator);
        equipment.setResponsible(responsible);
        equipment.setCustomer(customerService.getCurrentCustomerReference());
        equipment.setCategory(resolveCategory(equipmentCreateDTO.getCategoryId()));
        equipment.setLocation(resolveLocation(equipmentCreateDTO.getLocationId()));
        equipment.setAuthenticationType(resolveAuthenticationType(equipmentCreateDTO.getAuthTypeId()));

        sendMessages(creator);
        sendMessages(responsible);

        return equipmentRepository.save(equipment);
    }

    @Override
    @Transactional
    public Equipment updateEquipment(@NonNull Long equipmentId, @NonNull EquipmentEditDTO equipmentEditDTO) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundException("equipment.id.not.found", equipmentId.toString()));
        BeanCopyUtils.copyProperties(equipmentEditDTO, equipment, false);

        Long categoryId = equipmentEditDTO.getCategoryId();
        if (categoryId != null) {
            equipment.setCategory(resolveCategory(categoryId));
        }

        Long locationId = equipmentEditDTO.getLocationId();
        if (locationId != null) {
            equipment.setLocation(resolveLocation(locationId));
        }

        Long responsibleId = equipmentEditDTO.getResponsibleId();
        if (responsibleId != null) {
            Employee responsible = resolveResponsible(responsibleId);
            equipment.setResponsible(responsible);
            sendMessages(responsible);
        }

        Long authTypeId = equipmentEditDTO.getAuthTypeId();
        if (authTypeId != null) {
            equipment.setAuthenticationType(resolveAuthenticationType(authTypeId));
        }

        ApprovalType approvalType = equipmentEditDTO.getApprovalType();
        if (approvalType != null && approvalType == ApprovalType.NO_NEED) {
            equipment.eraseReviewDetails();
        }

        return equipmentRepository.save(equipment);
    }

    @Override
    public Page<Equipment> getNewEquipment(@NonNull Pageable pageable) {
        return getNewEquipment(pageable, null);
    }

    @Override
    public Page<Equipment> getNewEquipment(@NonNull Pageable pageable, Predicate predicate) {
        Employee currentUser = employeeService.getCurrentUserReference();

        Predicate newEquipmentPredicate = ExpressionUtils.and(
                predicate,
                ExpressionUtils.notIn(Expressions.constant(currentUser), QEquipment.equipment.visitedBy));
        return getAll(newEquipmentPredicate, pageable);
    }

    @Override
    public List<Equipment> getAllForReview() {
        return equipmentRepository.findAllByArchivedFalseAndNextReviewDateNotNull();
    }

    @Override
    @Transactional
    public void loanEquipment(Long equipmentId, EquipmentLoanDTO equipmentLoanDTO) {
        EquipmentLoan equipmentLoan = new EquipmentLoan();
        BeanCopyUtils.copyProperties(equipmentLoanDTO, equipmentLoan);

        equipmentLoan.setBorrower(resolveBorrower(equipmentLoanDTO.getBorrowerId()));
        equipmentLoan.setLocation(resolveLocation(equipmentLoanDTO.getLocationId()));

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundException("equipment.id.not.found", equipmentId.toString()));
        equipmentLoan.setEquipment(equipment);
        equipment.setLoan(equipmentLoan);
    }

    @Override
    @Transactional
    public void returnLoanedEquipment(Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundException("equipment.id.not.found", equipmentId.toString()));
        equipment.setLoan(null);
    }

    private void sendMessages(Employee employee) {
        EmailNotification notification = new EmailNotification(employee.getUsername(), "equipment_created");
        emailNotificationRepository.save(notification);
    }

    private EquipmentCategory resolveCategory(Long categoryId) {
        return equipmentCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("equipment.category.not.found", categoryId.toString()));
    }

    private Location resolveLocation(Long locationId) {
        return locationService.getById(locationId)
                .orElseThrow(() -> new NotFoundException("location.id.not.found", locationId.toString()));
    }

    private Employee resolveResponsible(Long responsibleId) {
        return employeeService.getById(responsibleId)
                .orElseThrow(() -> new NotFoundException("employee.responsible.not.found", responsibleId.toString()));
    }

    private AuthenticationType resolveAuthenticationType(Long authTypeId) {
        return (authTypeId == null) ? null :
                authenticationTypeRepository.findById(authTypeId)
                        .orElseThrow(() -> new NotFoundException("equipment.authentication.type.not.found", authTypeId.toString()));
    }

    private Employee resolveBorrower(Long borrowerId) {
        return employeeService.getById(borrowerId)
                .orElseThrow(() -> new NotFoundException("equipment.borrower.not.found", borrowerId.toString()));
    }

    @Override
    public List<IdAndLocalizedName> getAuthTypes() {
        return authenticationTypeRepository.getAllBy();
    }

    @Override
    public AuthenticationType createAuthType(AuthenticationTypeDTO authenticationTypeDTO) {
        AuthenticationType authenticationType = new AuthenticationType();
        BeanCopyUtils.copyProperties(authenticationTypeDTO, authenticationType);

        return authenticationTypeRepository.save(authenticationType);
    }

    @Override
    public EquipmentCategory createNewCategory(EquipmentCategoryCreateDTO categoryCreateDTO) {
        EquipmentCategory category = new EquipmentCategory();
        BeanCopyUtils.copyProperties(categoryCreateDTO, category);

        return equipmentCategoryRepository.save(category);
    }

    @Override
    public void deleteById(@NonNull Long id) {
        equipmentRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void deleteByIds(@NonNull Collection<Long> employeeIds) {
        equipmentRepository.deleteByIdIn(employeeIds);
    }
}
