package info.teksol.mindcode.ast;

import info.teksol.mindcode.ParsingException;

public class InvalidHeapAllocationException extends ParsingException {
    public InvalidHeapAllocationException() {
    }

    public InvalidHeapAllocationException(String message) {
        super(message);
    }

    public InvalidHeapAllocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidHeapAllocationException(Throwable cause) {
        super(cause);
    }

    public InvalidHeapAllocationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
