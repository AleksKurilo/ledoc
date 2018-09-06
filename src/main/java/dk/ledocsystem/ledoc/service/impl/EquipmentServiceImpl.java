package dk.ledocsystem.ledoc.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import dk.ledocsystem.ledoc.dto.equipment.AuthenticationTypeDTO;
import dk.ledocsystem.ledoc.dto.equipment.EquipmentCategoryCreateDTO;
import dk.ledocsystem.ledoc.dto.equipment.EquipmentCreateDTO;
import dk.ledocsystem.ledoc.dto.equipment.EquipmentEditDTO;
import dk.ledocsystem.ledoc.dto.projections.IdAndLocalizedName;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.AuthenticationType;
import dk.ledocsystem.ledoc.model.equipment.Equipment;
import dk.ledocsystem.ledoc.model.equipment.EquipmentCategory;
import dk.ledocsystem.ledoc.model.Location;
import dk.ledocsystem.ledoc.model.equipment.QEquipment;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.model.equipment.ReviewFrequency;
import dk.ledocsystem.ledoc.repository.AuthenticationTypeRepository;
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
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
class EquipmentServiceImpl implements EquipmentService {

    private static final Function<Long, Predicate> CUSTOMER_EQUALS_TO =
            (customerId) -> ExpressionUtils.eqConst(QEquipment.equipment.customer.id, customerId);
    private static final Predicate ARCHIVED_FALSE = ExpressionUtils.eqConst(QEquipment.equipment.archived, false);

    private final EquipmentRepository equipmentRepository;
    private final EquipmentCategoryRepository equipmentCategoryRepository;
    private final AuthenticationTypeRepository authenticationTypeRepository;
    private final CustomerService customerService;
    private final EmployeeService employeeService;
    private final LocationService locationService;

    @Override
    public List<Equipment> getAll() {
        return getAll(Pageable.unpaged()).getContent();
    }

    @Override
    public Page<Equipment> getAll(@NonNull Pageable pageable) {
        return getAll(ARCHIVED_FALSE, pageable);
    }

    @Override
    public List<Equipment> getAll(@NonNull Predicate predicate) {
        return getAll(predicate, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<Equipment> getAll(@NonNull Predicate predicate, @NonNull Pageable pageable) {
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
        BeanCopyUtils.copyProperties(equipmentCreateDTO, equipment);

        equipment.setCreator(employeeService.getCurrentUserReference());
        equipment.setCustomer(customerService.getCurrentCustomerReference());
        equipment.setCategory(resolveCategory(equipmentCreateDTO.getCategoryId()));
        equipment.setLocation(resolveLocation(equipmentCreateDTO.getLocationId()));
        equipment.setResponsible(resolveResponsible(equipmentCreateDTO.getResponsibleId()));
        equipment.setAuthenticationType(resolveAuthenticationType(equipmentCreateDTO.getAuthTypeId()));

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
            equipment.setResponsible(resolveResponsible(responsibleId));
        }

        Long authTypeId = equipmentEditDTO.getAuthTypeId();
        if (authTypeId != null) {
            equipment.setAuthenticationType(resolveAuthenticationType(authTypeId));
        }

        return equipmentRepository.save(equipment);
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

    @Override
    public Page<Equipment> getNewEquipment(@NonNull Pageable pageable) {
        return getNewEquipment(pageable, ARCHIVED_FALSE);
    }

    @Override
    public Page<Equipment> getNewEquipment(@NonNull Pageable pageable, @NotNull Predicate predicate) {
        Employee currentUser = employeeService.getCurrentUserReference();

        Predicate newEquipmentPredicate = ExpressionUtils.and(
                predicate,
                ExpressionUtils.notIn(Expressions.constant(currentUser), QEquipment.equipment.visitedBy));
        return getAll(newEquipmentPredicate, pageable);
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
        category.setNameEn(categoryCreateDTO.getNameEn());
        category.setNameDa(categoryCreateDTO.getNameDa());
        category.setReviewFrequency(ReviewFrequency.fromString(categoryCreateDTO.getReviewFrequency()));

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
