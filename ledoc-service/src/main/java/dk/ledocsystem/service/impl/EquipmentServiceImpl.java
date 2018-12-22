package dk.ledocsystem.service.impl;

import com.google.common.collect.ImmutableMap;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import dk.ledocsystem.data.model.Customer;
import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.equipment.*;
import dk.ledocsystem.data.model.review.ReviewTemplate;
import dk.ledocsystem.service.api.dto.outbound.IdAndLocalizedName;
import dk.ledocsystem.data.repository.*;
import dk.ledocsystem.service.api.EquipmentService;
import dk.ledocsystem.service.api.ReviewTemplateService;
import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.equipment.*;
import dk.ledocsystem.service.api.dto.outbound.equipment.EquipmentExportDTO;
import dk.ledocsystem.service.api.dto.outbound.equipment.EquipmentEditDto;
import dk.ledocsystem.service.api.dto.outbound.equipment.EquipmentPreviewDTO;
import dk.ledocsystem.service.api.dto.outbound.equipment.GetEquipmentDTO;
import dk.ledocsystem.service.api.dto.outbound.equipment.GetFollowedEquipmentDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.service.impl.events.producer.EquipmentProducer;
import dk.ledocsystem.service.impl.property_maps.equipment.*;
import dk.ledocsystem.service.impl.validators.BaseValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.*;

@Service
@RequiredArgsConstructor
class EquipmentServiceImpl implements EquipmentService {

    private static final Function<Long, Predicate> CUSTOMER_EQUALS_TO =
            customerId -> ExpressionUtils.eqConst(QEquipment.equipment.customer.id, customerId);

    private final ModelMapper modelMapper;
    private final EquipmentRepository equipmentRepository;
    private final EquipmentCategoryRepository equipmentCategoryRepository;
    private final AuthenticationTypeRepository authenticationTypeRepository;
    private final EmployeeRepository employeeRepository;
    private final LocationRepository locationRepository;
    private final ReviewTemplateService reviewTemplateService;
    private final EquipmentProducer equipmentProducer;

    private final BaseValidator<EquipmentDTO> equipmentDtoValidator;
    private final BaseValidator<EquipmentLoanDTO> equipmentLoanDtoValidator;
    private final BaseValidator<AuthenticationTypeDTO> authenticationTypeDtoValidator;
    private final BaseValidator<EquipmentCategoryCreateDTO> equipmentCategoryCreateDtoValidator;

    @PostConstruct
    private void init() {
        modelMapper.addMappings(new EquipmentToGetEquipmentDtoPropertyMap());
        modelMapper.addMappings(new EquipmentToEditDtoPropertyMap());
        modelMapper.addMappings(new EquipmentToPreviewDtoPropertyMap());
        modelMapper.addMappings(new FollowedEquipmentToGetFollowedEquipmentDtoMap());
        modelMapper.addMappings(new EquipmentToExportDtoMap());
    }

    @Override
    @Transactional
    public GetEquipmentDTO createEquipment(@NonNull EquipmentDTO equipmentDTO, UserDetails creatorDetails) {
        Employee creator = employeeRepository.findByUsername(creatorDetails.getUsername()).orElseThrow(IllegalStateException::new);
        Customer customer = creator.getCustomer();
        equipmentDtoValidator.validate(equipmentDTO, ImmutableMap.of("customerId", customer.getId()), equipmentDTO.getValidationGroups());

        Equipment equipment = modelMapper.map(equipmentDTO, Equipment.class);
        Employee responsible = resolveResponsible(equipmentDTO.getResponsibleId());

        equipment.setCreator(creator);
        equipment.setResponsible(responsible);
        equipment.setCustomer(customer);
        equipment.setCategory(resolveCategory(equipmentDTO.getCategoryId()));
        equipment.setLocation(resolveLocation(equipmentDTO.getLocationId()));
        equipment.setReviewTemplate(resolveReviewTemplate(equipmentDTO.getReviewTemplateId()));
        equipment.setAuthenticationType(resolveAuthenticationType(equipmentDTO.getAuthTypeId()));

        equipmentProducer.create(equipment, creator);

        return mapToDto(equipmentRepository.save(equipment));
    }

    @Override
    @Transactional
    public GetEquipmentDTO updateEquipment(@NonNull EquipmentDTO equipmentDTO, UserDetails creatorDetails) {
        Employee creator = employeeRepository.findByUsername(creatorDetails.getUsername()).orElseThrow(IllegalStateException::new);
        Customer customer = creator.getCustomer();
        equipmentDtoValidator.validate(equipmentDTO, ImmutableMap.of("customerId", customer.getId()), equipmentDTO.getValidationGroups());

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
            equipmentProducer.edit(equipment, creator);
        }

