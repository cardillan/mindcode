package info.teksol.mc.common;

import info.teksol.mc.emulator.Assertion;
import info.teksol.mc.emulator.TextBuffer;
import info.teksol.mc.messages.MindcodeMessage;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@NullMarked
public record CompilerOutput<T extends @Nullable Object>(
        T output, String fileName, List<MindcodeMessage> messages, List<Assertion> assertions,
        @Nullable TextBuffer textBuffer, int steps) {

    public <R> CompilerOutput<R> withOutput(R output) {
        return new CompilerOutput<>(output, fileName, messages, assertions, textBuffer, steps);
    }

    public CompilerOutput(T output, String fileName, List<MindcodeMessage> messages) {
        this(output, fileName, messages, List.of(), null, 0);
    }

    public CompilerOutput(List<MindcodeMessage> messages) {
        this(null, "", messages, List.of(), null, 0);
    }

    public void addMessage(MindcodeMessage message) {
        messages.add(message);
    }

    public String getStringOutput() {
        return output == null ? "" : output.toString();
    }

    public List<String> getPrintOutput() {
        return textBuffer != null ? textBuffer.getPrintOutput() : List.of();
    }

    public String getProgramOutput() {
        return textBuffer != null ? textBuffer.getFormattedOutput() : "";
    }

    public boolean hasProgramOutput() {
        return textBuffer != null && !textBuffer.isEmpty();
    }

    public <M> List<M> texts(Function<MindcodeMessage, M> messageTransformer) {
        return formatMessages(m -> true, messageTransformer);
    }

    public <M> List<M> errors(Function<MindcodeMessage, M> messageTransformer) {
        return formatMessages(MindcodeMessage::isError, messageTransformer);
    }

    public <M> List<M> warnings(Function<MindcodeMessage, M> messageTransformer) {
        return formatMessages(MindcodeMessage::isWarning, messageTransformer);
    }

    public <M> List<M> infos(Function<MindcodeMessage, M> messageTransformer) {
        return formatMessages(MindcodeMessage::isInfo, messageTransformer);
    }

    public <M> List<M> formatMessages(Predicate<MindcodeMessage> filter, Function<MindcodeMessage, M> messageTransformer) {
        return messages.stream().filter(filter)
                .map(messageTransformer)
                .collect(Collectors.toCollection(ArrayList::new));
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
