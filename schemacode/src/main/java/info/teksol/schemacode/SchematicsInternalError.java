package info.teksol.schemacode;

import org.intellij.lang.annotations.PrintFormat;

public class SchematicsInternalError extends RuntimeException {

    public SchematicsInternalError(String message) {
        super(message);
    }

    public SchematicsInternalError(Throwable cause) {
        super(cause);
    }

    public SchematicsInternalError(Throwable cause, String message) {
        super(message, cause);
    }

    public SchematicsInternalError(@PrintFormat String format, Object... args) {
        super(String.format(format, args));
    }

    public SchematicsInternalError(Throwable cause, @PrintFormat String format, Object... args) {
        super(String.format(format, args), cause);
    }
}
