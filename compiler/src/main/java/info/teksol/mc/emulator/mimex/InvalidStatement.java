package info.teksol.mc.emulator.mimex;

import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.List;

@NullMarked
public class InvalidStatement extends LStatement {
    private final String text;

    public InvalidStatement(String[] tokens, int tok) {
        super(false, "noop", List.of(), List.of());
        text = String.join(" ", Arrays.asList(tokens).subList(0, tok));
    }

    @Override
    public String toString() {
        return text;
    }
}
