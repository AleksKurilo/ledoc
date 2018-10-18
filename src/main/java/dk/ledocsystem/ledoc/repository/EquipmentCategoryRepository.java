package dk.ledocsystem.ledoc.repository;

import dk.ledocsystem.ledoc.dto.projections.IdAndLocalizedName;
import dk.ledocsystem.ledoc.model.equipment.EquipmentCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentCategoryRepository extends JpaRepository<EquipmentCategory, Long> {

    boolean existsByNameEn(String nameEn);

    boolean existsByNameDa(String nameDa);

    List<IdAndLocalizedName> getAllBy();

    Page<IdAndLocalizedName> getAllBy(Pageable pageable);
}
