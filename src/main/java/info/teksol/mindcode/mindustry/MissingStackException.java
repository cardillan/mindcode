package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.GenerationException;

public class MissingStackException extends GenerationException {
    public MissingStackException() {
    }

    public MissingStackException(String message) {
        super(message);
    }

    public MissingStackException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingStackException(Throwable cause) {
        super(cause);
    }

    public MissingStackException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
