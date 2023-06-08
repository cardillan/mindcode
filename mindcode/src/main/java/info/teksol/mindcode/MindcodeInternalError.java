package info.teksol.mindcode;

import org.intellij.lang.annotations.PrintFormat;

public class MindcodeInternalError extends RuntimeException {

    public MindcodeInternalError(String message) {
        super(message);
    }

    public MindcodeInternalError(Throwable cause) {
        super(cause);
    }

    public MindcodeInternalError(Throwable cause, String message) {
        super(message, cause);
    }

    public MindcodeInternalError(@PrintFormat String format, Object... args) {
        super(String.format(format, args));
    }

    public MindcodeInternalError(Throwable cause, @PrintFormat String format, Object... args) {
        super(String.format(format, args), cause);
    }
}
