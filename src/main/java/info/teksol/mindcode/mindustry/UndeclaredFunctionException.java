package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.GenerationException;

public class UndeclaredFunctionException extends GenerationException {
    public UndeclaredFunctionException() {
    }

    public UndeclaredFunctionException(String message) {
        super(message);
    }

    public UndeclaredFunctionException(String message, Throwable cause) {
        super(message, cause);
    }

    public UndeclaredFunctionException(Throwable cause) {
        super(cause);
    }

    public UndeclaredFunctionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
