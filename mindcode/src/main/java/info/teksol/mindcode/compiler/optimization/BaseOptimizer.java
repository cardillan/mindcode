package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.MindcodeException;
import info.teksol.mindcode.compiler.MessageLevel;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.logic.LogicLabel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static info.teksol.util.CollectionUtils.findFirstIndex;

/**
 * Base class for optimizers. Contains methods to access and modify instructions of the program being processed
 * by the optimizer.
 * <p>
 * <b>Note:</b> The methods for accessing instructions always return {@code null} when the instruction doesn't exist,
 * regardless of the way the instruction is being accessed (via index, searching for properties etc.) It is assumed
 * that accessed instructions are practically always tested using instanceof, which handles {@code null} values
 * gracefully. Methods that won't handle invalid indexes gracefully are documented as such.
 * <p>
 * Method that modify a program do not fail silently. For example, when trying to insert instruction before
 * instruction not in a program, an error occurs.
 */
abstract class BaseOptimizer extends AbstractOptimizer {
    private List<LogicInstruction> program;
    private AstContext rootContext;
    private int iterations = 0;
    private int modifications = 0;
    private int initialCount;
    private int finalCount;

    public BaseOptimizer(InstructionProcessor instructionProcessor) {
        super(instructionProcessor);
    }

    // Optimization logic

    /**
     * Performs one iteration of the optimization. Return true to run another iteration, false when done.
     * @return true to re-run the optimization
     */
    protected abstract boolean optimizeProgram();

    protected AstContext getRootContext() {
        return rootContext;
    }

    @Override
    public void optimizeProgram(List<LogicInstruction> input, AstContext rootContext) {
        this.rootContext = rootContext;
        this.program = Objects.requireNonNull(input);

        initialCount = program.stream().mapToInt(LogicInstruction::getRealSize).sum();

        boolean repeat;
        do {
            modifications = 0;
            repeat = optimizeProgram();
            if (!iterators.isEmpty()) {
                throw new IllegalStateException("Unclosed iterators.");
            }
            if (modifications > 0 && rootContext != null) {
                rebuildAstContextTree();
            }

            debugPrinter.registerIteration(this, ++iterations, program);
        } while (repeat && modifications > 0);

        finalCount = program.stream().mapToInt(LogicInstruction::getRealSize).sum();

        generateFinalMessages();
    }

    protected void generateFinalMessages() {
        if (finalCount != initialCount) {
            String verb = finalCount < initialCount ? "eliminated" : "added";
            if (iterations > 1) {
                emitMessage(MessageLevel.INFO, "%6d instructions %s by %s (%d iterations).",
                        Math.abs(initialCount - finalCount), verb, getClass().getSimpleName(), iterations - 1);
            } else {
                emitMessage(MessageLevel.INFO, "%6d instructions %s by %s.",
                        Math.abs(initialCount - finalCount), verb, getClass().getSimpleName());
            }
        }
    }

    private void rebuildAstContextTree() {
        eraseChildren(rootContext);
        for (LogicInstruction ix : program) {
            ensureChildInParent(ix.getAstContext());
        }
    }

    private void eraseChildren(AstContext context) {
        context.children().forEach(this::eraseChildren);
        context.children().clear();
    }

    private void ensureChildInParent(AstContext context) {
        if (context.parent() != null) {
            ensureChildInParent(context.parent());
            List<AstContext> children = context.parent().children();
            if (children.isEmpty()) {
                children.add(context);
            } else if (children.get(children.size() - 1) != context) {
                for (AstContext ctx : children) {
                    if (context == ctx) {
                        // Some optimization moved instructions in such a way that
                        // instructions in this context do not form continuous region.
                        throw new OptimizationException("Discontinuous AST context.");
                    }
                }
                children.add(context);
            }
        }
    }

    //<editor-fold desc="Finding instructions by position">
    protected LogicInstruction instructionAt(int index) {
        return index >= 0 && index < program.size() ? program.get(index) : null;
    }

    protected LogicInstruction instructionBefore(LogicInstruction instruction) {
        return instructionAt(instructionIndex(instruction) - 1);
    }

