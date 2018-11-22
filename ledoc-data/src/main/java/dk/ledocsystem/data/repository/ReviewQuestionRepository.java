package dk.ledocsystem.data.repository;

import dk.ledocsystem.data.model.review.ReviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ReviewQuestionRepository extends JpaRepository<ReviewQuestion, Long>,
        QuerydslPredicateExecutor<ReviewQuestion> {

    /**
     * Deletes review questions with the given IDs.
     *
     * @param ids The collection of review questions IDs.
     */
    @Modifying
    @Query("delete from ReviewQuestion rq where rq.id in ?1")
    void deleteByIdIn(Iterable<Long> ids);
}
