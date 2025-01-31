package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;

// Push and pop are always processed at the same time
@NullMarked
public interface PushOrPopInstruction extends LogicInstruction {

    default LogicVariable getMemory() {
        return (LogicVariable) getArg(0);
    }

    default LogicVariable getVariable() {
        return (LogicVariable) getArg(1);
    }
}
