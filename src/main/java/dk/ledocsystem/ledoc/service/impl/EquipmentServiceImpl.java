package dk.ledocsystem.ledoc.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import dk.ledocsystem.ledoc.dto.ArchivedStatusDTO;
import dk.ledocsystem.ledoc.dto.equipment.*;
import dk.ledocsystem.ledoc.dto.projections.IdAndLocalizedName;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.Location;
import dk.ledocsystem.ledoc.model.email_notifications.EmailNotification;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.model.equipment.*;
import dk.ledocsystem.ledoc.model.review.ReviewTemplate;
import dk.ledocsystem.ledoc.repository.AuthenticationTypeRepository;
import dk.ledocsystem.ledoc.repository.EmailNotificationRepository;
import dk.ledocsystem.ledoc.repository.EquipmentCategoryRepository;
import dk.ledocsystem.ledoc.repository.EquipmentRepository;
import dk.ledocsystem.ledoc.service.EmployeeService;
import dk.ledocsystem.ledoc.service.EquipmentService;
import dk.ledocsystem.ledoc.service.LocationService;
import dk.ledocsystem.ledoc.service.ReviewTemplateService;
import dk.ledocsystem.ledoc.service.dto.EquipmentPreviewDTO;
import dk.ledocsystem.ledoc.service.dto.GetEquipmentDTO;
import dk.ledocsystem.ledoc.validator.BaseValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.*;

@Service
@RequiredArgsConstructor
class EquipmentServiceImpl implements EquipmentService {

    private static final Function<Long, Predicate> CUSTOMER_EQUALS_TO =
            customerId -> ExpressionUtils.eqConst(QEquipment.equipment.customer.id, customerId);

    private final ModelMapper modelMapper;
    private final EquipmentRepository equipmentRepository;
    private final EquipmentCategoryRepository equipmentCategoryRepository;
    private final AuthenticationTypeRepository authenticationTypeRepository;
    private final EmployeeService employeeService;
    private final LocationService locationService;
    private final ReviewTemplateService reviewTemplateService;
    private final EmailNotificationRepository emailNotificationRepository;

    private final BaseValidator<EquipmentDTO> equipmentDtoValidator;
    private final BaseValidator<EquipmentLoanDTO> equipmentLoanDtoValidator;
    private final BaseValidator<AuthenticationTypeDTO> authenticationTypeDtoValidator;
    private final BaseValidator<EquipmentCategoryCreateDTO> equipmentCategoryCreateDtoValidator;

    @Override
    @Transactional
    public Equipment createEquipment(@NonNull EquipmentDTO equipmentDTO, Customer customer) {
        equipmentDtoValidator.validate(equipmentDTO);

        Equipment equipment = modelMapper.map(equipmentDTO, Equipment.class);
        Employee creator = employeeService.getCurrentUserReference();
        Employee responsible = resolveResponsible(equipmentDTO.getResponsibleId());

        equipment.setCreator(creator);
        equipment.setResponsible(responsible);
        equipment.setCustomer(customer);
        equipment.setCategory(resolveCategory(equipmentDTO.getCategoryId()));
        equipment.setLocation(resolveLocation(equipmentDTO.getLocationId()));
        equipment.setReviewTemplate(resolveReviewTemplate(equipmentDTO.getReviewTemplateId()));
        equipment.setAuthenticationType(resolveAuthenticationType(equipmentDTO.getAuthTypeId()));

        sendMessages(creator);
        sendMessages(responsible);

        return equipmentRepository.save(equipment);
    }

    @Override
    @Transactional
    public Equipment updateEquipment(@NonNull EquipmentDTO equipmentDTO) {
        equipmentDtoValidator.validate(equipmentDTO);

        Equipment equipment = equipmentRepository.findById(equipmentDTO.getId())
                .orElseThrow(() -> new NotFoundException(EQUIPMENT_ID_NOT_FOUND, equipmentDTO.getId().toString()));
        modelMapper.map(equipmentDTO, equipment);

        equipment.setCategory(resolveCategory(equipmentDTO.getCategoryId()));
        equipment.setLocation(resolveLocation(equipmentDTO.getLocationId()));
        equipment.setReviewTemplate(resolveReviewTemplate(equipmentDTO.getReviewTemplateId()));
        equipment.setAuthenticationType(resolveAuthenticationType(equipmentDTO.getAuthTypeId()));

        Long responsibleId = equipmentDTO.getResponsibleId();
        if (!responsibleId.equals(equipment.getResponsible().getId())) {
            Employee responsible = resolveResponsible(responsibleId);
            equipment.setResponsible(responsible);
            sendMessages(responsible);
        }

        ApprovalType approvalType = equipmentDTO.getApprovalType();
        if (approvalType == ApprovalType.NO_NEED) {
            equipment.eraseReviewDetails();
        }
        return equipmentRepository.save(equipment);
    }