    protected LogicInstruction instructionAfter(LogicInstruction instruction) {
        int index = instructionIndex(instruction);
        return instructionAt(index + 1);
    }

    /**
     * Provides a sublist of the current program. Will fail when such sublist cannot be created.
     * Returned list won't reflect further changed to the program.
     *
     * @param fromIndex starting index
     * @param toIndex ending index (exclusive)
     * @return a List containing given instructions.
     */
    protected List<LogicInstruction> instructionSubList(int fromIndex, int toIndex) {
        return List.copyOf(program.subList(fromIndex, toIndex));
    }

    protected Stream<LogicInstruction> instructionStream() {
        return program.stream();
    }

    /**
     * Locates an instruction based on instance identity and returns its index.
     * Returns -10 when such instruction doesn't exist, for better diagnosis of errors when
     * trying to remove previous instruction.
     *
     * @param instruction instruction to locate
     * @return index of the instruction, or -10 if the instruction isn't found.
     */
    protected int instructionIndex(LogicInstruction instruction) {
        for (int i = 0; i < program.size(); i++) {
            if (instruction == program.get(i)) {
                return i;
            }
        }
        return -10;
    }

    /** Returns the index of given instruction. When the instruction isn't found, an exception is thrown. */
    protected int existingInstructionIndex(LogicInstruction instruction) {
        for (int i = 0; i < program.size(); i++) {
            if (instruction == program.get(i)) {
                return i;
            }
        }
        throw new OptimizationException("Instruction not found in program.\nInstruction: " + instruction);
    }
    //</editor-fold>

    //<editor-fold desc="Finding instructions by properties">
    // Starting at given index, finds first instruction matching predicate.
    // Returns the index or -1 if not found.
    protected int firstInstructionIndex(int startIndex, Predicate<LogicInstruction> matcher) {
        return findFirstIndex(program, startIndex, matcher);
    }

    // Finds a first non-label instruction following a label
    // Returns the index or -1 if not found.
    protected int labeledInstructionIndex(LogicLabel label) {
        int labelIndex = firstInstructionIndex(0, ix -> ix instanceof LabelInstruction li && li.getLabel().equals(label));
        return labelIndex >= 0 ? firstInstructionIndex(labelIndex + 1, ix -> !(ix instanceof LabelInstruction)) : -1;
    }

    // Starting at given index, find first instruction matching predicate. Return null if not found.
    protected LogicInstruction firstInstruction(int startIndex, Predicate<LogicInstruction> matcher) {
        int result = firstInstructionIndex(startIndex, matcher);
        return result < 0 ? null : program.get(result);
    }

    // Finds a first non-label instruction following a label.
    // Return null when not found
    protected LogicInstruction labeledInstruction(LogicLabel label) {
        return instructionAt(labeledInstructionIndex(label));
    }
    // Return list of instructions matching predicate
    protected List<LogicInstruction> instructions(Predicate<LogicInstruction> matcher) {
        return program.stream().filter(matcher).toList();
    }

    //</editor-fold>

    //<editor-fold desc="General code structure methods">
    /**
     * Determines whether the code block is isolated, i.e. all effects that can happen inside the block are contained
     * in the block itself. In other word, all jumps that target a label inside the code block originate
     * in the code block.
     *
     * @param codeBlock code block to inspect
     * @return true if the block is localized
     */
    // TODO it would probably be better to reimplement the logic depending on this to use AST contexts
    protected boolean isIsolated(List<LogicInstruction> codeBlock) {
        Set<LogicLabel> localLabels = codeBlock.stream()
                .filter(ix -> ix instanceof LabelInstruction)
                .map(ix -> ((LabelInstruction) ix).getLabel())
                .collect(Collectors.toSet());

        // Get jump/goto instructions targeting any of local labels
        // If all of them are local to the code block, the code block is linear
        return instructionStream()
                .filter(ix -> ix instanceof JumpInstruction || ix instanceof GotoInstruction)
                .filter(ix -> getPossibleTargetLabels(ix).anyMatch(localLabels::contains))
                .allMatch(ix -> codeBlock.stream().anyMatch(local -> local == ix));
    }

