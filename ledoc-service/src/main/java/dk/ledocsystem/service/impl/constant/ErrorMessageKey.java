package dk.ledocsystem.service.impl.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorMessageKey {

    public static final String UNEXPECTED_ERROR = "Exception.unexpected";

    public static final String CUSTOMER_ID_NOT_FOUND = "customer.id.not.found";
    public static final String CUSTOMER_NAME_IS_ALREADY_IN_USE = "UniqueName.dk.ledocsystem.service.api.dto.inbound.customer.CustomerCreateDTO.name";
    public static final String CVR_IS_ALREADY_IN_USE = "UniqueCVR.dk.ledocsystem.service.api.dto.inbound.customer.CustomerCreateDTO.cvr";
    public static final String DOCUMENT_ID_NOT_FOUND = "document.id.not.found";
    public static final String DOCUMENT_CATEGORY_ID_NOT_FOUND = "document.category.id.not.found";
    public static final String DOCUMENT_SUBCATEGORY_ID_NOT_FOUND = "document.subcategory.id.not.found";
    public static final String DOCUMENT_NAME_IS_ALREADY_IN_USE = "UniqueName.dk.ledocsystem.service.api.dto.inbound.document.DocumentDTO.name";
    public static final String EMPLOYEE_ID_NOT_FOUND = "employee.id.not.found";
    public static final String EMPLOYEE_USERNAME_NOT_FOUND = "employee.username.not.found";
    public static final String EMPLOYEE_REVIEW_NOT_APPLICABLE = "employee.review.not.applicable";
    public static final String EMPLOYEE_USERNAME_IS_ALREADY_IN_USE = "employee.username.already.in.use";
    public static final String EMPLOYEE_RESPONSIBLE_NOT_FOUND = "employee.responsible.not.found";
    public static final String EMPLOYEE_RESPONSIBLE_OF_SKILL_NOT_FOUND = "employee.responsible.of.skills.not.found";
    public static final String EQUIPMENT_ID_NOT_FOUND = "equipment.id.not.found";
    public static final String EQUIPMENT_NAME_IS_ALREADY_IN_USE = "UniqueName.dk.ledocsystem.service.api.dto.inbound.equipment.EquipmentDTO.name";
    public static final String EQUIPMENT_CATEGORY_NOT_FOUND = "equipment.category.not.found";
    public static final String EQUIPMENT_AUTHENTICATION_TYPE_NOT_FOUND = "equipment.authentication.type.not.found";
    public static final String EQUIPMENT_BORROWER_NOT_FOUND = "equipment.borrower.not.found";
    public static final String LOCATION_ID_NOT_FOUND = "location.id.not.found";
    public static final String LOCATION_ADDRESS_LOCATION_NOT_FOUND = "location.address.location.not.found";
    public static final String LOCATION_NAME_IS_ALREADY_IN_USE = "location.name.already.in.use";
    public static final String REVIEW_QUESTION_ID_NOT_FOUND = "review.question.id.not.found";
    public static final String REVIEW_TEMPLATE_ID_NOT_FOUND = "review.template.id.not.found";

    public static final String USER_NAME_NOT_FOUND = "username.not.found";
    public static final String USER_PASSWORD_INVALID = "username.password.invalid";
    public static final String RESET_TOKEN_NOT_FOUND = "reset.token.not.found";


}
