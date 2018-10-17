package dk.ledocsystem.ledoc.repository;

import dk.ledocsystem.ledoc.model.review.QuestionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface QuestionGroupRepository extends JpaRepository<QuestionGroup, Long>,
        QuerydslPredicateExecutor<QuestionGroup> {

    /**
     * Deletes question groups with the given IDs.
     *
     * @param ids The collection of question group IDs.
     */
    @Modifying
    @Query("delete from QuestionGroup qg where qg.id in ?1")
    void deleteByIdIn(Iterable<Long> ids);
}
