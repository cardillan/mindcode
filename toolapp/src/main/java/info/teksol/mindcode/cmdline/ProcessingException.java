package info.teksol.mindcode.cmdline;

import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;

@NullMarked
class ProcessingException extends RuntimeException {

    public ProcessingException(String message) {
        super(message);
    }

    public ProcessingException(Throwable cause) {
        super(cause);
    }

    public ProcessingException(Throwable cause, String message) {
        super(message, cause);
    }

    public ProcessingException(@PrintFormat String format, Object... args) {
        super(String.format(format, args));
    }

    public ProcessingException(Throwable cause, @PrintFormat String format, Object... args) {
        super(format.formatted(args), cause);
    }

}
