package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.postprocess.JumpTable;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.SideEffects;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

@NullMarked
public interface ArrayConstructor {

    SideEffects createSideEffects();

    int getInstructionSize(@Nullable Map<String, Integer> sharedStructures);

    double getExecutionSteps();

    String getJumpTableId();

    void expandInstruction(Consumer<LogicInstruction> consumer, Map<String, JumpTable> jumpTables);

    default void generateJumpTable(Map<String, JumpTable> jumpTables) {
        // Most implementations don't need a jump table.
    }

    /// Only for compact array constructors: returns the name of the element variable.
    default LogicVariable getElementNameVariable() {
        throw new UnsupportedOperationException("Not supported for class " + getClass().getName());
    }
}
