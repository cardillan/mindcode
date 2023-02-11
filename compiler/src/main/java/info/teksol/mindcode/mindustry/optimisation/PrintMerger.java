package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.ArrayList;
import java.util.List;

// A simple optimizer which merges together print instructions with string literal arguments.
// The print instructions will get merged even if they aren't consecutive, assuming there aren't instructions
// that could break the print sequence (jump, label or print [variable]).
// Typical sequence of instructions targeted by this optimizer is:
//
// print count
// print "\n"
// op div ratio count total
// op mul ratio ratio 100
// print "Used: "
// print ratio
// print "%"
//
// Which will get turned to
//
// print count
// op div ratio count total
// op mul ratio ratio 100
// print "\nUsed: "
// print ratio
// print "%"
//
class PrintMerger extends PipelinedOptimizer {
    public PrintMerger(LogicInstructionPipeline next) {
        super(next);
    }
    
    @Override
    protected State initialState() {
        return new EmptyState();
    }

    private final class EmptyState implements State {
        @Override
        public State emit(LogicInstruction instruction) {
            if (instruction.isPrint() && instruction.getArgs().get(0).startsWith("\"")) {
                return new ExpectPrint(instruction);
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
        private final LogicInstruction firstPrint;
        private List<LogicInstruction> operations = new ArrayList<>();
        
        private ExpectPrint(LogicInstruction instruction) {
            firstPrint = instruction;
        }
        
        @Override
        public State emit(LogicInstruction instruction) {
            // Do not merge across jumps and labela
            // Function calls generate a label, so they prevent merging as well
            if (instruction.isJump() || instruction.isLabel()) {
                operations.add(instruction);
                return flush();
            }
            
            if (instruction.isPrint()) {
                // Only merge string literals
                if (instruction.getArgs().get(0).startsWith("\"")) {
                    LogicInstruction merged = merge(firstPrint, instruction);
                    if (merged != null) {
                        operations.forEach(PrintMerger.this::emitToNext);
                        // We can merge the merged instruction with next one as well
                        return new ExpectPrint(merged);
                    } else {
                        // We didn't merge for whatever reason.
                        // Try to merge current instruction with the next one
                        flush();
                        return new ExpectPrint(instruction);
                    }
                }
                
                // Any other print breaks the sequence and cannot be merged
                operations.add(instruction);
                return flush();
            }
            
            operations.add(instruction);
            return this;
        }

        // Creates a merged instruction from two constant prints
        // If merge is not possible (the string constants are malformed), returns null.
        // Only checks for quotes on both ends of the string, doesn't check for proper quote escaping
        private LogicInstruction merge(LogicInstruction first, LogicInstruction second) {
            String str1 = first.getArgs().get(0);
            String str2 = second.getArgs().get(0);
            if (str1.startsWith("\"") && str1.endsWith("\"") && str2.startsWith("\"") && str2.endsWith("\"")) {
                return createInstruction(first.getOpcode(), str1.substring(0, str1.length() - 1) + str2.substring(1));
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
