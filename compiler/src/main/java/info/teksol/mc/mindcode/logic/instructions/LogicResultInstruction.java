package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface LogicResultInstruction extends LogicInstruction {

    LogicVariable getResult();

    LogicResultInstruction withResult(LogicVariable result);

    /// @return true if the instruction depends on the prior value of the result variable (e.g. `op add x x 1`).
    default boolean isUpdating() {
        LogicVariable result = getResult();
        return getArgs().stream().filter(result::equals).count() > 1;
    }
}
