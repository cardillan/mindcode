package info.teksol.mc.mindcode.compiler;

import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/// Provides methods to count instruction sizes.
@NullMarked
public class InstructionCounter {

    public static int computeSizeShared(List<LogicInstruction> program) {
        Map<String, Integer> sharedStructures = new HashMap<>();
        int size = program.stream().mapToInt(ix -> ix.getRealSize(sharedStructures)).sum();
        return size + sharedStructures.values().stream().mapToInt(Integer::intValue).sum();
    }

    public static int computeSize(Stream<LogicInstruction> program) {
        return program.mapToInt(ix -> ix.getRealSize(null)).sum();
    }

    public static int computeSize(List<LogicInstruction> program) {
        return program.stream().mapToInt(ix -> ix.getRealSize(null)).sum();
    }
}
