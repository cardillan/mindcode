package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.SideEffects;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@NullMarked
public interface ArrayConstructor {

    SideEffects createSideEffects(AccessType accessType);

    int getInstructionSize(AccessType accessType, @Nullable Map<String, Integer> sharedStructures);

    String getJumpTableId(AccessType accessType);

    void generateJumpTable(AccessType accessType, Map<String, List<LogicInstruction>> jumpTables);

    void expandInstruction(Consumer<LogicInstruction> consumer, Map<String, List<LogicInstruction>> jumpTables);

    enum AccessType {
        READ, WRITE
    }
}
