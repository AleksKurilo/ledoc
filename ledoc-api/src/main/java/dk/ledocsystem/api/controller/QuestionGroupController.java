package dk.ledocsystem.api.controller;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.api.config.security.CurrentUser;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.data.model.review.QuestionGroup;
import dk.ledocsystem.service.api.CustomerService;
import dk.ledocsystem.service.api.QuestionGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/question-groups")
public class QuestionGroupController {

    private final QuestionGroupService questionGroupService;
    private final CustomerService customerService;

    @GetMapping
    @RolesAllowed("super_admin")
    public Iterable<QuestionGroup> getAllQuestionGroups(@QuerydslPredicate(root = QuestionGroup.class) Predicate predicate,
                                                        Pageable pageable) {
        return questionGroupService.getAll(predicate, pageable);
    }

    @GetMapping("/customer")
    public Iterable<QuestionGroup> getAllQuestionGroupsByCustomer(@CurrentUser UserDetails currentUser,
                                                                  @QuerydslPredicate(root = QuestionGroup.class) Predicate predicate,
                                                                  Pageable pageable) {
        return questionGroupService.getAllByCustomer(currentUser, predicate, pageable);
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

    private Long getCustomerId(UserDetails user) {
        return customerService.getByUsername(user.getUsername()).getId();
    }
}
