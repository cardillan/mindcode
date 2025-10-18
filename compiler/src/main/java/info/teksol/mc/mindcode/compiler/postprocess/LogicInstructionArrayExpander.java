package info.teksol.mc.mindcode.compiler.postprocess;

import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/// Expands virtual instructions (READARR, WRITEARR) into real code.
@NullMarked
public class LogicInstructionArrayExpander {
    private boolean expanded = false;
    private final Map<String, JumpTable> jumpTables = new TreeMap<>();

    public List<LogicInstruction> expandArrayInstructions(List<LogicInstruction> program) {
        // Already expanded, do not repeat
        if (expanded) return program;

        analyze(program);

        List<LogicInstruction> expanded = program.stream()
                .mapMulti(this::expandInstruction)
                .collect(Collectors.toCollection(ArrayList::new));

        int skip = expanded.getLast().getOpcode() == Opcode.END ? 1 : 0;
        jumpTables.values().stream()
                .map(JumpTable::instructions)
                .flatMap(Collection::stream)
                .skip(skip)
                .forEach(expanded::add);

        return expanded;
    }

    public boolean analyze(List<LogicInstruction> program) {
        if (expanded) return false;
        expanded = true;

        // Subarray support (separate processing for read/write):
        // 1. Gather all array access instructions
        // 2. Group by array name, find min/max index per array
        // 3. Build the jump table for the resulting range, remember to adjust offset

        boolean found = false;
        for (LogicInstruction instruction : program) {
            if (instruction instanceof ArrayAccessInstruction ix) {
                ix.getArrayConstructor().generateJumpTable(jumpTables);
                found = true;
            }
        }

        return found;
    }

    public List<LogicInstruction> getJumpTables(boolean generateEndSeparator) {
        return jumpTables.values().stream()
                .map(JumpTable::instructions)
                .flatMap(Collection::stream)
                .skip(generateEndSeparator ? 1 : 0)
                .collect(Collectors.toList());
    }

    public List<LogicInstruction> expand(ArrayAccessInstruction ix) {
        List<LogicInstruction> result = new ArrayList<>();
        ix.getArrayConstructor().expandInstruction(result::add, jumpTables);
        return result;
    }

    private void expandInstruction(LogicInstruction instruction, Consumer<LogicInstruction> consumer) {
        if (instruction instanceof ArrayAccessInstruction ix) {
            ix.getArrayConstructor().expandInstruction(consumer, jumpTables);
        } else {
            consumer.accept(instruction);
        }
    }
}
