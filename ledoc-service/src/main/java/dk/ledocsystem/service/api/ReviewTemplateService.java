package dk.ledocsystem.service.api;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.review.ReviewTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewTemplateService extends CustomerBasedDomainService<ReviewTemplate> {

    Page<ReviewTemplate> getAllGlobal(Predicate predicate, Pageable pageable);

    List<ReviewTemplate> getAllByModule(Long customerId, String module);
}
