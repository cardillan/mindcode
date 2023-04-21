package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.logic.ArgumentType;
import info.teksol.mindcode.logic.LogicString;
import info.teksol.mindcode.logic.Opcode;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple optimizer which merges together print instructions with string literal arguments.
 * The print instructions will get merged even if they aren't consecutive, assuming there aren't instructions
 * that could break the print sequence ({@code jump}, {@code label} or {@code print [variable]}).
 * Typical sequence of instructions targeted by this optimizer is:
 * <pre>{@code
 * print count
 * print "\n"
 * op div ratio count total
 * op mul ratio ratio 100
 * print "Used: "
 * print ratio
 * print "%"
 * }</pre>
 * Which will get turned to
 * <pre>{@code
 * print count
 * op div ratio count total
 * op mul ratio ratio 100
 * print "\nUsed: "
 * print ratio
 * print "%"
 * }</pre>
 */
class PrintMerger extends PipelinedOptimizer {
    public PrintMerger(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        super(instructionProcessor, next);
    }

    @Override
    protected State initialState() {
        return new EmptyState();
    }

    private final class EmptyState implements State {
        @Override
        public State emit(LogicInstruction instruction) {
            if (instruction instanceof PrintInstruction ix && ix.getValue().getType() == ArgumentType.STRING_LITERAL) {
                return new ExpectPrint(ix);
            } else {
                emitToNext(instruction);
                return this;
            }
        }

        @Override
        public State flush() {
            return this;
        }
    }

    private final class ExpectPrint implements State {
        private final PrintInstruction firstPrint;
        private final List<LogicInstruction> operations = new ArrayList<>();

        private ExpectPrint(PrintInstruction instruction) {
            firstPrint = instruction;
        }

        @Override
        public State emit(LogicInstruction instr) {
            // Do not merge across jumps, labels and printflushes
            // Function calls generate a label, so they prevent merging as well
            if (instr instanceof JumpInstruction || instr instanceof LabelInstruction || instr instanceof PrintflushInstruction) {
                operations.add(instr);
                return flush();
            }

            if (instr instanceof PrintInstruction ix) {
                // Only merge string literals
                if (ix.getValue().getType() == ArgumentType.STRING_LITERAL) {
                    PrintInstruction merged = merge(firstPrint, ix);
                    if (merged != null) {
                        operations.forEach(PrintMerger.this::emitToNext);
                        // We can merge the merged instruction with next one as well
                        return new ExpectPrint(merged);
                    } else {
                        // We didn't merge for whatever reason.
                        // Try to merge current instruction with the next one
                        flush();
                        return new ExpectPrint(ix);
                    }
                }

                // Any other print breaks the sequence and cannot be merged
                operations.add(instr);
                return flush();
            }

            // Continue merging
            operations.add(instr);
            return this;
        }

        // Creates a merged instruction from two constant prints
        // If merge is not possible (the string constants are malformed), returns null.
        // Only checks for quotes on both ends of the string, doesn't check for proper quote escaping
        private PrintInstruction merge(PrintInstruction first, PrintInstruction second) {
            final String q = "\"";
            LogicString str1 = (LogicString) first.getValue();
            LogicString str2 = (LogicString) second.getValue();
            // Do not merge strings if the length is over 34, unless aggressive
            if ((str1.length() + str2.length() <= 34 || level == OptimizationLevel.AGGRESSIVE)) {
                return (PrintInstruction) createInstruction(Opcode.PRINT, LogicString.concat(str1, str2));
            }

            return null;
        }

        @Override
        public State flush() {
            emitToNext(firstPrint);
            operations.forEach(PrintMerger.this::emitToNext);
            return new EmptyState();
        }
    }
}
