package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.instructions.GotoLabelInstruction;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.JumpInstruction;
import info.teksol.mindcode.compiler.instructions.LabelInstruction;
import info.teksol.mindcode.compiler.instructions.PrintInstruction;
import info.teksol.mindcode.compiler.instructions.PrintflushInstruction;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicLiteral;
import info.teksol.mindcode.logic.LogicString;

import static info.teksol.mindcode.logic.ArgumentType.STRING_LITERAL;

/**
 * A simple optimizer which merges together print instructions with string literal arguments. The print instructions
 * will get merged even if they aren't consecutive, assuming there aren't instructions that could break the print
 * sequence ({@code jump}, {@code label} or {@code print [variable]}). Typical sequence of instructions targeted by this
 * optimizer is:
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
class PrintMerger extends BaseOptimizer {

    public PrintMerger(InstructionProcessor instructionProcessor) {
        super(instructionProcessor);
    }

    private PrintInstruction previous = null;

    @Override
    protected boolean optimizeProgram() {
        try (LogicIterator iterator = createIterator()) {
            while (iterator.hasNext()) {
                switch (iterator.next()) {
                    case PrintInstruction current -> tryMerge(iterator, current);

                    // Do not merge across jump, (active) label and printflush instructions
                    // Function calls generate a label, so they prevent merging as well
                    case JumpInstruction ix -> previous = null;
                    case GotoLabelInstruction ix -> previous = null;
                    case LabelInstruction ix && isActive(ix) -> previous = null;
                    case PrintflushInstruction ix -> previous = null;

                    default -> {
                    } // Do nothing
                }
            }
        }

        return false;
    }

    // Tries to merge previous and current.
    // When successful, updates instructions and sets previous to the newly merged instruction.
    // If the merge is not possible, sets previous to current
    private void tryMerge(LogicIterator iterator, PrintInstruction current) {
        if (previous != null && previous.getValue() instanceof LogicLiteral lit1 && current.getValue() instanceof LogicLiteral lit2) {
            if (aggressive() || (lit1.getType() == STRING_LITERAL && lit2.getType() == STRING_LITERAL)) {
                String str1 = lit1.format();
                String str2 = lit2.format();
                // Do not merge strings if the combined length is over 34, unless aggressive
                if (str1.length() + str2.length() <= 34 || aggressive()) {
                    PrintInstruction merged = createPrint(current.getAstContext(), LogicString.create(str1 + str2));
                    removeInstruction(previous);
                    iterator.set(merged);
                    previous = merged;
                    return;
                }
            }
        }

        previous = current;
    }

    // TODO find or create a function for this in BaseOptimizer
    //      This might miss some active labels
    private boolean isActive(LabelInstruction ix) {
        return firstInstructionIndex(ixx -> ixx.getArgs().stream()
                .anyMatch(a -> a instanceof LogicLabel la && la.equals(ix.getLabel()))) >= 0;
    }
}
