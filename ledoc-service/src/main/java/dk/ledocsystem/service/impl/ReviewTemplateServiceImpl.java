package dk.ledocsystem.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.review.Module;
import dk.ledocsystem.data.model.review.QReviewTemplate;
import dk.ledocsystem.data.model.review.ReviewTemplate;
import dk.ledocsystem.data.repository.ReviewTemplateRepository;
import dk.ledocsystem.service.api.CustomerService;
import dk.ledocsystem.service.api.ReviewTemplateService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
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
    private static final Predicate ONLY_GLOBAL = QReviewTemplate.reviewTemplate.isGlobal.eq(true);
    private static final Function<Module, Predicate> MODULE_EQUALS =
            module -> ExpressionUtils.eqConst(QReviewTemplate.reviewTemplate.module, module);

    private final CustomerService customerService;
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
    public List<ReviewTemplate> getAllByCustomer(@NonNull UserDetails currentUser) {
        return getAllByCustomer(currentUser, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<ReviewTemplate> getAllByCustomer(@NonNull UserDetails currentUser, @NonNull Pageable pageable) {
        return getAllByCustomer(currentUser, null, pageable);
    }

    @Override
    public List<ReviewTemplate> getAllByCustomer(@NonNull UserDetails currentUser, Predicate predicate) {
        return getAllByCustomer(currentUser, predicate, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<ReviewTemplate> getAllByCustomer(@NonNull UserDetails currentUser, Predicate predicate, @NonNull Pageable pageable) {
        return getAllByCustomer(currentUser, "", predicate, pageable, false);
    }

    @Override
    public Page<ReviewTemplate> getAllByCustomer(@NonNull UserDetails currentUser, String searchString, Predicate predicate, @NonNull Pageable pageable, boolean isNew) {
        Predicate combinePredicate = ExpressionUtils.and(predicate, CUSTOMER_EQUALS_TO.apply(customerService.getByUsername(currentUser.getUsername()).getId()));
        return reviewTemplateRepository.findAll(combinePredicate, pageable);
    }

    @Override
    public Optional<ReviewTemplate> getByName(String name) {
        return reviewTemplateRepository.findByName(name);
    }

    @Override
    public Page<ReviewTemplate> getAllGlobal(Predicate predicate, @NonNull Pageable pageable) {
        Predicate combinePredicate = ExpressionUtils.and(predicate, ONLY_GLOBAL);
        return getAll(combinePredicate, pageable);
    }

    @Override
    public List<ReviewTemplate> getAllByModule(Long customerId, String moduleString) {
        Module module = Module.fromString(moduleString);
        Predicate combinePredicate = ExpressionUtils.and(MODULE_EQUALS.apply(module),
                ExpressionUtils.or(ONLY_GLOBAL, CUSTOMER_EQUALS_TO.apply(customerId)));
        return getAll(combinePredicate);
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
