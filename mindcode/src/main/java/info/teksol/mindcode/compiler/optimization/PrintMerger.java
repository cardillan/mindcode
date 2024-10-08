package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.instructions.LabelInstruction;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.PrintInstruction;
import info.teksol.mindcode.compiler.instructions.RemarkInstruction;
import info.teksol.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
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

    public PrintMerger(OptimizationContext optimizationContext) {
        super(Optimization.PRINT_TEXT_MERGING, optimizationContext);
    }

    private LogicInstruction previous;

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        previous = null;

        try (LogicIterator iterator = createIterator()) {
            while (iterator.hasNext()) {
                LogicInstruction current = iterator.next();
                switch (current.getOpcode()) {
                    case PRINT  -> tryMerge(iterator, (PrintInstruction) current);
                    case REMARK -> tryMerge(iterator, (RemarkInstruction) current);

                    // Do not merge across jump, (active) label and printflush instructions
                    // Function calls generate a label, so they prevent merging as well
                    case LABEL  -> {
                        if (isActive((LabelInstruction) current)) {
                            previous = null;
                        }
                    }
                    case JUMP, GOTOLABEL, PRINTFLUSH -> previous = null;
                }
            }
        }

        return false;
    }

    // Tries to merge previous and current prints.
    // When successful, updates instructions and sets previous to the newly merged instruction.
    // If the merge is not possible, sets previous to current
    private void tryMerge(LogicIterator iterator, PrintInstruction current) {
        if (previous instanceof PrintInstruction previous && previous.getValue().isConstant() && current.getValue().isConstant()) {
            if (advanced() || previous.getValue().getType() == STRING_LITERAL && current.getValue().getType() == STRING_LITERAL) {
                String str1 = previous.getValue().format();
                String str2 = current.getValue().format();
                // Do not merge strings if the combined length is over 34, unless advanced
                if (advanced() || str1.length() + str2.length() <= 34) {
                    PrintInstruction merged = createPrint(current.getAstContext(), LogicString.create(str1 + str2));
                    removeInstruction(this.previous);
                    iterator.set(merged);
                    this.previous = merged;
                    return;
                }
            }
        }

        previous = current;
    }

    // Tries to merge previous and current remarks.
    // When successful, updates instructions and sets previous to the newly merged instruction.
    // If the merge is not possible, sets previous to current
    private void tryMerge(LogicIterator iterator, RemarkInstruction current) {
        if (previous instanceof RemarkInstruction previous && previous.getAstContext() == current.getAstContext() &&
                previous.getValue().isConstant() && current.getValue().isConstant()) {
            RemarkInstruction merged = createRemark(current.getAstContext(),
                    LogicString.create(previous.getValue().format() + current.getValue().format()));
            removeInstruction(this.previous);
            iterator.set(merged);
            this.previous = merged;
            return;
        }

        previous = current;
    }

    private boolean isActive(LabelInstruction ix) {
        return optimizationContext.isActive(ix.getLabel());
    }
}
