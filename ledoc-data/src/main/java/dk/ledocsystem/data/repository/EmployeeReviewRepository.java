package dk.ledocsystem.data.repository;

import dk.ledocsystem.data.model.review.EmployeeReview;
import org.springframework.data.repository.CrudRepository;

public interface EmployeeReviewRepository extends CrudRepository<EmployeeReview, Long> {
}
