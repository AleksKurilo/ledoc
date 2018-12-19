package dk.ledocsystem.data.repository;

import dk.ledocsystem.data.model.document.DocumentCategory;
import dk.ledocsystem.data.model.document.DocumentCategoryType;
import dk.ledocsystem.data.projections.IdAndLocalizedName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface DocumentCategoryRepository extends JpaRepository<DocumentCategory, Long>, QuerydslPredicateExecutor<DocumentCategory> {

    boolean existsByNameEn(String nameEn);

    boolean existsByNameDa(String nameDa);

    List<IdAndLocalizedName> findAllByType(DocumentCategoryType type);
}
