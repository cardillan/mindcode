package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

// Push and pop are always processed at the same time
public abstract class PushOrPopInstruction extends BaseInstruction {

    PushOrPopInstruction(Opcode opcode, List<LogicArgument> args, List<LogicParameter> params) {
        super(opcode, args, params);
    }

    protected PushOrPopInstruction(BaseInstruction other, String marker) {
        super(other, marker);
    }

    public final LogicVariable getMemory() {
        return (LogicVariable) getArg(0);
    }

    public final LogicVariable getVariable() {
        return (LogicVariable) getArg(1);
    }
}
