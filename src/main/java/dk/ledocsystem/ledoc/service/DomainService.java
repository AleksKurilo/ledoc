package dk.ledocsystem.ledoc.service;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.config.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DomainService<T> {

    List<T> getAll();

    Page<T> getAll(Pageable pageable);

    List<T> getAll(Predicate predicate);

    Page<T> getAll(Predicate predicate, Pageable pageable);

    Optional<T> getById(Long id);

    void deleteById(Long id);

    void deleteByIds(Collection<Long> ids);

    default UserPrincipal getCurrentUser() {
        return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