    /**
     * Determines whether the given code block is contained, meaning it doesn't contain jumps outside.
     * Outside jumps are generated as a result of break, continue or return statements.
     *
     * @param codeBlock code block to inspect
     * @return true if the code block exits though its last instruction.
     */
    protected boolean isContained(List<LogicInstruction> codeBlock) {
        Set<LogicLabel> localLabels = codeBlock.stream()
                .filter(LabelInstruction.class::isInstance)
                .map(LabelInstruction.class::cast)
                .map(LabelInstruction::getLabel)
                .collect(Collectors.toSet());

        // No end/return instructions
        // All jump/goto instructions from this context must target only local labels
        return codeBlock.stream()
                .noneMatch(ix -> ix instanceof ReturnInstruction || ix instanceof EndInstruction)
                && codeBlock.stream()
                .filter(ix -> ix instanceof JumpInstruction || ix instanceof GotoInstruction)
                .allMatch(ix -> getPossibleTargetLabels(ix).allMatch(localLabels::contains));
    }

    private Stream<LogicLabel> getPossibleTargetLabels(LogicInstruction instruction) {
        return switch (instruction) {
            case JumpInstruction ix -> Stream.of(ix.getTarget());
            case GotoInstruction ix -> instructionStream()
                    .filter(in -> in instanceof LabelInstruction && in.matchesMarker(ix.getMarker()))
                    .map(in -> (LabelInstruction) in)
                    .map(LabelInstruction::getLabel);
            default -> Stream.empty();
        };
    }
    //</editor-fold>

    //<editor-fold desc="Program modification">
    protected void insertInstruction(int index, LogicInstruction instruction) {
        iterators.forEach(iterator -> iterator.instructionAdded(index));
        program.add(index, instruction);
        modifications++;
    }

    protected void replaceInstruction(int index, LogicInstruction instruction) {
        program.set(index, instruction);
        modifications++;
    }

    protected void removeInstruction(int index) {
        iterators.forEach(iterator -> iterator.instructionRemoved(index));
        program.remove(index);
        modifications++;
    }

    protected void insertBefore(LogicInstruction anchor, LogicInstruction inserted) {
        insertInstruction(existingInstructionIndex(anchor), inserted);
    }

    protected void insertAfter(LogicInstruction anchor, LogicInstruction inserted) {
        insertInstruction(existingInstructionIndex(anchor) + 1, inserted);
    }

    protected void insertInstructions(int index, List<LogicInstruction> instructions) {
        for (LogicInstruction instruction : instructions) {
            insertInstruction(index++, instruction);
        }
    }

    protected void insertInstructions(int index, LogicList instructions) {
        for (LogicInstruction instruction : instructions) {
            insertInstruction(index++, instruction);
        }
    }

    protected void replaceInstruction(LogicInstruction original, LogicInstruction replaced) {
        replaceInstruction(existingInstructionIndex(original), replaced);
    }

    protected void removeInstruction(LogicInstruction instruction) {
        removeInstruction(existingInstructionIndex(instruction));
    }

    protected void removePrevious(LogicInstruction anchor) {
        removeInstruction(existingInstructionIndex(anchor) - 1);
    }

    protected void removeNext(LogicInstruction anchor) {
        removeInstruction(existingInstructionIndex(anchor) + 1);
    }


