package info.teksol.mindcode.ast;

import info.teksol.mindcode.ParsingException;

public class OutOfHeapSpaceException extends ParsingException {
    public OutOfHeapSpaceException() {
    }

    public OutOfHeapSpaceException(String message) {
        super(message);
    }

    public OutOfHeapSpaceException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutOfHeapSpaceException(Throwable cause) {
        super(cause);
    }

    public OutOfHeapSpaceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
