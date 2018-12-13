package dk.ledocsystem.api.controller;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.api.config.security.CurrentUser;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.data.model.review.ReviewQuestion;
import dk.ledocsystem.service.api.CustomerService;
import dk.ledocsystem.service.api.ReviewQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Collection;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.REVIEW_QUESTION_ID_NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review-questions")
public class ReviewQuestionController {

    private final ReviewQuestionService reviewQuestionService;
    private final CustomerService customerService;

    @GetMapping
    @RolesAllowed("super_admin")
    public Iterable<ReviewQuestion> getAllReviewQuestions(@QuerydslPredicate(root = ReviewQuestion.class) Predicate predicate,
                                                          Pageable pageable) {
        return reviewQuestionService.getAll(predicate, pageable);
    }

    @GetMapping("/customer")
    public Iterable<ReviewQuestion> getAllReviewQuestionsByCustomer(@CurrentUser UserDetails currentUser,
                                                                    @QuerydslPredicate(root = ReviewQuestion.class) Predicate predicate,
                                                                    Pageable pageable) {
        Long customerId = getCustomerId(currentUser);
        return reviewQuestionService.getAllByCustomer(customerId, predicate, pageable);
    }

    @GetMapping("/{reviewQuestionId}")
    public ReviewQuestion getReviewQuestionById(@PathVariable Long reviewQuestionId) {
        return reviewQuestionService.getById(reviewQuestionId)
                .orElseThrow(() -> new NotFoundException(REVIEW_QUESTION_ID_NOT_FOUND, reviewQuestionId.toString()));
    }

    @DeleteMapping("/{reviewQuestionId}")
    public void deleteById(@PathVariable Long reviewQuestionId) {
        reviewQuestionService.deleteById(reviewQuestionId);
    }

    @DeleteMapping
    public void deleteByIds(@RequestParam("ids") Collection<Long> ids) {
        reviewQuestionService.deleteByIds(ids);
    }

    private Long getCustomerId(UserDetails user) {
        return customerService.getByUsername(user.getUsername()).getId();
    }
}
