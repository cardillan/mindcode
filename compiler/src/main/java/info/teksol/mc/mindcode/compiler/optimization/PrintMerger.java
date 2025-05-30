package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.messages.WARN;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mc.mindcode.logic.arguments.LogicBuiltIn;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.arguments.LogicString;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/// A simple optimizer which merges together print instructions with string literal arguments. The print instructions
/// will get merged even if they aren't consecutive, assuming there aren't instructions that could break the print
/// sequence (`jump`, `label` or `print variable`). Typical sequence of instructions targeted by this optimizer is:
///
/// ```
/// print count
/// print "\n"
/// op div ratio count total
/// op mul ratio ratio 100
/// print "Used: "
/// print ratio
/// print "%"`
///```
///
/// Which will get turned to
///
/// ```
/// print count
/// op div ratio count total
/// op mul ratio ratio 100
/// print "\nUsed: "
/// print ratio
/// print "%"
///```
@NullMarked
class PrintMerger extends BaseOptimizer {

    public PrintMerger(OptimizationContext optimizationContext) {
        super(Optimization.PRINT_MERGING, optimizationContext);
    }

    private final List<PrintingInstruction> printVars = new ArrayList<>();
    private @Nullable LogicInstruction previous;

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        reset();

        Merger printMerger = createPrintMerger();

        try (LogicIterator iterator = createIterator()) {
            while (iterator.hasNext()) {
                LogicInstruction current = iterator.next();
                if (current instanceof CustomInstruction ix) {
                    if (ix.isText()) reset();
                } else {
                    switch (current.getOpcode()) {
                        case PRINT, PRINTCHAR -> printMerger.tryMerge(iterator, (PrintingInstruction) current);
                        case REMARK -> tryMergeRemark(iterator, (RemarkInstruction) current);

                        // Do not merge across jump, (active) label and printflush instructions
                        // Function calls generate a label, so they prevent merging as well
                        case DRAW -> {
                            if (((DrawInstruction) current).getType().getKeyword().equals("print")) {
                                // draw print flushes the text buffer
                                reset();
                            }
                        }
                        case LABEL -> {
                            if (isActive((LabelInstruction) current)) {
                                reset();
                            }
                        }
                        case JUMP, MULTILABEL, PRINTFLUSH, FORMAT, ASSERT_PRINTS, ASSERT_FLUSH -> reset();
                    }
                }
            }
        }

        return false;
    }

    private Merger createPrintMerger() {
        return instructionProcessor.isSupported(Opcode.FORMAT) && noDangerousStrings()
                ? this::tryMergeUsingFormat
                : this::tryMergeUsingPrint;
    }

    private void reset() {
        previous = null;
        printVars.clear();
    }

    private @Nullable String printValue(PrintingInstruction ix) {
        if (!ix.getValue().isConstant()) return null;

        return switch (ix) {
            case PrintInstruction p -> p.getValue().format(instructionProcessor);
            case PrintCharInstruction p -> printCharValue(p);
            default -> throw new MindcodeInternalError("Unhandled instruction type: " + ix.getClass().getSimpleName());
        };
    }

    private @Nullable String printCharValue(PrintCharInstruction ix) {
        return switch (ix.getValue()) {
            case LogicNumber number -> {
                char ch = (char) Math.floor(number.getDoubleValue());
                boolean isPrintable = !Character.isISOControl(ch) && !Character.isSurrogate(ch);
                yield isPrintable && ch != '"' ? String.valueOf(ch) : null;
            }

            case LogicBuiltIn builtIn -> builtIn.getObject() != null ? builtIn.getObject().iconString(metadata) : null;

            default -> null;
        };
    }

    // Tries to merge previous and current prints.
    // When successful, updates instructions and sets previous to the newly merged instruction.
    // If the merge is not possible, sets previous to current
    private void tryMergeUsingPrint(LogicIterator iterator, PrintingInstruction current) {
        if (previous instanceof PrintingInstruction prev && prev.getValue().isConstant() && current.getValue().isConstant()) {
            String str1 = printValue(prev);
            String str2 = printValue(current);
            // Do not merge strings if the combined length is over 34, unless advanced
            if (str1 != null && str2 != null && (advanced() || str1.length() + str2.length() <= 34)) {
                PrintInstruction merged = createPrint(current.getAstContext(), LogicString.create(str1 + str2));
                removeInstruction(this.previous);
                iterator.set(merged);
                this.previous = merged;
                return;
            }
        }

        previous = current;
    }

    private interface Merger {
        void tryMerge(LogicIterator iterator, PrintingInstruction current);
    }

    // Tries to merge previous and current format.
    // When successful, updates instructions and sets previous to the newly merged instruction.
    // If the merge is not possible, sets previous to current
    private void tryMergeUsingFormat(LogicIterator iterator, PrintingInstruction current) {
        String previousStr, currentStr;
        if (previous instanceof PrintingInstruction prev && (previousStr = printValue(prev)) != null) {
            if (current.getValue().isConstant() && (currentStr = printValue(current)) != null) {
                StringBuilder str = new StringBuilder(previousStr);
                for (PrintingInstruction p : printVars) {
                    str.append("{0}");
                    optimizationContext.replaceInstruction(p, createFormat(p.getAstContext(), p.getValue()));
                }
                printVars.clear();
                str.append(currentStr);
                iterator.remove();

                PrintInstruction updated = createPrint(prev.getAstContext(), LogicString.create(str.toString()));
                optimizationContext.replaceInstruction(prev, updated);
                previous = updated;
            } else if (current instanceof PrintCharInstruction) {
                // Unresolvable printchar breaks the sequence
                reset();
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
                    LogicString.create(prev.getValue().format(instructionProcessor) + current.getValue().format(instructionProcessor)));
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

    private boolean noDangerousStrings() {
        List<LogicString> dangerousStrings = optimizationContext.instructionStream()
                .flatMap(LogicInstruction::inputArgumentsStream)
                .filter(LogicString.class::isInstance)
                .map(LogicString.class::cast)
                .filter(s -> containsDangerousStrings(s.getValue()))
                .toList();

        dangerousStrings.forEach(s -> warn(s.sourcePosition(), WARN.FORMAT_PRECLUDED_BY_STRING_LITERAL));

        return dangerousStrings.isEmpty();
    }

    private boolean containsDangerousStrings(String text) {
        return text.endsWith("{") || text.startsWith("}")
               || text.contains("{0}") || text.contains("{{") || text.contains("}}");
    }
}
