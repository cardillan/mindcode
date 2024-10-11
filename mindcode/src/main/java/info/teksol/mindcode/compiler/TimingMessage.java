package info.teksol.mindcode.compiler;

import info.teksol.mindcode.MessageLevel;
import info.teksol.mindcode.MindcodeMessage;

public record TimingMessage(String phase, long milliseconds) implements MindcodeMessage {

    @Override
    public MessageLevel level() {
        return MessageLevel.DEBUG;
    }

    @Override
    public String message() {
        return String.format("%s: %,d ms", phase, milliseconds);
    }

    @Override
    public String toString() {
        return "TimingMessage{" +
                "phase='" + phase + '\'' +
                ", milliseconds=" + milliseconds +
                '}';
    }
}
