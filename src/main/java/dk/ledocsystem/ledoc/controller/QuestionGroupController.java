package dk.ledocsystem.ledoc.controller;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.review.QuestionGroup;
import dk.ledocsystem.ledoc.service.CustomerService;
import dk.ledocsystem.ledoc.service.QuestionGroupService;
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
@RequestMapping("/question-groups")
public class QuestionGroupController {

    private final QuestionGroupService questionGroupService;
    private final CustomerService customerService;

    @GetMapping
    public Iterable<QuestionGroup> getAllQuestionGroups(Pageable pageable) {
        return questionGroupService.getAllByCustomer(getCurrentCustomerId(), pageable);
    }

    @GetMapping("/filter")
    public Iterable<QuestionGroup> getAllFilteredQuestionGroups(@QuerydslPredicate(root = QuestionGroup.class) Predicate predicate,
                                                                Pageable pageable) {
        return questionGroupService.getAllByCustomer(getCurrentCustomerId(), predicate, pageable);
    }

    @GetMapping("/{questionGroupId}")
    public QuestionGroup getQuestionGroupById(@PathVariable Long questionGroupId) {
        return questionGroupService.getById(questionGroupId)
                .orElseThrow(() -> new NotFoundException("question.group.id.not.found", questionGroupId.toString()));
    }

    @DeleteMapping("/{questionGroupId}")
    public void deleteById(@PathVariable Long questionGroupId) {
        questionGroupService.deleteById(questionGroupId);
    }

    @DeleteMapping
    public void deleteByIds(@RequestParam("ids") Collection<Long> ids) {
        questionGroupService.deleteByIds(ids);
    }

    private Long getCurrentCustomerId() {
        return customerService.getCurrentCustomerId();
    }
}
