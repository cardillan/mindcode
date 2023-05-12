package info.teksol.mindcode.compiler;

public record MindcodeMessage(MessageLevel level, String message) implements CompilerMessage {

    public static MindcodeMessage error(String message) {
        return new MindcodeMessage(MessageLevel.ERROR, message);
    }

    public static MindcodeMessage warn(String message) {
        return new MindcodeMessage(MessageLevel.WARNING, message);
    }

    public static MindcodeMessage info(String message) {
        return new MindcodeMessage(MessageLevel.INFO, message);
    }

    public static MindcodeMessage info(String message, Object... args) {
        return new MindcodeMessage(MessageLevel.INFO, String.format(message, args));
    }

    public static MindcodeMessage debug(String message) {
        return new MindcodeMessage(MessageLevel.DEBUG, message);
    }

    @Override
    public String source() {
        return "Mindcode";
    }

    @Override
    public String toString() {
        return "MindcodeMessage{" +
                "level=" + level +
                ", message='" + message + '\'' +
                '}';
    }
}
