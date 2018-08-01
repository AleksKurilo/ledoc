package dk.ledocsystem.ledoc.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DomainService<T> {

    List<T> getAll();

    Optional<T> getById(Long id);

    void deleteById(Long id);

    void deleteByIds(Collection<Long> ids);

    default Authentication getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
