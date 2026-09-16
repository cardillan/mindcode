package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static info.teksol.mc.mindcode.logic.arguments.Operation.ADD;

@NullMarked
public class CallInstruction extends BaseInstruction implements CallingInstruction {

    CallInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.CALL, args, params);
    }

    protected CallInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public CallInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new CallInstruction(this, astContext);
    }

    public final LogicLabel getCallAddr() {
        return (LogicLabel) getArg(0);
    }

    public final LogicVariable getReturnAddr() {
        return (LogicVariable) getArg(1);
    }

    public final LogicVariable getReturnValue()  {
        return (LogicVariable) getArg(2);
    }

    public int getSharedSize(@Nullable Map<String, Integer> sharedStructures) {
        return super.getSharedSize(sharedStructures) + (astContext.getGlobalProfile().isSymbolicLabels() ? 1 : 0);
    }

    @Override
    public void resolve(InstructionProcessor processor, Consumer<LogicInstruction> consumer) {
        LocalContextfulInstructionsCreator creator = creator(processor, consumer);

        if (astContext.getGlobalProfile().isSymbolicLabels()) {
            creator.createOp(ADD, getReturnAddr(), LogicBuiltIn.COUNTER, LogicNumber.ONE);
        }
        creator.createJumpUnconditional(getCallAddr()).copyComment(this);
    }
}
