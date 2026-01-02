package info.teksol.mc.emulator.v2;

import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class JumpStatement extends LStatement {
    int destIndex;

    public JumpStatement(boolean privileged, String opcode, List<String> types, List<String> arguments) {
        super(privileged, opcode, types, arguments);
    }
}
