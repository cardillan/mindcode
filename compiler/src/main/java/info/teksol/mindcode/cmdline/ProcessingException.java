package info.teksol.mindcode.cmdline;

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

    public ProcessingException(String format, Object... args) {
        super(String.format(format, args));
    }

    public ProcessingException(Throwable cause, String format, Object... args) {
        super(format.formatted(args), cause);
    }

}
