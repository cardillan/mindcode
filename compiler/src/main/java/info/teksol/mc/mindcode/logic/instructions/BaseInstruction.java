package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.logic.arguments.ArgumentType;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.mindcode.logic.opcodes.TypedArgument;

import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class BaseInstruction implements LogicInstruction {
    private final Opcode opcode;
    private final List<LogicArgument> args;
    private final List<InstructionParameterType> params;
    private final List<TypedArgument> typedArguments;
    private final int inputs;
    private final int outputs;

    // Used to mark instructions with additional information to optimizers.
    // AstContext and marker are not considered by hashCode or equals!
    protected final AstContext astContext;

    BaseInstruction(AstContext astContext, Opcode opcode, List<LogicArgument> args, List<InstructionParameterType> params) {
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

    protected BaseInstruction(BaseInstruction other, AstContext astContext) {
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
        if (params != null) {
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
    public List<InstructionParameterType> getArgumentTypes() {
        return params;
    }

    @Override
    public InstructionParameterType getArgumentType(int index) {
        return params.get(index);
    }

    public List<TypedArgument> getTypedArguments() {
        return typedArguments;
    }

    @Override
    public int getInputs() {
        return inputs;
    }

    @Override
    public int getOutputs() {
        return outputs;
    }

    private final BitSet contextMap = new BitSet();
    private int topContext = 0;

    @Override
    public boolean belongsTo(AstContext astContext) {
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
        return getClass().getSimpleName() + "{" +
                "astContext.id: " + astContext.id +
                ", opcode='" + opcode + '\'' +
                ", args=" + args +
                '}';
    }
}