    @Override
    @Transactional
    public void changeArchivedStatus(@NonNull Long equipmentId, @NonNull ArchivedStatusDTO archivedStatusDTO) {
        Equipment equipment = getById(equipmentId)
                .orElseThrow(() -> new NotFoundException("equipment.id.not.found", equipmentId.toString()));

        equipment.setArchived(archivedStatusDTO.isArchived());
        equipment.setArchiveReason(archivedStatusDTO.getArchiveReason());
        equipmentRepository.save(equipment);
    }

    @Override
    public Page<Equipment> getNewEquipment(@NonNull Long userId, @NonNull Pageable pageable) {
        return getNewEquipment(userId, pageable, null);
    }

    @Override
    public Page<Equipment> getNewEquipment(@NonNull Long userId, @NonNull Pageable pageable, Predicate predicate) {
        Employee employee = employeeService.getById(userId)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, userId.toString()));
        Long customerId = employee.getCustomer().getId();

        Predicate newEquipmentPredicate = ExpressionUtils.allOf(
                predicate,
                QEquipment.equipment.archived.eq(Boolean.FALSE),
                ExpressionUtils.notIn(Expressions.constant(employee), QEquipment.equipment.visitedBy));
        return getAllByCustomer(customerId, newEquipmentPredicate, pageable);
    }

    @Override
    public List<Equipment> getAllForReview() {
        return equipmentRepository.findAllByArchivedFalseAndNextReviewDateNotNull();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GetEquipmentDTO> getEquipmentDtoById(Long equipmentId) {
        return getById(equipmentId).map(this::mapModelToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EquipmentPreviewDTO> getPreviewDtoById(Long equipmentId) {
        return getById(equipmentId).map(this::mapModelToPreviewDto);
    }

    @Override
    @Transactional
    public void loanEquipment(Long equipmentId, EquipmentLoanDTO equipmentLoanDTO) {
        equipmentLoanDtoValidator.validate(equipmentLoanDTO);

        EquipmentLoan equipmentLoan = modelMapper.map(equipmentLoanDTO, EquipmentLoan.class);
        equipmentLoan.setBorrower(resolveBorrower(equipmentLoanDTO.getBorrowerId()));
        equipmentLoan.setLocation(resolveLocation(equipmentLoanDTO.getLocationId()));

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundException(EQUIPMENT_ID_NOT_FOUND, equipmentId.toString()));
        equipment.setLoan(equipmentLoan);
    }

    @Override
    @Transactional
    public void returnLoanedEquipment(Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundException(EQUIPMENT_ID_NOT_FOUND, equipmentId.toString()));
        equipment.removeLoan();
    }

    private void sendMessages(Employee employee) {
        EmailNotification notification = new EmailNotification(employee.getUsername(), "equipment_created");
        emailNotificationRepository.save(notification);
    }

    private EquipmentCategory resolveCategory(Long categoryId) {
        return equipmentCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(EQUIPMENT_CATEGORY_NOT_FOUND, categoryId.toString()));
    }

    private Location resolveLocation(Long locationId) {
        return locationService.getById(locationId)
                .orElseThrow(() -> new NotFoundException(LOCATION_ID_NOT_FOUND, locationId.toString()));
    }

    private ReviewTemplate resolveReviewTemplate(Long reviewTemplateId) {
        return (reviewTemplateId == null) ? null :
                reviewTemplateService.getById(reviewTemplateId)
                        .orElseThrow(() -> new NotFoundException(REVIEW_TEMPLATE_ID_NOT_FOUND, reviewTemplateId.toString()));
    }

    private Employee resolveResponsible(Long responsibleId) {
        return employeeService.getById(responsibleId)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_RESPONSIBLE_NOT_FOUND, responsibleId.toString()));
    }

    private AuthenticationType resolveAuthenticationType(Long authTypeId) {
        return (authTypeId == null) ? null :
                authenticationTypeRepository.findById(authTypeId)
                        .orElseThrow(() -> new NotFoundException(EQUIPMENT_AUTHENTICATION_TYPE_NOT_FOUND, authTypeId.toString()));
    }

    private Employee resolveBorrower(Long borrowerId) {
        return employeeService.getById(borrowerId)
                .orElseThrow(() -> new NotFoundException(EQUIPMENT_BORROWER_NOT_FOUND, borrowerId.toString()));
    }

    @Override
    public List<IdAndLocalizedName> getAuthTypes() {
        return authenticationTypeRepository.getAllBy();
    }

    @Override
    public Page<IdAndLocalizedName> getAuthTypes(Pageable pageable) {
        return authenticationTypeRepository.getAllBy(pageable);
    }

    @Override
    public AuthenticationType createAuthType(AuthenticationTypeDTO authenticationTypeDTO) {
        authenticationTypeDtoValidator.validate(authenticationTypeDTO);

        AuthenticationType authenticationType = modelMapper.map(authenticationTypeDTO, AuthenticationType.class);
        return authenticationTypeRepository.save(authenticationType);
    }

    @Override
    public Page<IdAndLocalizedName> getCategories(Pageable pageable) {
        return equipmentCategoryRepository.getAllBy(pageable);
    }

    @Override
    public List<IdAndLocalizedName> getCategories() {
        return equipmentCategoryRepository.getAllBy();
    }

    @Override
    public EquipmentCategory createNewCategory(EquipmentCategoryCreateDTO categoryCreateDTO) {
        equipmentCategoryCreateDtoValidator.validate(categoryCreateDTO);

        EquipmentCategory category = modelMapper.map(categoryCreateDTO, EquipmentCategory.class);
        return equipmentCategoryRepository.save(category);
    }

    //todo It'd be better to replace this with appropriate ModelMapper configuration
    //#see ModelMapper.addMappings()
    private GetEquipmentDTO mapModelToDto(Equipment equipment) {
        GetEquipmentDTO dto = modelMapper.map(equipment, GetEquipmentDTO.class);

        dto.setResponsibleId(equipment.getResponsible().getId());
        dto.setLocationId(equipment.getLocation().getId());
        dto.setCategoryId(equipment.getCategory().getId());

        if (equipment.getAuthenticationType() != null) {
            dto.setAuthTypeId(equipment.getAuthenticationType().getId());
        }

        if (equipment.getReviewTemplate() != null) {
            dto.setReviewTemplateId(equipment.getReviewTemplate().getId());
        }

        return dto;
    }

    private EquipmentPreviewDTO mapModelToPreviewDto(Equipment equipment) {
        EquipmentPreviewDTO dto = modelMapper.map(equipment, EquipmentPreviewDTO.class);

        dto.setResponsibleName(equipment.getResponsible().getName());
        dto.setLocationName(equipment.getLocation().getName());
        dto.setCategoryName(equipment.getCategory().getNameEn());

        if (equipment.getAuthenticationType() != null) {
            dto.setAuthTypeName(equipment.getAuthenticationType().getNameEn());
        }

        if (equipment.getReviewTemplate() != null) {
            dto.setReviewTemplateName(equipment.getReviewTemplate().getName());
        }

        return dto;
    }

    //region GET/DELETE standard API

    @Override
    public List<Equipment> getAll() {
        return equipmentRepository.findAll();
    }

    @Override
    public Page<Equipment> getAll(@NonNull Pageable pageable) {
        return equipmentRepository.findAll(pageable);
    }

    @Override
    public List<Equipment> getAll(@NonNull Predicate predicate) {
        return IterableUtils.toList(equipmentRepository.findAll(predicate));
    }

    @Override
    public Page<Equipment> getAll(@NonNull Predicate predicate, @NonNull Pageable pageable) {
        return equipmentRepository.findAll(predicate, pageable);
    }

    @Override
    public List<Equipment> getAllByCustomer(@NonNull Long customerId) {
        return getAllByCustomer(customerId, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<Equipment> getAllByCustomer(@NonNull Long customerId, @NonNull Pageable pageable) {
        return getAllByCustomer(customerId, null, pageable);
    }

    @Override
    public List<Equipment> getAllByCustomer(@NonNull Long customerId, Predicate predicate) {
        return getAllByCustomer(customerId, predicate, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<Equipment> getAllByCustomer(@NonNull Long customerId, Predicate predicate, @NonNull Pageable pageable) {
        Predicate combinePredicate = ExpressionUtils.and(predicate, CUSTOMER_EQUALS_TO.apply(customerId));
        return equipmentRepository.findAll(combinePredicate, pageable);
    }

    @Override
    public Optional<Equipment> getById(@NonNull Long id) {
        return equipmentRepository.findById(id);
    }

    @Override
    public List<Equipment> getAllById(@NonNull Iterable<Long> ids) {
        return equipmentRepository.findAllById(ids);
    }

    @Override
    public void deleteById(@NonNull Long id) {
        equipmentRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void deleteByIds(@NonNull Iterable<Long> equipmentIds) {
        equipmentRepository.deleteByIdIn(equipmentIds);
    }

    //endregion
}
