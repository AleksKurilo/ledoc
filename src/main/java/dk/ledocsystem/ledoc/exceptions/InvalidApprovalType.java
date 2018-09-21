package dk.ledocsystem.ledoc.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidApprovalType extends LedocException {

    public InvalidApprovalType(String approvalType) {
        super("equipment.approval.type.not.found", approvalType);
    }
}
