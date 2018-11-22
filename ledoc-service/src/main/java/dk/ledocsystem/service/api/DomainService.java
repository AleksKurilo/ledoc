package dk.ledocsystem.service.api;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Implementors provide base API to interact with domain objects of type T.
 *
 * @param <T> Domain object type
 */
public interface DomainService<T> {

    List<T> getAll();

    Page<T> getAll(Pageable pageable);

    List<T> getAll(Predicate predicate);

    Page<T> getAll(Predicate predicate, Pageable pageable);

    Optional<T> getById(Long id);

    List<T> getAllById(Iterable<Long> ids);

    void deleteById(Long id);

    void deleteByIds(Iterable<Long> ids);
}
