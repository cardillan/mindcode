package info.teksol.schemacode;

import info.teksol.mindcode.compiler.CompilerMessage;
import info.teksol.mindcode.compiler.MessageLevel;

public record SchemacodeMessage(MessageLevel level, String message) implements CompilerMessage {

    public static SchemacodeMessage error(String message) {
        return new SchemacodeMessage(MessageLevel.ERROR, message);
    }

    public static SchemacodeMessage warn(String message) {
        return new SchemacodeMessage(MessageLevel.WARNING, message);
    }

    public static SchemacodeMessage info(String message) {
        return new SchemacodeMessage(MessageLevel.INFO, message);
    }

    public static SchemacodeMessage debug(String message) {
        return new SchemacodeMessage(MessageLevel.DEBUG, message);
    }

    @Override
    public String source() {
        return "Schemacode";
    }

    @Override
    public String toString() {
        return "MindcodeMessage{" +
                "level=" + level +
                ", message='" + message + '\'' +
                '}';
    }
}
