package dk.ledocsystem.ledoc.controller;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.review.ReviewQuestion;
import dk.ledocsystem.ledoc.service.CustomerService;
import dk.ledocsystem.ledoc.service.ReviewQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

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
                .orElseThrow(() -> new NotFoundException("review.question.id.not.found", reviewQuestionId.toString()));
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
