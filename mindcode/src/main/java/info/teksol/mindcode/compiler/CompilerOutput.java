package info.teksol.mindcode.compiler;

import java.util.List;
import java.util.stream.Collectors;

public record CompilerOutput<T>(T output, List<CompilerMessage> messages) {

    public <R> CompilerOutput<R> withOutput(R output) {
        return new CompilerOutput<>(output, messages);
    }

    public List<String> texts() {
        return messages.stream().map(CompilerMessage::message).collect(Collectors.toList());
    }

    public List<String> errors() {
        return messages.stream().filter(CompilerMessage::isError).map(CompilerMessage::message).map(String::trim).collect(Collectors.toList());
    }

    public List<String> warnings() {
        return messages.stream().filter(CompilerMessage::isWarning).map(CompilerMessage::message).map(String::trim).collect(Collectors.toList());
    }

    public List<String> infos() {
        return messages.stream().filter(CompilerMessage::isInfo).map(CompilerMessage::message).map(String::trim).collect(Collectors.toList());
    }

    public boolean hasErrors() {
        return messages.stream().anyMatch(CompilerMessage::isError);
    }

    public boolean hasWarnings() {
        return messages.stream().anyMatch(CompilerMessage::isWarning);
    }

    public boolean hasInfos() {
        return messages.stream().anyMatch(CompilerMessage::isInfo);
    }

    public boolean hasDebug() {
        return messages.stream().anyMatch(CompilerMessage::isDebug);
    }
}