package info.teksol.mindcode.mindustry.instructions;

import info.teksol.mindcode.mindustry.logic.Opcode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static info.teksol.mindcode.mindustry.logic.Opcode.*;

public class LogicInstruction {
    private final Opcode opcode;
    private final List<String> args;

    public LogicInstruction(Opcode opcode) {
        this(opcode, List.of());
        validate();
    }

    public LogicInstruction(Opcode opcode, String... args) {
        this(opcode, List.of(args));
        validate();
    }

    public LogicInstruction(Opcode opcode, List<String> args) {
        this.opcode = opcode;
        this.args = List.copyOf(args);
        validate();
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

    public Opcode getOpcode() {
        return opcode;
    }

    public List<String> getArgs() {
        return args;
    }

    /**
     * @return stream of instruction arguments together with type information
     */
    public Stream<TypedArgument> getTypedArguments() {
        return opcode.getTypedArguments(args);
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
    
    /**
     * Validates the instruction arguments (their count and applicability to argument types)
     * against argument list of the instruction's Opcode.
     */
    private void validate() {
        int expectedArgs = opcode.getArgumentTypes(args).size();
        if (args.size() > expectedArgs) {
            throw new InvalidInstructionException("Too many arguments of instruction " + opcode +
                    " (expected " + expectedArgs + "). " + toString());
        }
        
        Optional<TypedArgument> wrongArgument = getTypedArguments()
                .filter(a -> !a.getArgumentType().isCompatible(a.getValue())).findAny();
        
        if (wrongArgument.isPresent()) {
            throw new InvalidInstructionException("Argument " + wrongArgument.get().getValue() + 
                    " not compatible with argument type " + wrongArgument.get().getArgumentType() + ". " + toString());
        }
    }
}
