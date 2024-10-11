package info.teksol.mindcode.compiler;

import info.teksol.mindcode.MindcodeMessage;

import java.util.List;
import java.util.stream.Collectors;

public record CompilerOutput<T>(T output, List<MindcodeMessage> messages, String textBuffer, int steps) {

    public <R> CompilerOutput<R> withOutput(R output) {
        return new CompilerOutput<>(output, messages, textBuffer, steps);
    }

    public CompilerOutput(T output, List<MindcodeMessage> messages) {
        this(output, messages, null, 0);
    }

    public void addMessage(MindcodeMessage message) {
        messages.add(message);
    }

    public List<String> texts() {
        return messages.stream().map(MindcodeMessage::message).collect(Collectors.toList());
    }

    public List<String> errors() {
        return messages.stream().filter(MindcodeMessage::isError).map(MindcodeMessage::message).map(String::trim).collect(Collectors.toList());
    }

    public List<String> warnings() {
        return messages.stream().filter(MindcodeMessage::isWarning).map(MindcodeMessage::message).map(String::trim).collect(Collectors.toList());
    }

    public List<String> infos() {
        return messages.stream().filter(MindcodeMessage::isInfo).map(MindcodeMessage::message).map(String::trim).collect(Collectors.toList());
    }

    public boolean hasErrors() {
        return messages.stream().anyMatch(MindcodeMessage::isError);
    }

    public boolean hasWarnings() {
        return messages.stream().anyMatch(MindcodeMessage::isWarning);
    }

    public boolean hasInfos() {
        return messages.stream().anyMatch(MindcodeMessage::isInfo);
    }

    public boolean hasDebug() {
        return messages.stream().anyMatch(MindcodeMessage::isDebug);
    }
}