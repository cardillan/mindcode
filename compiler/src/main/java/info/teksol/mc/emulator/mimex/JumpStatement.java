package info.teksol.mc.emulator.mimex;

import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class JumpStatement extends LStatement {
    public int destIndex;

    public JumpStatement(boolean privileged, String opcode, List<String> types, List<String> arguments) {
        super(privileged, opcode, types, arguments);

        destIndex = Integer.parseInt(arguments.getFirst());
    }

    @Override
    public String toString() {
        return opcode() + " " + destIndex + " " + String.join(" ", arguments().subList(1, arguments().size()));
    }
}
