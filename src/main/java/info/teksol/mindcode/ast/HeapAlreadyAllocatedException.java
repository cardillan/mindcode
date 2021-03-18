package info.teksol.mindcode.ast;

import info.teksol.mindcode.ParsingException;

public class HeapAlreadyAllocatedException extends ParsingException {
    public HeapAlreadyAllocatedException() {
    }

    public HeapAlreadyAllocatedException(String message) {
        super(message);
    }

    public HeapAlreadyAllocatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public HeapAlreadyAllocatedException(Throwable cause) {
        super(cause);
    }

    public HeapAlreadyAllocatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
