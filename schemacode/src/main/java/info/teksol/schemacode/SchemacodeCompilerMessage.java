package info.teksol.schemacode;

import info.teksol.mindcode.MessageLevel;
import info.teksol.mindcode.MindcodeMessage;

// TODO Change parent to CompilerMessage and provide positional information for messages
public record SchemacodeCompilerMessage(MessageLevel level, String message) implements MindcodeMessage {

    public static SchemacodeCompilerMessage error(String message) {
        return new SchemacodeCompilerMessage(MessageLevel.ERROR, message);
    }

    public static SchemacodeCompilerMessage warn(String message) {
        return new SchemacodeCompilerMessage(MessageLevel.WARNING, message);
    }

    public static SchemacodeCompilerMessage info(String message) {
        return new SchemacodeCompilerMessage(MessageLevel.INFO, message);
    }

    public static SchemacodeCompilerMessage debug(String message) {
        return new SchemacodeCompilerMessage(MessageLevel.DEBUG, message);
    }

    @Override
    public String toString() {
        return "SchemacodeCompilerMessage{" +
                "level=" + level +
                ", message='" + message + '\'' +
                '}';
    }
}
