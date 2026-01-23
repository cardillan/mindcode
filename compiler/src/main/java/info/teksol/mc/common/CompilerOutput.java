package info.teksol.mc.common;

import info.teksol.mc.emulator.Emulator;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public record CompilerOutput<T>(
        @Nullable T output,
        String fileName,
        @Nullable Emulator emulator
) {
    public static final CompilerOutput<?> EMPTY = new CompilerOutput<>();

    @SuppressWarnings("unchecked")
    public static <T> CompilerOutput<T> empty() {
        return (CompilerOutput<T>) EMPTY;
    }

    public CompilerOutput(T output, String fileName) {
        this(output, fileName, null);
    }

    public CompilerOutput(T output) {
        this(output, "");
    }

    private CompilerOutput() {
        this(null, "", null);
    }

    public <R> CompilerOutput<R> withOutput(R output) {
        return new CompilerOutput<>(output, fileName, emulator);
    }

    public String getStringOutput() {
        return output == null ? "" : output.toString();
    }

    public T existingOutput() {
        return Objects.requireNonNull(output);
    }
}
