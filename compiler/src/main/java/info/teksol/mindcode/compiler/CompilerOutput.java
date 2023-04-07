package info.teksol.mindcode.compiler;

import java.util.List;
import java.util.stream.Collectors;

public class CompilerOutput {
    private final String instructions;
    private final List<CompilerMessage> messages;

    public CompilerOutput(String instructions, List<CompilerMessage> messages) {
        this.instructions = instructions;
        this.messages = messages;
    }

    public String getInstructions() {
        return instructions;
    }

    public List<CompilerMessage> getCompilerMessages() {
        return messages;
    }

    public List<String> getAllTexts() {
        return messages.stream().map(CompilerMessage::getMessage).collect(Collectors.toList());
    }

    public List<String> getErrors() {
        return messages.stream().filter(CompilerMessage::isError).map(CompilerMessage::getMessage).collect(Collectors.toList());
    }

    public List<String> getWarnings() {
        return messages.stream().filter(CompilerMessage::isWarning).map(CompilerMessage::getMessage).collect(Collectors.toList());
    }

    public List<String> getMessages() {
        return messages.stream().filter(CompilerMessage::isInfo).map(CompilerMessage::getMessage).collect(Collectors.toList());
    }

    public boolean hasErrors() {
        return messages.stream().anyMatch(CompilerMessage::isError);
    }

    public boolean hasWarnings() {
        return messages.stream().anyMatch(CompilerMessage::isWarning);
    }

    public boolean hasInfo() {
        return messages.stream().anyMatch(CompilerMessage::isInfo);
    }

    public boolean hasDebug() {
        return messages.stream().anyMatch(CompilerMessage::isDebug);
    }
}