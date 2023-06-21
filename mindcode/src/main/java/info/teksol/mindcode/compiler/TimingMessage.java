package info.teksol.mindcode.compiler;

public record TimingMessage(String phase, long milliseconds) implements CompilerMessage {
    @Override
    public String source() {
        return "Timing";
    }

    @Override
    public MessageLevel level() {
        return MessageLevel.DEBUG;
    }

    @Override
    public String message() {
        return String.format("%s: %,d ms", phase, milliseconds);
    }
}
