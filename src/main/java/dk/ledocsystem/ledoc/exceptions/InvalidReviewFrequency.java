package dk.ledocsystem.ledoc.exceptions;

public class InvalidReviewFrequency extends LedocException {

    public InvalidReviewFrequency(String msgCode, Object... params) {
        super(msgCode, params);
    }
}
