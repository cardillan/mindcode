package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.mindcode.logic.opcodes.TypedArgument;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@NullMarked
public class CustomInstruction implements LogicInstruction {
    private final boolean safe;
    private final String opcode;
    private final List<LogicArgument> args;
    private final @Nullable List<InstructionParameterType> params;
    private final @Nullable List<TypedArgument> typedArguments;
    private final int inputs;
    private final int outputs;

    // Used to mark instructions with additional information to optimizers.
    // AstContext and marker are not considered by hashCode or equals!
    protected final AstContext astContext;

    public CustomInstruction(AstContext astContext, boolean safe, String opcode, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        this.astContext = Objects.requireNonNull(astContext);
        this.safe = safe;
        this.opcode = Objects.requireNonNull(opcode);
        this.args = List.copyOf(args);
        this.params = params;
        if (params == null) {
            typedArguments = null;
            inputs = -1;
            outputs = -1;
        } else {
            int count = Math.min(params.size(), args.size());
            typedArguments = IntStream.range(0, count).mapToObj(i -> new TypedArgument(params.get(i), args.get(i))).toList();
            inputs = (int) params.stream().filter(InstructionParameterType::isInput).count();
            outputs = (int) params.stream().filter(InstructionParameterType::isOutput).count();
        }
    }

    protected CustomInstruction(CustomInstruction other, AstContext astContext) {
        this.astContext = Objects.requireNonNull(astContext);
        this.safe = other.safe;
        this.opcode = other.opcode;
        this.args = other.args;
        this.params = other.params;
        this.typedArguments = other.typedArguments;
        this.inputs = other.inputs;
        this.outputs = other.outputs;
    }

    public AstContext getAstContext() {
        return astContext;
    }

    @Override
    public CustomInstruction copy() {
        return new CustomInstruction(this, astContext);
    }

    @Override
    public CustomInstruction withContext(AstContext astContext) {
        return Objects.equals(this.astContext, astContext) ? this : new CustomInstruction(this, astContext);
    }

    @Override
    public Opcode getOpcode() {
        return Opcode.CUSTOM;
    }

    @Override
    public String getMlogOpcode() {
        return opcode;
    }

    @Override
    public boolean isSafe() {
        return safe;
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
    public @Nullable List<InstructionParameterType> getArgumentTypes() {
        return params;
    }

    @Override
    public InstructionParameterType getArgumentType(int index) {
        return Objects.requireNonNull(params).get(index);
    }

    public List<TypedArgument> getTypedArguments() {
        return Objects.requireNonNull(typedArguments);
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
    public boolean belongsTo(@Nullable AstContext astContext) {
        return this.astContext.belongsTo(astContext);
    }

    public @Nullable AstContext findContextOfType(AstContextType contextType) {
        return astContext.findContextOfType(contextType);
    }

    public @Nullable AstContext findTopContextOfType(AstContextType contextType) {
        return astContext.findTopContextOfType(contextType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomInstruction that = (CustomInstruction) o;
        return Objects.equals(opcode, that.opcode) &&
                Objects.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opcode, args);
    }

    @Override
    public String toString() {
        return "CustomInstruction{" +
                "astContext.id: " + astContext.id +
                ", opcode='" + opcode + '\'' +
                ", args=" + args +
                '}';
    }
}
