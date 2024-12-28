package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicVariable;

public interface LogicResultInstruction extends LogicInstruction {

    LogicVariable getResult();

    LogicResultInstruction withResult(LogicVariable result);

    /// @return true if the instruction depends on the prior value of the result variable (e.g. `op add x x 1`).
    default boolean isUpdating() {
        LogicVariable result = getResult();
        return getArgs().stream().filter(result::equals).count() > 1;
    }
}
