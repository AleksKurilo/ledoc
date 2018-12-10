package dk.ledocsystem.data.repository;

import dk.ledocsystem.data.model.document.DocumentSubcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface DocumentSubcategoryRepository extends JpaRepository<DocumentSubcategory, Long>, QuerydslPredicateExecutor<DocumentSubcategory> {
}
