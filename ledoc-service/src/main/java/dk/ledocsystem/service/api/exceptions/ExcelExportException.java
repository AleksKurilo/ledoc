package dk.ledocsystem.service.api.exceptions;

public class ExcelExportException extends LedocException {
    public ExcelExportException(String msgKey, Object... params) {
        super(msgKey, params);
    }
}