        ApprovalType approvalType = equipmentDTO.getApprovalType();
        if (approvalType == ApprovalType.NO_NEED) {
            equipment.eraseReviewDetails();
        }
        return mapToDto(equipmentRepository.save(equipment));
    }

    @Override
    @Transactional
    public void changeArchivedStatus(@NonNull Long equipmentId, @NonNull ArchivedStatusDTO archivedStatusDTO, UserDetails creatorDetails) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundException("equipment.id.not.found", equipmentId.toString()));

        Employee creator = employeeRepository.findByUsername(creatorDetails.getUsername()).orElseThrow(IllegalStateException::new);

        equipment.setArchived(archivedStatusDTO.isArchived());
        equipment.setArchiveReason(archivedStatusDTO.getArchiveReason());
        equipmentRepository.save(equipment);
        if (archivedStatusDTO.isArchived()) {
            equipmentProducer.archive(equipment, creator);
        } else {
            equipmentProducer.unarchive(equipment, creator);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetEquipmentDTO> getNewEquipment(@NonNull UserDetails user, @NonNull Pageable pageable) {
        return getNewEquipment(user, pageable, null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetEquipmentDTO> getNewEquipment(@NonNull UserDetails user, @NonNull Pageable pageable, Predicate predicate) {
        Employee employee = employeeRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_USERNAME_NOT_FOUND, user.getUsername()));
        Long customerId = employee.getCustomer().getId();

        Predicate newEquipmentPredicate = ExpressionUtils.allOf(
                predicate,
                QEquipment.equipment.archived.eq(Boolean.FALSE),
                ExpressionUtils.notIn(Expressions.constant(employee), QEquipment.equipment.visitedBy));
        return getAllByCustomer(customerId, newEquipmentPredicate, pageable);
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

    private EquipmentCategory resolveCategory(Long categoryId) {
        return equipmentCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(EQUIPMENT_CATEGORY_NOT_FOUND, categoryId.toString()));
    }

    private Location resolveLocation(Long locationId) {
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new NotFoundException(LOCATION_ID_NOT_FOUND, locationId.toString()));
    }

    private ReviewTemplate resolveReviewTemplate(Long reviewTemplateId) {
        return (reviewTemplateId == null) ? null :
                reviewTemplateService.getById(reviewTemplateId)
                        .orElseThrow(() -> new NotFoundException(REVIEW_TEMPLATE_ID_NOT_FOUND, reviewTemplateId.toString()));
    }

    private Employee resolveResponsible(Long responsibleId) {
        return employeeRepository.findById(responsibleId)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_RESPONSIBLE_NOT_FOUND, responsibleId.toString()));
    }

    private AuthenticationType resolveAuthenticationType(Long authTypeId) {
        return (authTypeId == null) ? null :
                authenticationTypeRepository.findById(authTypeId)
                        .orElseThrow(() -> new NotFoundException(EQUIPMENT_AUTHENTICATION_TYPE_NOT_FOUND, authTypeId.toString()));
    }

    private Employee resolveBorrower(Long borrowerId) {
        return employeeRepository.findById(borrowerId)
                .orElseThrow(() -> new NotFoundException(EQUIPMENT_BORROWER_NOT_FOUND, borrowerId.toString()));
    }

    @Override
    public List<IdAndLocalizedName> getAuthTypes() {
        return authenticationTypeRepository.findAll().stream().map(this::mapAuthTypeToDto).collect(Collectors.toList());
    }

    @Override
    public IdAndLocalizedName createAuthType(AuthenticationTypeDTO authenticationTypeDTO) {
        authenticationTypeDtoValidator.validate(authenticationTypeDTO);

        AuthenticationType authenticationType = modelMapper.map(authenticationTypeDTO, AuthenticationType.class);
        return mapAuthTypeToDto(authenticationTypeRepository.save(authenticationType));
    }

    @Override
    public List<IdAndLocalizedName> getCategories() {
        return equipmentCategoryRepository.findAll().stream().map(this::mapCategoryToDto).collect(Collectors.toList());
    }

    @Override
    public IdAndLocalizedName createCategory(EquipmentCategoryCreateDTO categoryCreateDTO) {
        equipmentCategoryCreateDtoValidator.validate(categoryCreateDTO);

        EquipmentCategory category = modelMapper.map(categoryCreateDTO, EquipmentCategory.class);
        return mapCategoryToDto(equipmentCategoryRepository.save(category));
    }

    //region GET/DELETE standard API

    @Override
    @Transactional(readOnly = true)
    public List<GetEquipmentDTO> getAll() {
        return getAll(Pageable.unpaged()).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetEquipmentDTO> getAll(@NonNull Pageable pageable) {
        return equipmentRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetEquipmentDTO> getAll(@NonNull Predicate predicate) {
        return getAll(predicate, Pageable.unpaged()).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetEquipmentDTO> getAll(@NonNull Predicate predicate, @NonNull Pageable pageable) {
        return equipmentRepository.findAll(predicate, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetEquipmentDTO> getAllByCustomer(@NonNull Long customerId) {
        return getAllByCustomer(customerId, Pageable.unpaged()).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetEquipmentDTO> getAllByCustomer(@NonNull Long customerId, @NonNull Pageable pageable) {
        return getAllByCustomer(customerId, null, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetEquipmentDTO> getAllByCustomer(@NonNull Long customerId, Predicate predicate) {
        return getAllByCustomer(customerId, predicate, Pageable.unpaged()).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetEquipmentDTO> getAllByCustomer(@NonNull Long customerId, Predicate predicate, @NonNull Pageable pageable) {
        Predicate combinePredicate = ExpressionUtils.and(predicate, CUSTOMER_EQUALS_TO.apply(customerId));
        return equipmentRepository.findAll(combinePredicate, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<List<String>> getAllForExport(UserDetails creatorDetails, Predicate predicate, boolean isNew) {
        Employee employee = employeeRepository.findByUsername(creatorDetails.getUsername()).orElseThrow(IllegalStateException::new);
        Long customerId = employee.getCustomer().getId();
        Predicate combinePredicate = ExpressionUtils.and(predicate, CUSTOMER_EQUALS_TO.apply(customerId));
        if (isNew) {
            combinePredicate = ExpressionUtils.allOf(combinePredicate,
                    ExpressionUtils.notIn(Expressions.constant(employee), QEquipment.equipment.visitedBy));
        }
        return equipmentRepository.findAll(combinePredicate).stream().map(this::mapToExportDto).map(EquipmentExportDTO::getFields).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EquipmentPreviewDTO> getPreviewDtoById(@NonNull Long equipmentId, boolean isSaveLog, UserDetails creatorDetails) {
        Employee creator = employeeRepository.findByUsername(creatorDetails.getUsername()).orElseThrow(IllegalStateException::new);
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundException(EQUIPMENT_ID_NOT_FOUND, equipmentId.toString()));
        equipmentProducer.read(equipment, creator, isSaveLog);
        return equipmentRepository.findById(equipmentId).map(this::mapToPreviewDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GetEquipmentDTO> getById(@NonNull Long id) {
        return equipmentRepository.findById(id).map(this::mapToEditDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetEquipmentDTO> getAllById(@NonNull Iterable<Long> ids) {
        return equipmentRepository.findAllById(ids).stream().map(this::mapToEditDto).collect(Collectors.toList());
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

    private GetEquipmentDTO mapToDto(Equipment equipment) {
        return modelMapper.map(equipment, GetEquipmentDTO.class);
    }

    private EquipmentEditDto mapToEditDto(Equipment equipment) {
        return modelMapper.map(equipment, EquipmentEditDto.class);
    }

    private EquipmentPreviewDTO mapToPreviewDto(Equipment equipment) {
        return modelMapper.map(equipment, EquipmentPreviewDTO.class);
    }

    private GetFollowedEquipmentDTO mapToFollowDto(FollowedEquipment equipment) {
        return modelMapper.map(equipment, GetFollowedEquipmentDTO.class);
    }

    private EquipmentExportDTO mapToExportDto(Equipment equipment) {
        return modelMapper.map(equipment, EquipmentExportDTO.class);
    }

    private IdAndLocalizedName mapAuthTypeToDto(AuthenticationType authenticationType) {
        return modelMapper.map(authenticationType, IdAndLocalizedName.class);
    }

    private IdAndLocalizedName mapCategoryToDto(EquipmentCategory category) {
        return modelMapper.map(category, IdAndLocalizedName.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetFollowedEquipmentDTO> getFollowedEquipment(Long employeeId, Pageable pageable) {
        return employeeRepository.findAllFollowedEquipmentByEmployeePaged(employeeId, pageable).map(this::mapToFollowDto);
    }

    @Override
    @Transactional
    public void follow(Long equipmentId, UserDetails currentUserDetails, EquipmentFollowDTO equipmentFollowDTO) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundException(EQUIPMENT_ID_NOT_FOUND, equipmentId.toString()));

        Employee follower;
        boolean forced = false;
        if (equipmentFollowDTO.getFollowerId() != null) {
            follower = employeeRepository.findById(equipmentFollowDTO.getFollowerId()).orElseThrow(IllegalStateException::new);
            forced = true;
        } else {
            follower = employeeRepository.findByUsername(currentUserDetails.getUsername()).orElseThrow(IllegalStateException::new);
        }
        if (equipmentFollowDTO.isFollowed()) {
            equipment.addFollower(follower, forced);
        } else {
            equipment.removeFollower(follower);
        }
        equipmentProducer.follow(equipment, follower, forced, equipmentFollowDTO.isFollowed());
    }

    //endregion
}
