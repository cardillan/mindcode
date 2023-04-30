package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.Opcode;
import info.teksol.mindcode.logic.ParameterAssignment;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BaseInstruction implements LogicInstruction {
    private final Opcode opcode;
    private final List<LogicArgument> args;
    private final List<LogicParameter> params;
    private final List<ParameterAssignment> assignments;
    private final int inputs;
    private final int outputs;

    // Used to mark instructions with additional information to optimizers.
    // Marker is not considered by hashCode or equals!
    protected final String marker;

    BaseInstruction(Opcode opcode, List<LogicArgument> args, List<LogicParameter> params) {
        this.opcode = opcode;
        this.args = List.copyOf(args);
        this.marker = null;
        this.params = params;
        if (params == null) {
            assignments = null;
            inputs = -1;
            outputs = -1;
        } else {
            assignments = IntStream.range(0, params.size()).mapToObj(i -> new ParameterAssignment(params.get(i), args.get(i))).toList();
            inputs = (int) params.stream().filter(LogicParameter::isInput).count();
            outputs = (int) params.stream().filter(LogicParameter::isOutput).count();
        }
    }

    protected BaseInstruction(BaseInstruction other, String marker) {
        this.opcode = other.opcode;
        this.args = other.args;
        this.marker = marker;
        this.params = other.params;
        this.assignments = other.assignments;
        this.inputs = other.inputs;
        this.outputs = other.outputs;
    }

    @Override
    public BaseInstruction withMarker(String marker) {
        return new BaseInstruction(this, marker);
    }

    @Override
    public Opcode getOpcode() {
        return opcode;
    }

    @Override
    public List<LogicArgument> getArgs() {
        return args;
    }

    @Override
    public LogicArgument getArg(int index) {
        return args.get(index);
    }

    @Override
    public List<LogicParameter> getParams() {
        return params;
    }

    @Override
    public LogicParameter getParam(int index) {
        return params.get(index);
    }

    public List<ParameterAssignment> getAssignments() {
        return assignments;
    }

    @Override
    public Stream<ParameterAssignment> assignmentsStream() {
        return assignments.stream();
    }

    @Override
    public Stream<LogicArgument> inputArgumentsStream() {
        return assignments.stream().filter(ParameterAssignment::isInput).map(ParameterAssignment::argument);
    }

    @Override
    public Stream<LogicArgument> outputArgumentsStream() {
        return assignments.stream().filter(ParameterAssignment::isOutput).map(ParameterAssignment::argument);
    }

    @Override
    public Stream<LogicArgument> inputOutputArgumentsStream() {
        return assignments.stream().filter(ParameterAssignment::isInputOrOutput).map(ParameterAssignment::argument);
    }

    @Override
    public int getInputs() {
        return inputs;
    }

    @Override
    public int getOutputs() {
        return outputs;
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
