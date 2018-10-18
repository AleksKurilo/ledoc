package dk.ledocsystem.ledoc.controller;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.review.ReviewQuestion;
import dk.ledocsystem.ledoc.service.CustomerService;
import dk.ledocsystem.ledoc.service.ReviewQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.REVIEW_QUESTION_ID_NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review-questions")
public class ReviewQuestionController {

    private final ReviewQuestionService reviewQuestionService;
    private final CustomerService customerService;

    @GetMapping
    public Iterable<ReviewQuestion> getAllReviewQuestions(Pageable pageable) {
        return reviewQuestionService.getAllByCustomer(getCurrentCustomerId(), pageable);
    }

    @GetMapping("/filter")
    public Iterable<ReviewQuestion> getAllFilteredReviewQuestions(@QuerydslPredicate(root = ReviewQuestion.class) Predicate predicate,
                                                                  Pageable pageable) {
        return reviewQuestionService.getAllByCustomer(getCurrentCustomerId(), predicate, pageable);
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

    private Long getCurrentCustomerId() {
        return customerService.getCurrentCustomerId();
    }
}
