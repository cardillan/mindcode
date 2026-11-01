package io.github.cardillan.mc.mindcode.logic.instructions;

import io.github.cardillan.mc.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicArgument;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicNumber;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicVariable;
import io.github.cardillan.mc.mindcode.logic.opcodes.InstructionParameterType;
import io.github.cardillan.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static io.github.cardillan.mc.mindcode.logic.arguments.Operation.SUB;

@NullMarked
public class PopInstruction extends PushOrPopInstruction {

    PopInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.POP, args, params);
    }

    protected PopInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public PopInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new PopInstruction(this, astContext);
    }

    @Override
    public int getSharedSize(@Nullable Map<String, Integer> sharedStructures) {
        return externalStack() ? super.getSharedSize(sharedStructures) : 0;
    }

    @Override
    public void resolve(InstructionProcessor processor, Consumer<LogicInstruction> consumer) {
        LocalContextfulInstructionsCreator creator = creator(processor, consumer);

        assert stackTracker != null;
        assert nameCreator != null;
        LogicVariable stackPointer = stackTracker.getStackPointer();
        LogicVariable stackMemory = stackTracker.getStackMemory();
        boolean externalStack = stackTracker.externalStack();

        if (externalStack) {
            creator.createOp(SUB, stackPointer, stackPointer, LogicNumber.ONE);
            creator.createRead(getVariable(), stackMemory, stackPointer).copyComment(this);
        }
    }
}
