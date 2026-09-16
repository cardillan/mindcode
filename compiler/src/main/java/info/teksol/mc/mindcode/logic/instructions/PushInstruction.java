package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.ArgumentType;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static info.teksol.mc.mindcode.logic.arguments.Operation.ADD;

@NullMarked
public class PushInstruction extends PushOrPopInstruction {

    PushInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.PUSH, args, params);
    }

    protected PushInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public PushInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new PushInstruction(this, astContext);
    }

    @Override
    public int getSharedSize(@Nullable Map<String, Integer> sharedStructures) {
        return externalStack() ? super.getSharedSize(sharedStructures)
                : getVariable().getType() == ArgumentType.FUNCTION_PARAMETER ? 1 : 0;
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
            creator.createWrite(getVariable(), stackMemory, stackPointer);
            creator.createOp(ADD, stackPointer, stackPointer, LogicNumber.ONE).copyComment(this);
        } else if (getVariable().getType() == ArgumentType.FUNCTION_PARAMETER) {
            creator.createSet(getVariable().stackFrame(nameCreator.stackFrameSuffix(0)), getVariable());
        }
    }
}
