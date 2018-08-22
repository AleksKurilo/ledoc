package dk.ledocsystem.ledoc.exceptions;

public class ExcelExportException extends LedocException {
    public ExcelExportException(String msgKey, Object... params) {
        super(msgKey, params);
    }
}
