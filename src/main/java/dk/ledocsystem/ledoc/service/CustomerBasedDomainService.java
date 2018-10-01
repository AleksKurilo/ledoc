package dk.ledocsystem.ledoc.service;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Extended {@link DomainService} that declares additional API for {@link Customer Customer-dependent} domain objects.
 *
 * @param <T> Customer-dependent domain object type
 */
public interface CustomerBasedDomainService<T> extends DomainService<T> {

    List<T> getAllByCustomer(Long customerId);

    Page<T> getAllByCustomer(Long customerId, Pageable pageable);

    List<T> getAllByCustomer(Long customerId, Predicate predicate);

    Page<T> getAllByCustomer(Long customerId, Predicate predicate, Pageable pageable);
}
