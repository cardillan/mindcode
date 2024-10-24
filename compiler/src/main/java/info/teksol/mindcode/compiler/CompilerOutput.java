package info.teksol.mindcode.compiler;

import info.teksol.mindcode.MindcodeMessage;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

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

    public <M> List<M> texts(Function<MindcodeMessage, M> messageTransformer) {
        return formatMessages(m -> true, messageTransformer);
    }

    public  <M> List<M> errors(Function<MindcodeMessage, M> messageTransformer) {
        return formatMessages(MindcodeMessage::isError, messageTransformer);
    }

    public  <M> List<M> warnings(Function<MindcodeMessage, M> messageTransformer) {
        return formatMessages(MindcodeMessage::isWarning, messageTransformer);
    }

    public  <M> List<M> infos(Function<MindcodeMessage, M> messageTransformer) {
        return formatMessages(MindcodeMessage::isInfo, messageTransformer);
    }

    private <M> List<M> formatMessages(Predicate<MindcodeMessage> filter, Function<MindcodeMessage, M> messageTransformer) {
        return messages.stream().filter(filter)
                .map(messageTransformer)
                .toList();
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