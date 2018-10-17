package dk.ledocsystem.ledoc.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.model.review.QReviewTemplate;
import dk.ledocsystem.ledoc.model.review.ReviewTemplate;
import dk.ledocsystem.ledoc.repository.ReviewTemplateRepository;
import dk.ledocsystem.ledoc.service.ReviewTemplateService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
class ReviewTemplateServiceImpl implements ReviewTemplateService {
    private static final Function<Long, Predicate> CUSTOMER_EQUALS_TO =
            customerId -> ExpressionUtils.eqConst(QReviewTemplate.reviewTemplate.customer.id, customerId);

    private final ReviewTemplateRepository reviewTemplateRepository;

    @PostConstruct
    private void initializeConstants() {
        if (!ReviewTemplateConstantsInitializer.constantsCreated(reviewTemplateRepository)) {
            ReviewTemplateConstantsInitializer.createConstants(reviewTemplateRepository);
        }
    }

    //region GET/DELETE standard API

    @Override
    public List<ReviewTemplate> getAll() {
        return reviewTemplateRepository.findAll();
    }

    @Override
    public Page<ReviewTemplate> getAll(@NonNull Pageable pageable) {
        return reviewTemplateRepository.findAll(pageable);
    }

    @Override
    public List<ReviewTemplate> getAll(@NonNull Predicate predicate) {
        return IterableUtils.toList(reviewTemplateRepository.findAll(predicate));
    }

    @Override
    public Page<ReviewTemplate> getAll(@NonNull Predicate predicate, @NonNull Pageable pageable) {
        return reviewTemplateRepository.findAll(predicate, pageable);
    }

    @Override
    public List<ReviewTemplate> getAllByCustomer(@NonNull Long customerId) {
        return getAllByCustomer(customerId, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<ReviewTemplate> getAllByCustomer(@NonNull Long customerId, @NonNull Pageable pageable) {
        return getAllByCustomer(customerId, null, pageable);
    }

    @Override
    public List<ReviewTemplate> getAllByCustomer(@NonNull Long customerId, Predicate predicate) {
        return getAllByCustomer(customerId, predicate, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<ReviewTemplate> getAllByCustomer(@NonNull Long customerId, Predicate predicate, @NonNull Pageable pageable) {
        Predicate combinePredicate = ExpressionUtils.and(predicate, CUSTOMER_EQUALS_TO.apply(customerId));
        return reviewTemplateRepository.findAll(combinePredicate, pageable);
    }

    @Override
    public Optional<ReviewTemplate> getById(@NonNull Long id) {
        return reviewTemplateRepository.findById(id);
    }

    @Override
    public List<ReviewTemplate> getAllById(@NonNull Iterable<Long> ids) {
        return reviewTemplateRepository.findAllById(ids);
    }

    @Override
    public void deleteById(@NonNull Long id) {
        reviewTemplateRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void deleteByIds(@NonNull Iterable<Long> reviewTemplateIds) {
        reviewTemplateRepository.deleteByIdIn(reviewTemplateIds);
    }

    //endregion
}