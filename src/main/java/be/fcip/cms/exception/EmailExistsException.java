package be.fcip.cms.exception;

public class EmailExistsException extends Throwable {

    public EmailExistsException(final String message) {
        super(message);
    }
}