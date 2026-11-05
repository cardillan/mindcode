package io.github.cardillan.mindcode.logic.instructions;

import io.github.cardillan.mindcode.compiler.ContextFactory;
import io.github.cardillan.mindcode.compiler.MindcodeInternalError;
import io.github.cardillan.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mindcode.logic.arguments.ArgumentType;
import io.github.cardillan.mindcode.logic.arguments.LogicArgument;
import io.github.cardillan.mindcode.logic.opcodes.InstructionParameterType;
import io.github.cardillan.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

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

    private @Nullable Boolean externalStack = null;
    protected boolean externalStack() {
        if (externalStack == null) {
            externalStack = ContextFactory.getStackContext().stackTracker().externalStack();
        }
        return externalStack;
    }

    protected LocalContextfulInstructionsCreator creator(InstructionProcessor processor, Consumer<LogicInstruction> consumer) {
        return new LocalContextfulInstructionsCreator(processor, astContext, consumer);
    }
}
