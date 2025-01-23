package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

// Push and pop are always processed at the same time
@NullMarked
public abstract class PushOrPopInstruction extends BaseInstruction {

    PushOrPopInstruction(AstContext astContext, Opcode opcode, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, opcode, args, params);
    }

    protected PushOrPopInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    public final LogicVariable getMemory() {
        return (LogicVariable) getArg(0);
    }

    public final LogicVariable getVariable() {
        return (LogicVariable) getArg(1);
    }
}
