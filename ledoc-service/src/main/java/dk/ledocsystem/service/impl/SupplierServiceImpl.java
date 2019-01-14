package dk.ledocsystem.service.impl;

import com.google.common.collect.ImmutableMap;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.Customer;
import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.review.ReviewTemplate;
import dk.ledocsystem.data.model.supplier.QSupplier;
import dk.ledocsystem.data.model.supplier.Supplier;
import dk.ledocsystem.data.model.supplier.SupplierCategory;
import dk.ledocsystem.data.repository.EmployeeRepository;
import dk.ledocsystem.data.repository.LocationRepository;
import dk.ledocsystem.data.repository.SupplierCategoryRepository;
import dk.ledocsystem.data.repository.SupplierRepository;
import dk.ledocsystem.service.api.ReviewTemplateService;
import dk.ledocsystem.service.api.SupplierService;
import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.supplier.SupplierDTO;
import dk.ledocsystem.service.api.dto.outbound.supplier.GetSupplierDTO;
import dk.ledocsystem.service.api.dto.outbound.supplier.SupplierPreviewDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.service.impl.property_maps.document.SupplierToSupplierPreviewDtoPropertyMap;
import dk.ledocsystem.service.impl.property_maps.supplier.SupplierToGetSupplierDtoPropertyMap;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.*;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private static final Function<Long, Predicate> CUSTOMER_EQUALS_TO =
            customerId -> ExpressionUtils.eqConst(QSupplier.supplier.customer.id, customerId);
    private static final Function<Boolean, Predicate> SUPPLIER_ARCHIVED =
            archived -> ExpressionUtils.eqConst(QSupplier.supplier.archived, archived);

    private final EmployeeRepository employeeRepository;
    private final SupplierRepository supplierRepository;
    private final LocationRepository locationRepository;
    private final SupplierCategoryRepository supplierCategoryRepository;
    private final ReviewTemplateService reviewTemplateService;
    private final ModelMapper modelMapper;
    private final BaseValidator<SupplierDTO> validator;

    @PostConstruct
    private void init() {
        modelMapper.addMappings(new SupplierToGetSupplierDtoPropertyMap());
        modelMapper.addMappings(new SupplierToSupplierPreviewDtoPropertyMap());
    }

    @Override
    @Transactional
    public GetSupplierDTO create(SupplierDTO supplierDTO, UserDetails currentUser) {
        Employee creator = employeeRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_USERNAME_NOT_FOUND, currentUser.getUsername()));
        Supplier supplier = modelMapper.map(supplierDTO, Supplier.class);
        Customer customer = resolveCustomerByUsername(currentUser.getUsername());
        validator.validate(supplierDTO, ImmutableMap.of("customerId", customer.getId()));

        supplier.setCustomer(customer);
        supplier.setCreator(creator);
        supplier.setCategory(resolveSupplierCategory(supplierDTO.getCategoryId()));
        supplier.setResponsible(resolveResponsible(supplierDTO.getResponsibleId()));
        supplier.setReviewResponsible(resolveResponsible(supplierDTO.getReviewResponsible()));
        supplier.setReviewTemplate(resolveReviewTemplate(supplierDTO.getReviewTemplateId()));
        supplier.setLocations(resolveLocations(supplierDTO.getLocationIds()));

        //TODO supplierProducer.create(supplier, creator)
        return mapToDto(supplierRepository.save(supplier));
    }

    private SupplierCategory resolveSupplierCategory(Long categoryId) {
        return supplierCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(SUPPLIER_CATEGORY_ID_NOT_FOUND, categoryId.toString()));
    }

    private Employee resolveResponsible(Long responsibleId) {
        return employeeRepository.findById(responsibleId)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_RESPONSIBLE_NOT_FOUND, responsibleId.toString()));
    }

    private ReviewTemplate resolveReviewTemplate(Long reviewTemplateId) {
        return (reviewTemplateId == null) ? null :
                reviewTemplateService.getById(reviewTemplateId)
                        .orElseThrow(() -> new NotFoundException(REVIEW_TEMPLATE_ID_NOT_FOUND, reviewTemplateId.toString()));
    }

    private Set<Location> resolveLocations(Set<Long> locationIds) {
        return new HashSet<>(locationRepository.findAllById(locationIds));
    }

    private GetSupplierDTO mapToDto(Supplier supplier) {
        return modelMapper.map(supplier, GetSupplierDTO.class);
    }

    @Override
    @Transactional
    public GetSupplierDTO update(SupplierDTO supplierDTO, UserDetails currentUser) {
        Employee creator = employeeRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_USERNAME_NOT_FOUND, currentUser.getUsername()));
        Customer customer = resolveCustomerByUsername(currentUser.getUsername());
        validator.validate(supplierDTO, ImmutableMap.of("customerId", customer.getId()), supplierDTO.getValidationGroups());

        Supplier supplier = supplierRepository.findById(supplierDTO.getId())
                .orElseThrow(() -> new NotFoundException(SUPPLIER_ID_NOT_FOUND, supplierDTO.getId().toString()));
        modelMapper.map(supplierDTO, supplier);

        supplier.setReviewResponsible(resolveResponsible(supplierDTO.getReviewResponsible()));
        supplier.setCategory(resolveSupplierCategory(supplierDTO.getCategoryId()));
        supplier.setReviewTemplate(resolveReviewTemplate(supplierDTO.getReviewTemplateId()));
        supplier.setLocations(resolveLocations(supplierDTO.getLocationIds()));

        Long responsibleId = supplierDTO.getResponsibleId();
        if (!responsibleId.equals(supplier.getResponsible().getId())) {
            Employee responsible = resolveResponsible(responsibleId);
            supplier.setResponsible(responsible);
            //TODO supplierProducer.edit(supplier, creator);
        }
        return mapToDto(supplierRepository.save(supplier));
    }

    @Override
    @Transactional
    public Optional<SupplierPreviewDTO> getPreviewDtoById(Long id, boolean isSaveLog, UserDetails currentUser) {
        Employee creator = employeeRepository.findByUsername(currentUser.getUsername()).orElseThrow(IllegalStateException::new);
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(SUPPLIER_ID_NOT_FOUND, id.toString()));
        //TODO add supplierProducer.read(supplier, creator, isSaveLog);

        return supplierRepository.findById(id).map(this::mapToPreviewDto);
    }

    @Override
    public void changeArchivedStatus(@NonNull Long id, @NonNull ArchivedStatusDTO archivedStatusDTO, UserDetails currentUser) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(SUPPLIER_ID_NOT_FOUND, id.toString()));

        Employee creator = employeeRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(IllegalStateException::new);

        supplier.setArchived(archivedStatusDTO.isArchived());
        supplier.setArchiveReason(archivedStatusDTO.getArchiveReason());
        supplierRepository.save(supplier);

        //TODO add data to logs equipmentProducer.archive(equipment, creator) or equipmentProducer.unarchive
    }

    private SupplierPreviewDTO mapToPreviewDto(Supplier supplier) {
        return modelMapper.map(supplier, SupplierPreviewDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetSupplierDTO> getAllByCustomer(@NonNull Long customerId) {
        return getAllByCustomer(customerId, Pageable.unpaged()).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetSupplierDTO> getAllByCustomer(@NonNull Long customerId, @NonNull Pageable pageable) {
        return getAllByCustomer(customerId, null, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetSupplierDTO> getAllByCustomer(@NonNull Long customerId, Predicate predicate) {
        return getAllByCustomer(customerId, predicate, Pageable.unpaged()).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetSupplierDTO> getAllByCustomer(@NonNull Long customerId, Predicate predicate, @NonNull Pageable pageable) {
        return getAllByCustomer(customerId, "", predicate, pageable);
    }

    @Override
    public Page<GetSupplierDTO> getAllByCustomer(Long customerId, String searchString, Predicate predicate, Pageable pageable) {
        Predicate combinePredicate = ExpressionUtils.and(predicate, CUSTOMER_EQUALS_TO.apply(customerId));
        return supplierRepository.findAll(combinePredicate, pageable).map(this::mapToDto);
    }

    @Override
    public List<GetSupplierDTO> getAll() {
        return getAll(Pageable.unpaged()).getContent();
    }

    @Override
    public Page<GetSupplierDTO> getAll(Pageable pageable) {
        return supplierRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    public List<GetSupplierDTO> getAll(Predicate predicate) {
        return getAll(predicate, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<GetSupplierDTO> getAll(@NonNull Predicate predicate, @NonNull Pageable pageable) {
        return supplierRepository.findAll(predicate, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GetSupplierDTO> getById(@NonNull Long id) {
        return supplierRepository.findById(id).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetSupplierDTO> getAllById(@NonNull Iterable<Long> ids) {
        return supplierRepository.findAllById(ids).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        supplierRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByIds(Iterable<Long> ids) {
        supplierRepository.deleteByIdIn(ids);
    }

    private Customer resolveCustomerByUsername(String username) {
        return employeeRepository.findByUsername(username)
                .map(Employee::getCustomer)
                .orElseThrow(() -> new NotFoundException(USER_NAME_NOT_FOUND, username));
    }
}
