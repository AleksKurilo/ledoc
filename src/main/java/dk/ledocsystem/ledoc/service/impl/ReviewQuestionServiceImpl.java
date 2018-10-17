package dk.ledocsystem.ledoc.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.model.review.QReviewQuestion;
import dk.ledocsystem.ledoc.model.review.ReviewQuestion;
import dk.ledocsystem.ledoc.repository.ReviewQuestionRepository;
import dk.ledocsystem.ledoc.service.ReviewQuestionService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ReviewQuestionServiceImpl implements ReviewQuestionService {
    private static final Function<Long, Predicate> CUSTOMER_EQUALS_TO =
            customerId -> ExpressionUtils.eqConst(QReviewQuestion.reviewQuestion.customer.id, customerId);

    private final ReviewQuestionRepository reviewQuestionRepository;

    //region GET/DELETE standard API

    @Override
    public List<ReviewQuestion> getAll() {
        return reviewQuestionRepository.findAll();
    }

    @Override
    public Page<ReviewQuestion> getAll(@NonNull Pageable pageable) {
        return reviewQuestionRepository.findAll(pageable);
    }

    @Override
    public List<ReviewQuestion> getAll(@NonNull Predicate predicate) {
        return IterableUtils.toList(reviewQuestionRepository.findAll(predicate));
    }

    @Override
    public Page<ReviewQuestion> getAll(@NonNull Predicate predicate, @NonNull Pageable pageable) {
        return reviewQuestionRepository.findAll(predicate, pageable);
    }

    @Override
    public List<ReviewQuestion> getAllByCustomer(@NonNull Long customerId) {
        return getAllByCustomer(customerId, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<ReviewQuestion> getAllByCustomer(@NonNull Long customerId, @NonNull Pageable pageable) {
        return getAllByCustomer(customerId, null, pageable);
    }

    @Override
    public List<ReviewQuestion> getAllByCustomer(@NonNull Long customerId, Predicate predicate) {
        return getAllByCustomer(customerId, predicate, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<ReviewQuestion> getAllByCustomer(@NonNull Long customerId, Predicate predicate, @NonNull Pageable pageable) {
        Predicate combinePredicate = ExpressionUtils.and(predicate, CUSTOMER_EQUALS_TO.apply(customerId));
        return reviewQuestionRepository.findAll(combinePredicate, pageable);
    }

    @Override
    public Optional<ReviewQuestion> getById(@NonNull Long id) {
        return reviewQuestionRepository.findById(id);
    }

    @Override
    public List<ReviewQuestion> getAllById(@NonNull Iterable<Long> ids) {
        return reviewQuestionRepository.findAllById(ids);
    }

    @Override
    public void deleteById(@NonNull Long id) {
        reviewQuestionRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void deleteByIds(@NonNull Iterable<Long> reviewQuestionIds) {
        reviewQuestionRepository.deleteByIdIn(reviewQuestionIds);
    }

    //endregion
}
