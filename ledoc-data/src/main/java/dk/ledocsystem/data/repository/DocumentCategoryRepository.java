package dk.ledocsystem.data.repository;

import dk.ledocsystem.data.model.document.DocumentCategory;
import dk.ledocsystem.data.model.document.DocumentCategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Set;

public interface DocumentCategoryRepository extends JpaRepository<DocumentCategory, Long>, QuerydslPredicateExecutor<DocumentCategory> {

    Set<DocumentCategory> findAllByType(DocumentCategoryType type);
}
