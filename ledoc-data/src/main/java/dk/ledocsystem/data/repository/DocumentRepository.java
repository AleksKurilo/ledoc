package dk.ledocsystem.data.repository;


import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.Document;
import dk.ledocsystem.data.model.equipment.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface DocumentRepository extends JpaRepository<Document, Long>, QuerydslPredicateExecutor<Document> {

    List<Document> findAll(Predicate predicate);

    Optional<Document> findById(long id);

    Set<Document> findByEmployeeId(long employeeId);

    Set<Document> findByEquipmentId(long equipmentId);

    /**
     * Deletes documents with the given IDs.
     *
     * @param ids The collection of document IDs.
     */
    @Modifying
    @Query("delete from Document d where d.id in ?1")
    void deleteByIdIn(Iterable<Long> ids);
}
