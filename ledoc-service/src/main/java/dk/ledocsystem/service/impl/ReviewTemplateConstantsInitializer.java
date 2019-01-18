package dk.ledocsystem.service.impl;

import dk.ledocsystem.data.model.review.DocumentReviewTemplate;
import dk.ledocsystem.data.model.review.EmployeeReviewTemplate;
import dk.ledocsystem.data.model.review.EquipmentReviewTemplate;
import dk.ledocsystem.data.model.review.QuestionGroup;
import dk.ledocsystem.data.model.review.QuestionType;
import dk.ledocsystem.data.model.review.ReviewQuestion;
import dk.ledocsystem.data.model.review.ReviewTemplate;
import dk.ledocsystem.data.model.review.SupplierReviewTemplate;
import dk.ledocsystem.data.repository.ReviewTemplateRepository;

import java.util.Arrays;
import java.util.Collections;

import static dk.ledocsystem.service.api.ReviewTemplateService.*;

class ReviewTemplateConstantsInitializer {

    static boolean constantsCreated(ReviewTemplateRepository reviewTemplateRepository) {
        return reviewTemplateRepository.findByNameAndCustomer(EMPLOYEE_QUICK_REVIEW_TEMPLATE_NAME, null).isPresent();
    }

    static void createConstants(ReviewTemplateRepository reviewTemplateRepository) {
        ReviewQuestion reviewQuestionForEmployee = ReviewQuestion.builder()
                .title("Approved/Declined")
                .wording("Is employee approved?")
                .questionType(QuestionType.YES_NO)
                .build();
        ReviewQuestion reviewQuestionForEquipment = ReviewQuestion.builder()
                .title("Approved/Declined")
                .wording("Is equipment approved?")
                .questionType(QuestionType.YES_NO)
                .build();
        ReviewQuestion reviewQuestionForDocument = ReviewQuestion.builder()
                .title("Approved/Declined")
                .wording("Is document approved?")
                .questionType(QuestionType.YES_NO)
                .build();
        ReviewQuestion reviewQuestionForSupplier = ReviewQuestion.builder()
                .title("Approved/Declined")
                .wording("Is supplier approved?")
                .questionType(QuestionType.YES_NO)
                .build();

        QuestionGroup questionGroupForEmployee = QuestionGroup.builder()
                .name("Question group for employee quick review")
                .reviewQuestion(reviewQuestionForEmployee)
                .build();
        QuestionGroup questionGroupForEquipment = QuestionGroup.builder()
                .name("Question group for equipment quick review")
                .reviewQuestion(reviewQuestionForEquipment)
                .build();
        QuestionGroup questionGroupForDocument = QuestionGroup.builder()
                .name("Question group for document quick review")
                .reviewQuestion(reviewQuestionForDocument)
                .build();
        QuestionGroup questionGroupForSupplier = QuestionGroup.builder()
                .name("Question group for supplier quick review")
                .reviewQuestion(reviewQuestionForSupplier)
                .build();

        ReviewTemplate employeeQuickReviewTemplate = new EmployeeReviewTemplate();
        employeeQuickReviewTemplate.setName(EMPLOYEE_QUICK_REVIEW_TEMPLATE_NAME);
        employeeQuickReviewTemplate.setIsGlobal(true);
        employeeQuickReviewTemplate.setEditable(false);
        employeeQuickReviewTemplate.setQuestionGroups(Collections.singletonList(questionGroupForEmployee));

        ReviewTemplate equipmentQuickReviewTemplate = new EquipmentReviewTemplate();
        equipmentQuickReviewTemplate.setName(EQUIPMENT_QUICK_REVIEW_TEMPLATE_NAME);
        equipmentQuickReviewTemplate.setIsGlobal(true);
        equipmentQuickReviewTemplate.setEditable(false);
        equipmentQuickReviewTemplate.setQuestionGroups(Collections.singletonList(questionGroupForEquipment));

        ReviewTemplate documentQuickReviewTemplate = new DocumentReviewTemplate();
        documentQuickReviewTemplate.setName(DOCUMENT_QUICK_REVIEW_TEMPLATE_NAME);
        documentQuickReviewTemplate.setIsGlobal(true);
        documentQuickReviewTemplate.setEditable(false);
        documentQuickReviewTemplate.setQuestionGroups(Collections.singletonList(questionGroupForDocument));

        ReviewTemplate supplierQuickReviewTemplate = new SupplierReviewTemplate();
        supplierQuickReviewTemplate.setName(SUPPLIER_QUICK_REVIEW_TEMPLATE_NAME);
        supplierQuickReviewTemplate.setIsGlobal(true);
        supplierQuickReviewTemplate.setEditable(false);
        supplierQuickReviewTemplate.setQuestionGroups(Collections.singletonList(questionGroupForSupplier));

        reviewTemplateRepository.saveAll(Arrays.asList(employeeQuickReviewTemplate, equipmentQuickReviewTemplate,
                documentQuickReviewTemplate, supplierQuickReviewTemplate));
    }

}
