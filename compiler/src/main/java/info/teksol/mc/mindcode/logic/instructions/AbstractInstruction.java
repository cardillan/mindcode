package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.postprocess.LogicInstructionPrinter;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.TypedArgument;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@NullMarked
public abstract class AbstractInstruction implements LogicInstruction {
    protected final List<LogicArgument> args;
    protected final @Nullable List<InstructionParameterType> params;
    protected final @Nullable List<TypedArgument> typedArguments;
    protected final int inputs;
    protected final int outputs;

    // Used to mark instructions with additional information to optimizers.
    // AstContext and marker are not considered by hashCode or equals!
    protected final AstContext astContext;

    // Instruction info
    protected final EnumMap<InstructionInfo, Object> info = new EnumMap<>(InstructionInfo.class);

    protected AbstractInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        this.astContext = Objects.requireNonNull(astContext);
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

    protected AbstractInstruction(AbstractInstruction other, AstContext astContext) {
        this.astContext = Objects.requireNonNull(astContext);
        this.args = other.args;
        this.params = other.params;
        this.typedArguments = other.typedArguments;
        this.inputs = other.inputs;
        this.outputs = other.outputs;
        this.info.putAll(other.info);
    }

    public AstContext getAstContext() {
        return astContext;
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

    public @Nullable AstContext findTopContextOfTypes(AstContextType... contextTypes) {
        return astContext.findTopContextOfTypes(contextTypes);
    }

    @Override
    public Object getInfo(InstructionInfo instructionInfo) {
        return info.getOrDefault(instructionInfo, instructionInfo.getDefaultValue());
    }

    @Override
    public LogicInstruction setInfo(InstructionInfo instructionInfo, Object value) {
        info.put(instructionInfo, value);
        return this;
    }

    @Override
    public LogicInstruction copyInfo(LogicInstruction other) {
        info.clear();
        info.putAll(((AbstractInstruction) other).info);
        return this;
    }

    @Override
    public String toString() {
        return LogicInstructionPrinter.toStringSimple(this);
    }
}
