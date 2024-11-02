package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mindcode.logic.LogicNull;
import info.teksol.mindcode.logic.LogicString;
import info.teksol.mindcode.logic.Opcode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

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
        super(Optimization.PRINT_MERGING, optimizationContext);
    }

    private final List<PrintInstruction> printVars = new ArrayList<>();
    private LogicInstruction previous;

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        reset();

        BiConsumer<LogicIterator, PrintInstruction> printMerger =
                experimental()
                        && instructionProcessor.isSupported(Opcode.FORMAT, List.of(LogicNull.NULL))
                        && !containsDangerousStrings()
                ? this::tryMergeUsingFormat : this::tryMergeUsingPrint;

        try (LogicIterator iterator = createIterator()) {
            while (iterator.hasNext()) {
                LogicInstruction current = iterator.next();
                switch (current.getOpcode()) {
                    case PRINT  -> printMerger.accept(iterator, (PrintInstruction) current);
                    case REMARK -> tryMergeRemark(iterator, (RemarkInstruction) current);

                    // Do not merge across jump, (active) label and printflush instructions
                    // Function calls generate a label, so they prevent merging as well
                    case DRAW -> {
                        if (((DrawInstruction) current).getType().getKeyword().equals("print")) {
                            // draw print flushes the text buffer
                            reset();
                        }
                    }
                    case LABEL  -> {
                        if (isActive((LabelInstruction) current)) {
                            reset();
                        }
                    }
                    case JUMP, GOTOLABEL, PRINTFLUSH, FORMAT -> reset();
                }
            }
        }

        return false;
    }

    private void reset() {
        previous = null;
        printVars.clear();
    }

    // Tries to merge previous and current prints.
    // When successful, updates instructions and sets previous to the newly merged instruction.
    // If the merge is not possible, sets previous to current
    private void tryMergeUsingPrint(LogicIterator iterator, PrintInstruction current) {
        if (previous instanceof PrintInstruction prev && prev.getValue().isConstant() && current.getValue().isConstant()) {
            if (advanced() || prev.getValue().getType() == STRING_LITERAL && current.getValue().getType() == STRING_LITERAL) {
                String str1 = prev.getValue().format();
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

    // Tries to merge previous and current format.
    // When successful, updates instructions and sets previous to the newly merged instruction.
    // If the merge is not possible, sets previous to current
    private void tryMergeUsingFormat(LogicIterator iterator, PrintInstruction current) {
        if (previous instanceof PrintInstruction prev && prev.getValue().isConstant()) {
            StringBuilder str = new StringBuilder(prev.getValue().format());
            if (current.getValue().isConstant()) {
                for (PrintInstruction p : printVars) {
                    str.append("{0}");
                    optimizationContext.replaceInstruction(p, createFormat(p.getAstContext(), p.getValue()));
                }
                printVars.clear();
                str.append(current.getValue().format());
                iterator.remove();

                PrintInstruction updated = createPrint(prev.getAstContext(), LogicString.create(str.toString()));
                optimizationContext.replaceInstruction(prev, updated);
                previous = updated;
            } else {
                printVars.add(current);
            }
        } else {
            previous = current;
        }
    }

    // Tries to merge previous and current remarks.
    // When successful, updates instructions and sets previous to the newly merged instruction.
    // If the merge is not possible, sets previous to current
    // Format isn't used for remarks
    private void tryMergeRemark(LogicIterator iterator, RemarkInstruction current) {
        if (previous instanceof RemarkInstruction prev && prev.getAstContext() == current.getAstContext() &&
                prev.getValue().isConstant() && current.getValue().isConstant()) {
            RemarkInstruction merged = createRemark(current.getAstContext(),
                    LogicString.create(prev.getValue().format() + current.getValue().format()));
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

    private boolean containsDangerousStrings() {
        return optimizationContext.instructionStream()
                .flatMap(LogicInstruction::inputArgumentsStream)
                .filter(LogicString.class::isInstance)
                .map(LogicString.class::cast)
                .map(LogicString::format)
                .anyMatch(this::containsDangerousStrings);
    }

    private boolean containsDangerousStrings(String text) {
        return text.endsWith("{") || text.startsWith("}")
                || text.contains("{0}") || text.contains("{{") || text.contains("}}");
    }
}
