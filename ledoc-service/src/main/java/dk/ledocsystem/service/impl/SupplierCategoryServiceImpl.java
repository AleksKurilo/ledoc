package dk.ledocsystem.service.impl;

import dk.ledocsystem.data.model.supplier.SupplierCategory;
import dk.ledocsystem.data.repository.SupplierCategoryRepository;
import dk.ledocsystem.service.api.SupplierCategoryService;
import dk.ledocsystem.service.api.dto.inbound.supplier.SupplierCategoryDTO;
import dk.ledocsystem.service.api.dto.outbound.IdAndLocalizedName;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.service.impl.validators.BaseValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.SUPPLIER_CATEGORY_ID_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SupplierCategoryServiceImpl implements SupplierCategoryService {

    private final SupplierCategoryRepository repository;
    private final ModelMapper modelMapper;
    private final BaseValidator<SupplierCategoryDTO> validator;

    @Override
    @Transactional
    public IdAndLocalizedName create(SupplierCategoryDTO categoryDTO) {
        validator.validate(categoryDTO);

        SupplierCategory category = modelMapper.map(categoryDTO, SupplierCategory.class);
        category = repository.save(category);
        return modelMapper.map(category, IdAndLocalizedName.class);
    }

    @Override
    @Transactional
    public IdAndLocalizedName update(long id, SupplierCategoryDTO categoryDTO) {
        validator.validate(categoryDTO);

        SupplierCategory category = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(SUPPLIER_CATEGORY_ID_NOT_FOUND, id));

        modelMapper.map(categoryDTO, category);
        category = repository.save(category);
        return modelMapper.map(category, IdAndLocalizedName.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdAndLocalizedName> getList() {
        return repository.findAll().stream().map(this::mapEntityToDto).collect(Collectors.toList());
    }

    private IdAndLocalizedName mapEntityToDto(SupplierCategory category) {
        return modelMapper.map(category, IdAndLocalizedName.class);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SupplierCategory category = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(SUPPLIER_CATEGORY_ID_NOT_FOUND, id));
        repository.delete(category);
    }
}
