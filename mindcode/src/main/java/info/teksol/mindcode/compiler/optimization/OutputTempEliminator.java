package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.PushOrPopInstruction;
import info.teksol.mindcode.compiler.instructions.SetInstruction;
import info.teksol.mindcode.logic.ArgumentType;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.ParameterAssignment;

import java.util.List;

/**
 * Generic optimizer to remove all assignments to temporary variables that carry over the output value
 * of the preceding instruction. The {@code set} instruction is removed, while the preceding instruction is updated
 * to replace the temp variable with the target variable used in the {@code set} instruction.
 * The optimization is performed only when the following conditions are met:
 * <ol>
 * <li>The set instruction assigns from a {@code __tmp} variable.</li>
 * <li>The {@code __tmp} variable is used in exactly one other instruction, which immediately precedes
 * the instruction producing the {@code __tmp} variable</li>
 * <li>All arguments of the other instruction referencing the {@code __tmp} variable are output ones.</li>
 * </ol>
 * This optimizer ignores push and pop instructions. The StackUsageOptimizer will remove push/pop instructions of any
 * eliminated variables later on.
 */
class OutputTempEliminator extends GlobalOptimizer {
    public OutputTempEliminator(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        super(instructionProcessor, next);
    }

    @Override
    protected boolean optimizeProgram() {
        try (LogicIterator itCurr = createIterator(); LogicIterator itPrev = createIterator()) {
            if (itCurr.hasNext()) {
                itCurr.next(); // Skip first
            }

            while (itCurr.hasNext()) {
                LogicInstruction current = itCurr.next();
                LogicInstruction previous = itPrev.next();
                if (current instanceof SetInstruction ix && ix.getValue().getType() == ArgumentType.TMP_VARIABLE) {
                    LogicArgument value = ix.getValue();
                    List<LogicInstruction> list = findInstructions(
                            in -> in.getArgs().contains(value) && !(in instanceof PushOrPopInstruction));

                    // Not exactly two instructions, or the previous instruction doesn't produce the tmp variable
                    if (list.size() == 2 && list.get(0) == previous) {
                        // Make sure all arg1 arguments of the other instruction are output
                        boolean replacesOutputArg = previous.assignmentsStream()
                                .filter(t -> t.argument().equals(value))
                                .allMatch(ParameterAssignment::isOutput);

                        if (replacesOutputArg) {
                            // The current instruction merely transfers a value from the output argument of the previous instruction
                            // Replacing those arguments with target of the set instruction
                            itPrev.set(replaceAllArgs(previous, value, ix.getTarget()));
                            itCurr.remove();

                            // We just removed instruction *after* itPref cursor, but we need itPref to sync with itCurr
                            // which got its cursor decreased.
                            itPrev.previous();
                        }
                    }
                }
            }
        }

        try (LogicIterator itCurr = createIterator(); LogicIterator itPrev = createIterator()) {
            if (itCurr.hasNext()) {
                itCurr.next(); // Skip first
            }

            while (itCurr.hasNext()) {
                LogicInstruction current = itCurr.next();
                LogicInstruction previous = itPrev.next();
                if (current instanceof SetInstruction ix && ix.getValue().getType() == ArgumentType.TMP_VARIABLE) {
                    LogicArgument value = ix.getValue();
                    List<LogicInstruction> list = findInstructions(
                            in -> in.getArgs().contains(value) && !(in instanceof PushOrPopInstruction));

                    // Not exactly two instructions, or the previous instruction doesn't produce the tmp variable
                    if (list.size() == 2 && list.get(0) == previous) {
                        // Make sure all arg1 arguments of the other instruction are output
                        boolean replacesOutputArg = previous.assignmentsStream()
                                .filter(t -> t.argument().equals(value))
                                .allMatch(ParameterAssignment::isOutput);

                        if (replacesOutputArg) {
                            // The current instruction merely transfers a value from the output argument of the previous instruction
                            // Replacing those arguments with target of the set instruction
                            itPrev.set(replaceAllArgs(previous, value, ix.getTarget()));
                            itCurr.remove();

                            // We just removed instruction *after* itPref cursor, but we need itPref to sync with itCurr
                            // which got its cursor decreased.
                            itPrev.previous();
                        }
                    }
                }
            }
        }

        return false;
    }
}
