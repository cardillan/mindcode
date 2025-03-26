package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.postprocess.LogicInstructionPrinter;
import info.teksol.mc.mindcode.logic.arguments.ArgumentType;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.mindcode.logic.opcodes.TypedArgument;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@NullMarked
public class BaseInstruction implements LogicInstruction {
    public static SideEffects NO_SIDE_EFFECTS = SideEffects.none();

    private final Opcode opcode;
    private final List<LogicArgument> args;
    private final @Nullable List<InstructionParameterType> params;
    private final @Nullable List<TypedArgument> typedArguments;
    private final int inputs;
    private final int outputs;

    // Contains the side effects of the instruction
    protected final SideEffects sideEffects;

    // Used to mark instructions with additional information to optimizers.
    // AstContext and marker are not considered by hashCode or equals!
    protected final AstContext astContext;

    BaseInstruction(AstContext astContext, Opcode opcode, List<LogicArgument> args, @Nullable List<InstructionParameterType> params,
            SideEffects sideEffects) {
        this.sideEffects = Objects.requireNonNull(sideEffects);
        this.astContext = Objects.requireNonNull(astContext);
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
        validate();
    }

    BaseInstruction(AstContext astContext, Opcode opcode, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        this(astContext, opcode, args, params, NO_SIDE_EFFECTS);
    }

    protected BaseInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        this.sideEffects = Objects.requireNonNull(sideEffects);
        this.astContext = Objects.requireNonNull(astContext);
        this.opcode = other.opcode;
        this.args = other.args;
        this.params = other.params;
        this.typedArguments = other.typedArguments;
        this.inputs = other.inputs;
        this.outputs = other.outputs;
        validate();
    }

    protected void validate() {
        if (typedArguments != null) {
            typedArguments.forEach(arg -> {
                if (arg.isOutput() && !arg.argument().isWritable() && arg.argument().getType() != ArgumentType.UNSPECIFIED) {
                    throw new MindcodeInternalError("Argument " + arg.argument().toMlog() + " is not writable in " + toMlog());
                }
            });
        }
    }

    public AstContext getAstContext() {
        return astContext;
    }

    @Override
    public BaseInstruction copy() {
        return new BaseInstruction(this, astContext, sideEffects);
    }

    @Override
    public BaseInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new BaseInstruction(this, astContext, sideEffects);
    }

    @Override
    public LogicInstruction withSideEffects(SideEffects sideEffects) {
        return this.sideEffects == sideEffects ? this : new BaseInstruction(this, astContext, sideEffects);
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
    public SideEffects sideEffects() {
        return sideEffects;
    }

    private final BitSet contextMap = new BitSet();
    private int topContext = 0;

    @Override
    public boolean belongsTo(@Nullable AstContext astContext) {
        if (false) {
            if (topContext < AstContext.getCurrentCounter()) {
                topContext = AstContext.getCurrentCounter();
                contextMap.clear();
                for (AstContext current = this.astContext; current != null; current = current.parent()) {
                    contextMap.set(current.id);
                }
            }

            return astContext != null && contextMap.get(astContext.id);
        } else {
            return this.astContext.belongsTo(astContext);
        }
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
        return LogicInstructionPrinter.toStringSimple(this);
    }
}
