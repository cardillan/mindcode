package info.teksol.mindcode;

import org.intellij.lang.annotations.PrintFormat;

/**
 * Represents an internal error occurring within the Mindcode Compiler. To be thrown when
 * an unexpected situation - and therefore a bug in Mindcode - is encountered.
 * <p>
 * Expected errors need to be handled properly through the standard error-reporting mechanism.
 */
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
