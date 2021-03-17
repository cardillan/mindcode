package info.teksol.mindcode.ast;

import info.teksol.mindcode.ParsingException;

public class UnallocatedHeapException extends ParsingException {
    public UnallocatedHeapException() {
    }

    public UnallocatedHeapException(String message) {
        super(message);
    }

    public UnallocatedHeapException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnallocatedHeapException(Throwable cause) {
        super(cause);
    }

    public UnallocatedHeapException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
