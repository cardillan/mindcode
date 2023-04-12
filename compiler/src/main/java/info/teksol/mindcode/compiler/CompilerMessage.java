package info.teksol.mindcode.compiler;

public class CompilerMessage {
    private final MessageLevel level;
    private final String message;

    public CompilerMessage(MessageLevel level, String message) {
        this.level = level;
        this.message = message;
    }

    public MessageLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public boolean isError() {
        return level == MessageLevel.ERROR;
    }

    public boolean isWarning() {
        return level == MessageLevel.WARNING;
    }

    public boolean isInfo() {
        return level == MessageLevel.INFO;
    }

    public boolean isDebug() {
        return level == MessageLevel.DEBUG;
    }

    public static CompilerMessage error(String message) {
        return new CompilerMessage(MessageLevel.ERROR, message);
    }

    public static CompilerMessage warn(String message) {
        return new CompilerMessage(MessageLevel.WARNING, message);
    }

    public static CompilerMessage info(String message) {
        return new CompilerMessage(MessageLevel.INFO, message);
    }

    public static CompilerMessage info(String message, Object... args) {
        return new CompilerMessage(MessageLevel.INFO, String.format(message, args));
    }

    public static CompilerMessage debug(String message) {
        return new CompilerMessage(MessageLevel.DEBUG, message);
    }
}