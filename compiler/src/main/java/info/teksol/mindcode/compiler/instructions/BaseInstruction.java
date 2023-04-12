package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.Opcode;

import java.util.List;
import java.util.Objects;

public class BaseInstruction implements LogicInstruction {
    private final Opcode opcode;
    private final List<String> args;

    // Used to mark instructions that belong together -- provides additional information to optimizers.
    // Marker is not considered by hashCode or equals!!
    private final String marker;

    BaseInstruction(String marker, Opcode opcode, List<String> args) {
        this.opcode = opcode;
        this.args = List.copyOf(args);
        this.marker = marker;
    }

    @Override
    public Opcode getOpcode() {
        return opcode;
    }

    @Override
    public List<String> getArgs() {
        return args;
    }

    @Override
    public String getArg(int index) {
        return args.get(index);
    }

    @Override
    public String getMarker() {
        return marker;
    }

    @Override
    public boolean matchesMarker(String marker) {
        return this.marker!= null && this.marker.equals(marker);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseInstruction that = (BaseInstruction) o;
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
