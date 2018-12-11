package dk.ledocsystem.api.controller;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.api.config.security.CurrentUser;
import dk.ledocsystem.service.api.CustomerService;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.data.model.review.ReviewTemplate;
import dk.ledocsystem.service.api.ReviewTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Collection;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.REVIEW_TEMPLATE_ID_NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review-templates")
public class ReviewTemplateController {

    private final ReviewTemplateService reviewTemplateService;
    private final CustomerService customerService;

    @GetMapping
    @RolesAllowed("super_admin")
    public Iterable<ReviewTemplate> getAllReviewTemplates(@QuerydslPredicate(root = ReviewTemplate.class) Predicate predicate,
                                                          Pageable pageable) {
        return reviewTemplateService.getAll(predicate, pageable);
    }

    @GetMapping("/global")
    public Iterable<ReviewTemplate> getAllGlobalReviewTemplates(@QuerydslPredicate(root = ReviewTemplate.class) Predicate predicate,
                                                                Pageable pageable) {
        return reviewTemplateService.getAllGlobal(predicate, pageable);
    }

    @GetMapping("/customer")
    public Iterable<ReviewTemplate> getAllReviewTemplatesByCustomer(@CurrentUser UserDetails currentUser,
                                                                    @QuerydslPredicate(root = ReviewTemplate.class) Predicate predicate,
                                                                    Pageable pageable) {
        Long customerId = getCustomerId(currentUser);
        return reviewTemplateService.getAllByCustomer(customerId, predicate, pageable);
    }

    @GetMapping("/module")
    public Iterable<ReviewTemplate> getAllReviewTemplatesByCustomer(@CurrentUser UserDetails currentUser,
                                                                    @RequestParam String module) {
        Long customerId = getCustomerId(currentUser);
        return new PageImpl<>(reviewTemplateService.getAllByModule(customerId, module));
    }

    @GetMapping("/{reviewTemplateId}")
    public ReviewTemplate getReviewTemplateById(@PathVariable Long reviewTemplateId) {
        return reviewTemplateService.getById(reviewTemplateId)
                .orElseThrow(() -> new NotFoundException(REVIEW_TEMPLATE_ID_NOT_FOUND, reviewTemplateId.toString()));
    }

    @DeleteMapping("/{reviewTemplateId}")
    public void deleteById(@PathVariable Long reviewTemplateId) {
        reviewTemplateService.deleteById(reviewTemplateId);
    }

    @DeleteMapping
    public void deleteByIds(@RequestParam("ids") Collection<Long> ids) {
        reviewTemplateService.deleteByIds(ids);
    }

    private Long getCustomerId(UserDetails user) {
        return customerService.getByUsername(user.getUsername()).getId();
    }
}
