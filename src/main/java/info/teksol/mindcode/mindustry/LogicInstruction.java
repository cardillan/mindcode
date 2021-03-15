package info.teksol.mindcode.mindustry;

import java.util.List;
import java.util.Objects;

public class LogicInstruction {
    private final String opcode;
    private final List<String> args;

    public LogicInstruction(String opcode) {
        this(opcode, List.of());
    }

    public LogicInstruction(String opcode, String... args) {
        this(opcode, List.of(args));
    }

    public LogicInstruction(String opcode, List<String> args) {
        this.opcode = opcode;
        this.args = args;
    }

    public boolean isWrite() {
        return getOpcode().equals("write");
    }

    public boolean isPrint() {
        return getOpcode().equals("print");
    }

    public boolean isJump() {
        return getOpcode().equals("jump");
    }

    public boolean isSet() {
        return getOpcode().equals("set");
    }

    public boolean isOp() {
        return getOpcode().equals("op");
    }

    public String getOpcode() {
        return opcode;
    }

    public List<String> getArgs() {
        return args;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogicInstruction that = (LogicInstruction) o;
        return Objects.equals(opcode, that.opcode) &&
                Objects.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opcode, args);
    }

    @Override
    public String toString() {
        return "LogicInstruction{" +
                "opcode='" + opcode + '\'' +
                ", args=" + args +
                '}';
    }
}
