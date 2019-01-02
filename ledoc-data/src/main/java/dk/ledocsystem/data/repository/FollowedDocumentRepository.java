package dk.ledocsystem.data.repository;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.document.FollowedDocument;
import dk.ledocsystem.data.model.document.FollowedDocumentId;
import dk.ledocsystem.data.model.document.QFollowedDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.CrudRepository;


public interface FollowedDocumentRepository extends CrudRepository<FollowedDocument, FollowedDocumentId>,
        QuerydslPredicateExecutor<FollowedDocument>,
        QuerydslBinderCustomizer<QFollowedDocument> {

    Page<FollowedDocument> findAll(Predicate predicate, Pageable pageable);

    @Override
    default void customize(QuerydslBindings bindings, QFollowedDocument root) {
        bindings.including(root.read, root.followed, root.employee, root.forced);
    }
}
