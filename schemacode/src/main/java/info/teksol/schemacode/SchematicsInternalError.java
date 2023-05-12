package info.teksol.schemacode;

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

    public SchematicsInternalError(String format, Object... args) {
        super(String.format(format, args));
    }

    public SchematicsInternalError(Throwable cause, String format, Object... args) {
        super(String.format(format, args), cause);
    }
}
