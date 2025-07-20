package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.ArgumentType;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
public class BaseInstruction extends AbstractInstruction {
    private final Opcode opcode;

    BaseInstruction(AstContext astContext, Opcode opcode, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, args, params);
        this.opcode = Objects.requireNonNull(opcode);
        validate();
    }

    protected BaseInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
        this.opcode = other.opcode;
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

    @Override
    public BaseInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new BaseInstruction(this, astContext).copyInfo(this);
    }

    @Override
    public Opcode getOpcode() {
        return opcode;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseInstruction that = (BaseInstruction) o;
        return Objects.equals(opcode, that.opcode) &&
                Objects.equals(args, that.args) &&
                info.equals(that.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opcode, args);
    }
}
