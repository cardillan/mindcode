package info.teksol.mc.mindcode.compiler;

import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

/// Provides methods to count instruction sizes.
@NullMarked
public class InstructionCounter {

    /// Computes the global size of the entire program, including a not-yet-generated code required by some
    /// virtual instructions (e.g., jump tables for internal arrays). These structures may be shared between
    /// several instructions; this is accounted for.
    ///
    /// @param program the entire program to compute the size of
    /// @return total number of mlog instructions represented by this program
    public static int globalSize(List<LogicInstruction> program) {
        Map<String, Integer> sharedStructures = new HashMap<>();
        int size = program.stream().mapToInt(ix -> ix.getRealSize(sharedStructures)).sum();

        final int variableCreationSize;
        Set<LogicVariable> forcedVariables = new HashSet<>(MindcodeCompiler.getContext().getForcedVariables());
        program.forEach(ix -> forcedVariables.addAll(ix.getIndirectVariables()));
        if (!forcedVariables.isEmpty()) {
            program.forEach(ix -> ix.getTypedArguments().forEach(a -> {
                if (a.argument() instanceof LogicVariable variable) {
                    forcedVariables.remove(variable);
                }
            }));
            variableCreationSize = 1 + (forcedVariables.size() + 5) / 6;
        } else {
            variableCreationSize = 0;
        }

        return size + variableCreationSize + sharedStructures.values().stream().mapToInt(Integer::intValue).sum();
    }

    /// Computes the local size of the instructions in the given stream, using the provided function to estimate
    /// the real instruction size. The additional code required by some instructions isn't taken into account in
    /// this computation. The computation is suitable for measuring the effect of removing or duplicating an
    /// instruction. Computation is not precise when a last instruction of a given kind is being removed from
    /// the program, as the size of a related shared structure (e.g., a jump table) is not considered.
    ///
    /// @param instructions stream of instructions to compute the size of
    /// @return total number of mlog instructions represented by this program
    public static int localSize(Stream<LogicInstruction> instructions, ToIntFunction<LogicInstruction> sizeEvaluator) {
        return instructions.mapToInt(sizeEvaluator).sum();
    }

    /// Computes the local size of the instructions in the given stream. The additional code required by some
    /// instructions isn't taken into account in this computation. The computation is suitable for measuring
    ///  the effect of removing or duplicating an instruction. Computation is not precise when a last instruction
    /// of a given kind is being removed from the program, as the size of a related shared structure
    /// (e.g., a jump table) is not considered.
    ///
    /// @param instructions stream of instructions to compute the size of
    /// @return total number of mlog instructions represented by this program
    public static int localSize(Stream<LogicInstruction> instructions) {
        return localSize(instructions, ix -> ix.getRealSize(null));
    }

    /// Computes the local size of the instructions in the given list. See [#localSize(Stream, ToIntFunction<LogicInstruction>)].
    ///
    /// @param instructions list of instructions to compute the size of
    /// @return total number of mlog instructions represented by this program
    public static int localSize(List<LogicInstruction> instructions, ToIntFunction<LogicInstruction> sizeEvaluator) {
        return localSize(instructions.stream(), sizeEvaluator);
    }

    /// Computes the local size of the instructions in the given list. See [#localSize(Stream)].
    ///
    /// @param instructions list of instructions to compute the size of
    /// @return total number of mlog instructions represented by this program
    public static int localSize(List<LogicInstruction> instructions) {
        return localSize(instructions.stream());
    }
}
