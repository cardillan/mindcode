package info.teksol.mc.emulator.v2;

import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class InvalidStatement extends LStatement {

    public InvalidStatement() {
        super(false, "noop", List.of(), List.of());
    }
}
