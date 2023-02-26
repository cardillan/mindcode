package info.teksol.mindcode.mindustry.instructions;

import info.teksol.mindcode.mindustry.logic.Opcode;
import java.util.List;
import java.util.Objects;

import static info.teksol.mindcode.mindustry.logic.Opcode.*;

public class LogicInstruction {
    private final Opcode opcode;
    private final List<String> args;

    LogicInstruction(Opcode opcode, List<String> args) {
        this.opcode = opcode;
        this.args = List.copyOf(args);
    }
    
    public boolean isWrite() {
        return opcode == WRITE;
    }

    public boolean isPrint() {
        return opcode == PRINT;
    }

    public boolean isJump() {
        return opcode == JUMP;
    }

    public boolean isSet() {
        return opcode == SET;
    }

    public boolean isOp() {
        return opcode == OP;
    }

    public boolean isRead() {
        return opcode == READ;
    }

    public boolean isUControl() {
        return opcode == UCONTROL;
    }

    public boolean isLabel() {
        return opcode == LABEL;
    }

    public boolean isGetlink() {
        return opcode == GETLINK;
    }

    public boolean isSensor() {
        return opcode == SENSOR;
    }

    public boolean isEnd() {
        return opcode == END;
    }

    public boolean isPush() {
        return opcode == PUSH;
    }

    public boolean isPop() {
        return opcode == POP;
    }

    public boolean isPushOrPop() {
        return opcode == PUSH || opcode == POP;
    }

    public boolean isCall() {
        return opcode == CALL;
    }

    public boolean isReturn() {
        return opcode == RETURN;
    }

    public Opcode getOpcode() {
        return opcode;
    }

    public List<String> getArgs() {
        return args;
    }

    public String getArg(int index) {
        return args.get(index);
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
