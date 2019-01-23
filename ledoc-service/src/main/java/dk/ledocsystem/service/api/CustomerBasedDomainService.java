package dk.ledocsystem.service.api;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * Extended {@link DomainService} that declares additional API for {@link Customer Customer-dependent} domain objects.
 *
 * @param <T> Customer-dependent domain object type
 */
public interface CustomerBasedDomainService<T> extends DomainService<T> {

    List<T> getAllByCustomer(UserDetails currentUser);

    Page<T> getAllByCustomer(UserDetails currentUser, Pageable pageable);

    List<T> getAllByCustomer(UserDetails currentUser, Predicate predicate);

    Page<T> getAllByCustomer(UserDetails currentUser, Predicate predicate, Pageable pageable);

    Page<T> getAllByCustomer(UserDetails currentUser, String searchString, Predicate predicate, Pageable pageable, boolean isNew, boolean isArchived);
}
