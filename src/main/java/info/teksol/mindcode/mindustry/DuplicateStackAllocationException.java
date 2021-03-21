package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.GenerationException;

public class DuplicateStackAllocationException extends GenerationException {
    public DuplicateStackAllocationException() {
    }

    public DuplicateStackAllocationException(String message) {
        super(message);
    }

    public DuplicateStackAllocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateStackAllocationException(Throwable cause) {
        super(cause);
    }

    public DuplicateStackAllocationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
