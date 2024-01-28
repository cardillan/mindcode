package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.generator.AstContextType;
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
    // AstContext and marker are not considered by hashCode or equals!
    protected final AstContext astContext;

    BaseInstruction(AstContext astContext, Opcode opcode, List<LogicArgument> args, List<LogicParameter> params) {
        this.astContext = Objects.requireNonNull(astContext);
        this.opcode = Objects.requireNonNull(opcode);
        this.args = List.copyOf(args);
        this.params = params;
        if (params == null) {
            assignments = null;
            inputs = -1;
            outputs = -1;
        } else {
            int count = Math.min(params.size(), args.size());
            assignments = IntStream.range(0, count).mapToObj(i -> new ParameterAssignment(params.get(i), args.get(i))).toList();
            inputs = (int) params.stream().filter(LogicParameter::isInput).count();
            outputs = (int) params.stream().filter(LogicParameter::isOutput).count();
        }
    }

    protected BaseInstruction(BaseInstruction other, AstContext astContext) {
        this.astContext = Objects.requireNonNull(astContext);
        this.opcode = other.opcode;
        this.args = other.args;
        this.params = other.params;
        this.assignments = other.assignments;
        this.inputs = other.inputs;
        this.outputs = other.outputs;
    }

    public AstContext getAstContext() {
        return astContext;
    }

    @Override
    public BaseInstruction copy() {
        return new BaseInstruction(this, astContext);
    }

    @Override
    public BaseInstruction withContext(AstContext astContext) {
        return Objects.equals(this.astContext, astContext) ? this : new BaseInstruction(this, astContext);
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
    public boolean belongsTo(AstContext astContext) {
        return this.astContext.belongsTo(astContext);
    }

    public AstContext findContextOfType(AstContextType contextType) {
        return astContext.findContextOfType(contextType);
    }

    public AstContext findTopContextOfType(AstContextType contextType) {
        return astContext.findTopContextOfType(contextType);
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
                "astContext.id: " + astContext.id +
                ", opcode='" + opcode + '\'' +
                ", args=" + args +
                '}';
    }
}
