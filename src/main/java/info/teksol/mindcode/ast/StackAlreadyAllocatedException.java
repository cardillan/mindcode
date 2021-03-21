package info.teksol.mindcode.ast;

import info.teksol.mindcode.ParsingException;

public class StackAlreadyAllocatedException extends ParsingException {
    public StackAlreadyAllocatedException() {
    }

    public StackAlreadyAllocatedException(String message) {
        super(message);
    }

    public StackAlreadyAllocatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public StackAlreadyAllocatedException(Throwable cause) {
        super(cause);
    }

    public StackAlreadyAllocatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
