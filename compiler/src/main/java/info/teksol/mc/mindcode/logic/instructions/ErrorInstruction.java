package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicNull;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@NullMarked
public class ErrorInstruction extends BaseInstruction {

    public ErrorInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.ERROR, args, params);
    }

    public ErrorInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    protected void validate() {
        // Do nothing
    }

    @Override
    public boolean endsCodePath() {
        return true;
    }

    @Override
    public ErrorInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new ErrorInstruction(this, astContext);
    }

    @Override
    public int getSharedSize(@Nullable Map<String, Integer> sharedStructures) {
        return switch (getLocalProfile().getErrorReporting()) {
            case NONE -> 0;
            case ASSERT -> 1;
            case MINIMAL, SIMPLE, DESCRIBED -> args.size() + 1;
        };
    }

    @Override
    public void resolve(InstructionProcessor processor, Consumer<LogicInstruction> consumer) {
        LocalContextfulInstructionsCreator creator = creator(processor, consumer);

        switch (getLocalProfile().getErrorReporting()) {
            case NONE -> {}
            case ASSERT -> {
                if (getArgs().size() == 10) {
                    consumer.accept(this);
                } else {
                    ArrayList<LogicArgument> messages = new ArrayList<>(getArgs());
                    messages.addAll(Collections.nCopies(10 - getArgs().size(), LogicNull.NULL));
                    creator.createError(messages);
                }
            }
            case MINIMAL, SIMPLE, DESCRIBED -> {
                for (int index = 0; index < getArgs().size(); index++) {
                    creator.createSet(LogicVariable.error(index), (LogicValue) getArg(index));
                }
                creator.createStop();
            }
        }
    }
}
