package dk.ledocsystem.service.api;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.review.ReviewTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReviewTemplateService extends CustomerBasedDomainService<ReviewTemplate> {
    String EMPLOYEE_QUICK_REVIEW_TEMPLATE_NAME = "Quick review for employees";
    String EQUIPMENT_QUICK_REVIEW_TEMPLATE_NAME = "Quick review for equipment";
    String SUPPLIER_QUICK_REVIEW_TEMPLATE_NAME = "Quick review for suppliers";
    String DOCUMENT_QUICK_REVIEW_TEMPLATE_NAME = "Quick review for documents";

    Optional<ReviewTemplate> getByName(String name);

    Page<ReviewTemplate> getAllGlobal(Predicate predicate, Pageable pageable);

    List<ReviewTemplate> getAllByModule(Long customerId, String module);
}
