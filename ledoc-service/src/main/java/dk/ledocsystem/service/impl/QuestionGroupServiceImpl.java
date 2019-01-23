package dk.ledocsystem.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.review.QuestionGroup;
import dk.ledocsystem.data.model.review.QQuestionGroup;
import dk.ledocsystem.data.repository.QuestionGroupRepository;
import dk.ledocsystem.service.api.CustomerService;
import dk.ledocsystem.service.api.QuestionGroupService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
class QuestionGroupServiceImpl implements QuestionGroupService {
    private static final Function<Long, Predicate> CUSTOMER_EQUALS_TO =
            customerId -> ExpressionUtils.eqConst(QQuestionGroup.questionGroup.customer.id, customerId);

    private final QuestionGroupRepository questionGroupRepository;
    private final CustomerService customerService;

    //region GET/DELETE standard API

    @Override
    public List<QuestionGroup> getAll() {
        return questionGroupRepository.findAll();
    }

    @Override
    public Page<QuestionGroup> getAll(@NonNull Pageable pageable) {
        return questionGroupRepository.findAll(pageable);
    }

    @Override
    public List<QuestionGroup> getAll(@NonNull Predicate predicate) {
        return IterableUtils.toList(questionGroupRepository.findAll(predicate));
    }

    @Override
    public Page<QuestionGroup> getAll(@NonNull Predicate predicate, @NonNull Pageable pageable) {
        return questionGroupRepository.findAll(predicate, pageable);
    }

    @Override
    public List<QuestionGroup> getAllByCustomer(@NonNull UserDetails currentUser) {
        return getAllByCustomer(currentUser, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<QuestionGroup> getAllByCustomer(@NonNull UserDetails currentUser, @NonNull Pageable pageable) {
        return getAllByCustomer(currentUser, null, pageable);
    }

    @Override
    public List<QuestionGroup> getAllByCustomer(@NonNull UserDetails currentUser, Predicate predicate) {
        return getAllByCustomer(currentUser, predicate, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<QuestionGroup> getAllByCustomer(@NonNull UserDetails currentUser, Predicate predicate, @NonNull Pageable pageable) {
        return getAllByCustomer(currentUser, "", predicate, pageable, false, false);
    }

    @Override
    public Page<QuestionGroup> getAllByCustomer(@NonNull UserDetails currentUser, String searchString, Predicate predicate, @NonNull Pageable pageable, boolean isNew, boolean isArchived) {
        Predicate combinePredicate = ExpressionUtils.and(predicate, CUSTOMER_EQUALS_TO.apply(customerService.getByUsername(currentUser.getUsername()).getId()));
        return questionGroupRepository.findAll(combinePredicate, pageable);
    }

    @Override
    public Optional<QuestionGroup> getById(@NonNull Long id) {
        return questionGroupRepository.findById(id);
    }

    @Override
    public List<QuestionGroup> getAllById(@NonNull Iterable<Long> ids) {
        return questionGroupRepository.findAllById(ids);
    }

    @Override
    public void deleteById(@NonNull Long id) {
        questionGroupRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void deleteByIds(@NonNull Iterable<Long> questionGroupIds) {
        questionGroupRepository.deleteByIdIn(questionGroupIds);
    }

    //endregion
}
