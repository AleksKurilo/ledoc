package dk.ledocsystem.ledoc.controller;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.review.ReviewTemplate;
import dk.ledocsystem.ledoc.service.ReviewTemplateService;
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
@RequestMapping("/review-templates")
public class ReviewTemplateController {

    private final ReviewTemplateService reviewTemplateService;

    @GetMapping
    public Iterable<ReviewTemplate> getAllReviewTemplates(Pageable pageable) {
        return reviewTemplateService.getAll(pageable);
    }

    @GetMapping("/filter")
    public Iterable<ReviewTemplate> getAllFilteredReviewTemplates(@QuerydslPredicate(root = ReviewTemplate.class) Predicate predicate,
                                                                  Pageable pageable) {
        return reviewTemplateService.getAll(predicate, pageable);
    }

    @GetMapping("/{reviewTemplateId}")
    public ReviewTemplate getReviewTemplateById(@PathVariable Long reviewTemplateId) {
        return reviewTemplateService.getById(reviewTemplateId)
                .orElseThrow(() -> new NotFoundException("review.template.id.not.found", reviewTemplateId.toString()));
    }

    @DeleteMapping("/{reviewTemplateId}")
    public void deleteById(@PathVariable Long reviewTemplateId) {
        reviewTemplateService.deleteById(reviewTemplateId);
    }

    @DeleteMapping
    public void deleteByIds(@RequestParam("ids") Collection<Long> ids) {
        reviewTemplateService.deleteByIds(ids);
    }
}
