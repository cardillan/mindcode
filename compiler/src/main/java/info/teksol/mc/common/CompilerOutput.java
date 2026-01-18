package info.teksol.mc.common;

import info.teksol.mc.emulator.Assertion;
import info.teksol.mc.emulator.EmulatorMessage;
import info.teksol.mc.messages.MindcodeMessage;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@NullMarked
public record CompilerOutput<T>(
        @Nullable T output,
        String fileName,
        List<MindcodeMessage> messages,
        List<Assertion> assertions
) {

    public <R> CompilerOutput<R> withOutput(R output) {
        return new CompilerOutput<>(output, fileName, messages, assertions);
    }

    public CompilerOutput(T output, String fileName, List<MindcodeMessage> messages) {
        this(output, fileName, messages, List.of());
    }

    public CompilerOutput(List<MindcodeMessage> messages) {
        this(null, "", messages, List.of());
    }

    public void addMessage(MindcodeMessage message) {
        messages.add(message);
    }

    public String getStringOutput() {
        return output == null ? "" : output.toString();
    }

    public T existingOutput() {
        return Objects.requireNonNull(output);
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

    public boolean hasCompilerErrors() {
        return messages.stream().anyMatch(m -> m.isError() &&!(m instanceof EmulatorMessage));
    }
}