    protected void removeMatchingInstructions(Predicate<LogicInstruction> matcher) {
        for (int index = 0; index < program.size(); index++) {
            if (matcher.test(program.get(index))) {
                removeInstruction(index);
                index--;
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="Logic iterator">
    private final List<LogicIterator> iterators = new ArrayList<>();

    protected LogicIterator createIterator() {
        return createIterator(0);
    }

    protected LogicIterator createIteratorAtInstruction(LogicInstruction instruction) {
        int index = instructionIndex(instruction);
        return index < 0 ? null : createIterator(index);
    }

    protected LogicIterator createIteratorAtLabel(LogicLabel label) {
        int index = firstInstructionIndex(0, ix -> ix instanceof LabelInstruction lx && lx.getLabel().equals(label));
        return index < 0 ? null : createIterator(index);
    }

    protected LogicIterator createIteratorAtLabelledInstruction(LogicLabel label) {
        int index = labeledInstructionIndex(label);
        return index < 0 ? null : createIterator(index);
    }

    protected class LogicIterator implements ListIterator<LogicInstruction>, AutoCloseable {
        private int cursor;
        private boolean closed = false;
        private int lastRet = -1;

        private LogicIterator(int cursor) {
            this.cursor = cursor;
        }

        @Override
        public void close() {
            closed = true;
            closeIterator(this);
        }

        public LogicIterator copy() {
            return createIterator(cursor);
        }

        // Returns the instruction at given offset from current position
        // Null if it doesn't exist
        public LogicInstruction peek(int offset) {
            return instructionAt(cursor + offset);
        }

        public boolean hasNext() {
            checkClosed();
            return cursor < program.size();
        }

        public LogicInstruction next() {
            checkClosed();
            int i = cursor;
            if (i >= program.size()) {
                throw new NoSuchElementException();
            }
            cursor = i + 1;
            return program.get(lastRet = i);
        }

        @Override
        public int nextIndex() {
            checkClosed();
            return cursor;
        }

        @Override
        public boolean hasPrevious() {
            checkClosed();
            return cursor != 0;
        }

        public LogicInstruction previous() {
            checkClosed();
            int i = cursor - 1;
            if (i < 0) {
                throw new NoSuchElementException();
            }
            cursor = i;
            return program.get(lastRet = i);
        }

        public int previousIndex() {
            checkClosed();
            return cursor - 1;
        }

        public void remove() {
            checkClosed();
            if (lastRet < 0) {
                throw new IllegalStateException();
            }
            removeInstruction(lastRet);
            // Cursor will be updated in instructionRemoved
            lastRet = -1;
        }

        @Override
        public void set(LogicInstruction instruction) {
            checkClosed();
            if (lastRet < 0) {
                throw new IllegalStateException();
            }
            replaceInstruction(lastRet, instruction);
        }

        @Override
        public void add(LogicInstruction instruction) {
            checkClosed();
            int index = cursor;
            insertInstruction(index, instruction);
            // Cursor will be updated in instructionAdded
            lastRet = -1;
        }

        // Provides a stream of instructions between this and upTo (inclusive at this, exclusive at end)
        public Stream<LogicInstruction> between(LogicIterator upTo) {
            checkClosed();
            upTo.checkClosed();
            return program.subList(cursor, upTo.cursor).stream();
        }

        private void instructionRemoved(int index) {
            if (cursor > index) {
                cursor--;
            }
            if (lastRet == index) {
                lastRet = -1;
            } else if (lastRet > index) { // Cannot happen if it is -1
                lastRet--;
            }
        }

        private void instructionAdded(int index) {
            // When an instruction is added, the lastRet doesn't change.
            if (cursor >= index) {
                cursor++;
            }
            if (lastRet >= index) {  // Cannot happen if it is -1
                lastRet++;
            }
        }

        private void checkClosed() {
            if (closed) {
                throw new IllegalStateException("Trying to access closed iterator.");
            }
        }

        @Override
        public String toString() {
            return "LogicIterator{" +
                    "cursor=" + cursor +
                    ", closed=" + closed +
                    ", lastRet=" + lastRet +
                    '}';
        }
    }

    private LogicIterator createIterator(int index) {
        LogicIterator iterator = new LogicIterator(index);
        iterators.add(iterator);
        return iterator;
    }

    private void closeIterator(LogicIterator iterator) {
        if (!iterators.remove(iterator)) {
            throw new IllegalStateException("Trying to close unknown iterator.");
        }
    }
    //</editor-fold>

    //<editor-fold desc="Finding contexts">
    protected boolean hasSubcontexts(AstContext context, AstSubcontextType... types) {
        if (context.children().size() == types.length) {
            for (int i = 0; i < types.length; i++) {
                if (context.children().get(i).subcontextType() != types[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private void forEachContext(AstContext astContext, Predicate<AstContext> matcher, Consumer<AstContext> action) {
        if (matcher.test(astContext)) {
            action.accept(astContext);
        }
        astContext.children().forEach(c -> forEachContext(c, matcher, action));
    }

    protected void forEachContext(Predicate<AstContext> matcher, Consumer<AstContext> action) {
        forEachContext(getRootContext(), matcher, action);
    }

    protected List<AstContext> contexts(Predicate<AstContext> matcher) {
        List<AstContext> contexts = new ArrayList<>();
        forEachContext(getRootContext(), matcher, contexts::add);
        return List.copyOf(contexts);
    }

    protected AstContext context(Predicate<AstContext> matcher) {
        List<AstContext> contexts = contexts(matcher);
        return switch (contexts.size()) {
            case 0 -> null;
            case 1 -> contexts.get(0);
            default -> throw new MindcodeException("More than one context found.");
        };
    }

    /**
     * Creates a list of AST contexts representing inline functions.
     *
     * @return list of inline function node contexts
     */
    protected List<AstContext> getInlineFunctions() {
        return contexts(c -> c.contextType() == AstContextType.INLINE_FUNCTION);
    }

    protected List<AstContext> getOutOfLineFunctions() {
        return contexts(c -> c.contextType() == AstContextType.FUNCTION);
    }
    //</editor-fold>


    //<editor-fold desc="Finding instructions by context">
    protected LogicList contextInstructions(AstContext astContext) {
        return astContext == null ? EMPTY :
                new LogicList(List.copyOf(instructionStream().filter(ix -> ix.matchesContext(astContext)).toList()));
    }

    protected Stream<LogicInstruction> contextStream(AstContext astContext) {
        return astContext == null ? Stream.empty() : instructionStream().filter(ix -> ix.matchesContext(astContext));
    }
    //</editor-fold>

    //<editor-fold desc="Logic list">
    private static final LogicList EMPTY = new LogicList(java.util.List.of());

    protected static class LogicList implements Iterable<LogicInstruction> {
        private final List<LogicInstruction> instructions;

        private LogicList(List<LogicInstruction> instructions) {
            this.instructions = instructions;
        }

        public int size() {
            return instructions.size();
        }

        public boolean isEmpty() {
            return instructions.isEmpty();
        }

        public Iterator<LogicInstruction> iterator() {
            return instructions.iterator();
        }

        public LogicInstruction get(int index) {
            return index >= 0 && index < size() ? instructions.get(index) : null;
        }

        public LogicInstruction getFirst() {
            return isEmpty() ? null : get(0);
        }

        public LogicInstruction getLast() {
            return isEmpty() ? null : get(size() - 1);
        }

        public LogicInstruction fromEnd(int index) {
            return index >= 0 && index < size() ? instructions.get(size() - index - 1) : null;
        }

        public int indexOf(LogicInstruction o) {
            return instructions.indexOf(o);
        }

        public int indexOf(Predicate<LogicInstruction> matcher) {
            for (int i = 0; i < size(); i++) {
                if (matcher.test(get(i))) {
                    return i;
                }
            }

            return -1;
        }

        public int lastIndexOf(Predicate<LogicInstruction> matcher) {
            for (int i = size() - 1; i >= 0; i--) {
                if (matcher.test(get(i))) {
                    return i;
                }
            }

            return -1;
        }

        public int lastIndexOf(LogicInstruction o) {
            return instructions.lastIndexOf(o);
        }

        public ListIterator<LogicInstruction> listIterator() {
            return instructions.listIterator();
        }

        public LogicList subList(int fromIndex, int toIndex) {
            return new LogicList(instructions.subList(fromIndex, toIndex));
        }

        public List<LogicInstruction> toList() {
            return instructions;
        }

        public Stream<LogicInstruction> stream() {
            return instructions.stream();
        }

        public Stream<LogicInstruction> reversedStream() {
            return reversed().stream();
        }

        public void forEach(Consumer<? super LogicInstruction> action) {
            instructions.forEach(action);
        }

        private List<LogicInstruction> reversed;

        private List<LogicInstruction> reversed() {
            if (reversed == null) {
                reversed = new ArrayList<>();
                ListIterator<LogicInstruction> listIterator = instructions.listIterator(instructions.size());

                while (listIterator.hasPrevious()) {
                    reversed.add(listIterator.previous());
                }
            }
            return reversed;
        }
    }
    //</editor-fold>
}
