package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.MessageLevel;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.logic.LogicLabel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static info.teksol.util.CollectionUtils.findFirstIndex;
import static info.teksol.util.CollectionUtils.findLastIndex;

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

    protected void setProgram(List<LogicInstruction> program) {
        this.program = program;
    }

    protected void setRootContext(AstContext rootContext) {
        this.rootContext = rootContext;
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
        program.forEach(ix -> eraseChildren(ix.getAstContext()));  // Erase children of nodes that aren't part of the tree yet
        program.forEach(ix -> ensureChildInParent(ix.getAstContext()));
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
                        throw new MindcodeInternalError("Discontinuous AST context " + context);
                    }
                }
                children.add(context);
            }
        }
    }

    //<editor-fold desc="Finding instructions by position">
    /**
     * Return the instruction at given position in the program.
     *
     * @param index index of the instruction
     * @return the instruction at given index
     */
    protected LogicInstruction instructionAt(int index) {
        return program.get(index);
    }

    /**
     * Returns the instruction preceding the given instruction. If such instruction doesn't exist
     * (because the reference instruction is the first one), returns null. If the reference instruction
     * isn't found the program, an exception is thrown.
     *
     * @param instruction instruction to find in the program
     * @return instruction before the reference instruction
     */
    protected LogicInstruction instructionBefore(LogicInstruction instruction) {
        int index = existingInstructionIndex(instruction);
        return index == 0 ? null : instructionAt(index - 1);
    }

    /**
     * Returns the instruction following the given instruction. If such instruction doesn't exist
     * (because the reference instruction is the last one), returns null. If the reference instruction
     * isn't found the program, an exception is thrown.
     *
     * @param instruction instruction to find in the program
     * @return instruction after the reference instruction
     */
    protected LogicInstruction instructionAfter(LogicInstruction instruction) {
        int index = existingInstructionIndex(instruction) + 1;
        return index == program.size() ? null : instructionAt(index);
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

    /**
     * Provides stream of all instructions in the program.
     *
     * @return an instruction stream
     */
    protected Stream<LogicInstruction> instructionStream() {
        return program.stream();
    }

    /**
     * Locates an instruction based on instance identity and returns its index.
     * Returns -1 when such instruction doesn't exist.
     *
     * @param instruction instruction to locate
     * @return index of the instruction, or -1 if the instruction isn't found.
     */
    protected int instructionIndex(LogicInstruction instruction) {
        for (int i = 0; i < program.size(); i++) {
            if (instruction == program.get(i)) {
                return i;
            }
        }
        return -1;
    }

    /** Returns the index of given instruction. When the instruction isn't found, an exception is thrown. */
    protected int existingInstructionIndex(LogicInstruction instruction) {
        for (int i = 0; i < program.size(); i++) {
            if (instruction == program.get(i)) {
                return i;
            }
        }
        throw new NoSuchElementException("Instruction not found in program.\nInstruction: " + instruction);
    }
    //</editor-fold>

    //<editor-fold desc="Finding instructions by properties">

    /**
     * Starting at given index, finds the first instruction matching predicate.
     * Returns the index or -1 if not found.
     *
     * @param startIndex index to start search from, inclusive
     * @param matcher predicate matching sought instruction
     * @return index of the first instruction matching the predicate
     */
    protected int firstInstructionIndex(int startIndex, Predicate<LogicInstruction> matcher) {
        return findFirstIndex(program, startIndex, matcher);
    }

    /**
     * Finds the first instruction matching predicate in the entire program.
     * Returns the index or -1 if not found.
     *
     * @param matcher predicate matching sought instruction
     * @return index of the first instruction matching the predicate
     */
    protected int firstInstructionIndex(Predicate<LogicInstruction> matcher) {
        return findFirstIndex(program, 0, matcher);
    }

    /**
     * Finds the last instruction matching predicate up to the given instruction index.
     * Returns the index or -1 if not found.
     *
     * @param startIndex index to end search at, inclusive
     * @param matcher predicate matching sought instruction
     * @return index of the last instruction matching the predicate, up to the specified index
     */
    protected int lastInstructionIndex(int startIndex, Predicate<LogicInstruction> matcher) {
        return findLastIndex(program, startIndex, matcher);
    }

    /**
     * Finds the last instruction matching predicate in the entire program.
     * Returns the index or -1 if not found.
     *
     * @param matcher predicate matching sought instruction
     * @return index of the last instruction matching the predicate
     */
    protected int lastInstructionIndex(Predicate<LogicInstruction> matcher) {
        return findLastIndex(program, program.size() - 1, matcher);
    }

    /**
     * Finds a first non-label instruction following a label
     * Returns the index or -1 if not found.
     * If the label doesn't exist in the program, an exception is thrown.
     *
     * @param label target label
     * @return first non-label instruction following the label
     */
    protected int labeledInstructionIndex(LogicLabel label) {
        int labelIndex = firstInstructionIndex(ix -> ix instanceof LabeledInstruction li && li.getLabel().equals(label));
        if (labelIndex < 0) {
            throw new MindcodeInternalError("Label not found in program.\nLabel: " + label);
        }
        return firstInstructionIndex(labelIndex + 1, ix -> !(ix instanceof LabeledInstruction));
    }

    /**
     * Starting at given index, find first instruction matching predicate. Return null if not found.
     *
     * @param startIndex index to start search from, inclusive
     * @param matcher predicate matching sought instruction
     * @return the first instruction matching the predicate
     */
    protected LogicInstruction firstInstruction(int startIndex, Predicate<LogicInstruction> matcher) {
        int result = firstInstructionIndex(startIndex, matcher);
        return result < 0 ? null : program.get(result);
    }

    /**
     * Finds the first instruction matching predicate. Returns null if not found.
     *
     * @param matcher predicate matching sought instruction
     * @return the first instruction in the entire program matching the predicate
     */
    protected LogicInstruction firstInstruction(Predicate<LogicInstruction> matcher) {
        int result = firstInstructionIndex(matcher);
        return result < 0 ? null : program.get(result);
    }

    /**
     * Finds the last instruction matching predicate up to the given instruction index.
     * Returns null if not found.
     *
     * @param startIndex index to end search at, inclusive
     * @param matcher predicate matching sought instruction
     * @return index of the last instruction matching the predicate, up to the specified index
     */
    protected LogicInstruction lastInstruction(int startIndex, Predicate<LogicInstruction> matcher) {
        int result = lastInstructionIndex(startIndex, matcher);
        return result < 0 ? null : program.get(result);
    }

    /**
     * Finds the last instruction matching predicate. Returns null if not found.
     *
     * @param matcher predicate matching sought instruction
     * @return the last instruction matching the predicate
     */
    protected LogicInstruction lastInstruction(Predicate<LogicInstruction> matcher) {
        int result = lastInstructionIndex(matcher);
        return result < 0 ? null : program.get(result);
    }

    /**
     * Finds a first non-label instruction following a label.
     * Return null when not found
     */
    protected LogicInstruction labeledInstruction(LogicLabel label) {
        int index = labeledInstructionIndex(label);
        return index < 0 ? null : instructionAt(index);
    }
    /**
     * Return an independent list of instructions matching predicate.
     *
     * @param matcher predicate matching sought instructions
     * @return list of all instructions matching the predicate.
     */
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
                .filter(ix -> ix instanceof LabeledInstruction)
                .map(ix -> ((LabeledInstruction) ix).getLabel())
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
                    .filter(in -> in instanceof GotoLabelInstruction gl && gl.matches(ix))
                    .map(in -> (GotoLabelInstruction) in)
                    .map(GotoLabelInstruction::getLabel);
            default -> Stream.empty();
        };
    }
    //</editor-fold>

    //<editor-fold desc="Program modification">

    /**
     * Inserts a new instruction at given index. The instruction must be assigned an AST context suitable for its
     * position in the program. An instruction must not be placed into the program twice; when an instruction
     * truly needs to be duplicated, an independent copy with proper AST context needs to be created.
     *
     * @param index where to place the instruction
     * @param instruction instruction to add
     * @throws MindcodeInternalError when the new instruction is already present elsewhere in the program
     */
    protected void insertInstruction(int index, LogicInstruction instruction) {
        for (LogicInstruction logicInstruction : program) {
            if (logicInstruction == instruction) {
                throw new MindcodeInternalError("Trying to insert the same instruction twice.\n" + instruction);
            }
        }

        iterators.forEach(iterator -> iterator.instructionAdded(index));
        program.add(index, instruction);
        modifications++;
    }

    /**
     * Inserts all instruction in the list to the program, starting at given index.
     * See {@link #insertInstruction(int, LogicInstruction)}.
     *
     * @param index where to place the instructions
     * @param instructions instructions to add
     * @throws MindcodeInternalError when any of the new instructions is already present elsewhere in the program
     */
    protected void insertInstructions(int index, LogicList instructions) {
        for (LogicInstruction instruction : instructions) {
            insertInstruction(index++, instruction);
        }
    }

    /**
     * Replaces an instruction at given index. The replaced instruction should either reuse the AST context
     * of the original instruction at this position, or use a new one specifically created for the purpose
     * of the replacement.
     * <p>
     * Replacing an instruction with the same instruction isn't supported and causes an OptimizationException
     * to be thrown.
     *
     * @param index index of an instruction to be replaced
     * @param instruction new instruction for given index
     * @throws MindcodeInternalError when trying to replace an instruction with itself, or when the replaced
     * instruction is already present elsewhere in the program
     */
    protected void replaceInstruction(int index, LogicInstruction instruction) {
        for (LogicInstruction logicInstruction : program) {
            if (logicInstruction == instruction) {
                throw new MindcodeInternalError("Trying to insert the same instruction twice.\n" + instruction);
            }
        }
        program.set(index, instruction);
        modifications++;
    }

    /**
     * Removes an instruction at given index.
     *
     * @param index index of an instruction to be removed
     */
    protected void removeInstruction(int index) {
        iterators.forEach(iterator -> iterator.instructionRemoved(index));
        program.remove(index);
        modifications++;
    }

    /**
     * Inserts a new instruction before given, existing instruction. The new instruction must be assigned
     * an AST context suitable for its position in the program. An instruction must not be placed into the
     * program twice; when an instruction truly needs to be duplicated, an independent copy with proper
     * AST context needs to be created.
     * <p>
     * If the reference instruction isn't found in the program, an exception is thrown.
     *
     * @param anchor instruction before which to place the new instruction
     * @param inserted instruction to add
     */
    protected void insertBefore(LogicInstruction anchor, LogicInstruction inserted) {
        insertInstruction(existingInstructionIndex(anchor), inserted);
    }

    /**
     * Inserts a new instruction after given, existing instruction. The new instruction must be assigned
     * an AST context suitable for its position in the program. An instruction must not be placed into the
     * program twice; when an instruction truly needs to be duplicated, an independent copy with proper
     * AST context needs to be created.
     * <p>
     * If the reference instruction isn't found in the program, an exception is thrown.
     *
     * @param anchor instruction after which to place the new instruction
     * @param inserted instruction to add
     */
    protected void insertAfter(LogicInstruction anchor, LogicInstruction inserted) {
        insertInstruction(existingInstructionIndex(anchor) + 1, inserted);
    }

    /**
     * Replaces a given instruction with a new one. The new instruction should either reuse the AST context
     * of the original instruction at this position, or use a new one specifically created for the purpose
     * of the replacement.
     * <p>
     * If the original instruction isn't found in the program, an exception is thrown.
     *
     * @param original index of an instruction to be replaced
     * @param replaced new instruction for given index
     */
    protected void replaceInstruction(LogicInstruction original, LogicInstruction replaced) {
        replaceInstruction(existingInstructionIndex(original), replaced);
    }

    /**
     * Removes an existing instruction from the program. If the instruction isn't found, an exception is thrown.
     *
     * @param instruction instruction to be removed
     */
    protected void removeInstruction(LogicInstruction instruction) {
        removeInstruction(existingInstructionIndex(instruction));
    }

    /**
     * Removes an instruction immediately preceding given instruction.
     * If the given instruction isn't found, an exception is thrown.
     *
     * @param anchor the reference instruction
     */
    protected void removePrevious(LogicInstruction anchor) {
        removeInstruction(existingInstructionIndex(anchor) - 1);
    }

    /**
     * Removes an instruction immediately following given instruction.
     * If the given instruction isn't found, an exception is thrown.
     *
     * @param anchor the reference instruction
     */
    protected void removeFollowing(LogicInstruction anchor) {
        removeInstruction(existingInstructionIndex(anchor) + 1);
    }

    /**
     * Removes all instructions matching given predicate.
     *
     * @param matcher predicate to match instructions to be removed
     */
    protected void removeMatchingInstructions(Predicate<LogicInstruction> matcher) {
        for (int index = 0; index < program.size(); index++) {
            if (matcher.test(program.get(index))) {
                removeInstruction(index);
                index--;
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="Program modification using contexts">
    /**
     * Returns a label at the very beginning of the given AST context. If such label doesn't exist, creates it.
     * Note that the label must belong directly to the given context to be reused, labels belonging to
     * child contexts aren't considered. Care needs to be taken to provide correct context; for nodes modelled
     * with subcontexts a subcontext should be always provided.
     */
    protected LogicLabel obtainContextLabel(AstContext astContext) {
        LogicInstruction instruction = firstInstruction(astContext);
        if (instruction instanceof LabelInstruction label && label.getAstContext() == astContext) {
            return label.getLabel();
        } else {
            LogicLabel label = instructionProcessor.nextLabel();
            insertBefore(instruction, createLabel(astContext, label));
            return label;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Logic iterator">
    private final List<LogicIterator> iterators = new ArrayList<>();

    /**
     * Creates a new LogicIterator positioned at the start of the program.
     *
     * @return a new LogicIterator instance
     */
    protected LogicIterator createIterator() {
        return createIterator(0);
    }

    /**
     * Creates a new LogicIterator positioned at given instruction.
     *
     * @param instruction target instruction
     * @return LogicIterator positioned at given instruction
     */
    protected LogicIterator createIteratorAtInstruction(LogicInstruction instruction) {
        return createIterator(existingInstructionIndex(instruction));
    }

    /**
     * This class is modelled after ListIterator. Provides read-only functions at the moment.
     * All instances of ListIterator reflect changes to the underlying program (if possible).
     * When ListIterator points at certain instruction, if keeps pointing to it even though
     * some instructions are removed or added at positions preceding that of the LogicIterator,
     * regardless of how the modification was done (i.e. even modifications made though
     * different LogicIterator instance, or by calling BaseOptimizer methods).
     * <p>
     * LogicIterator instances can be only allocated from within the {@link #optimizeProgram()} method
     * and must be closed before the method finishes.
     */
    protected class LogicIterator implements ListIterator<LogicInstruction>, AutoCloseable {
        private int cursor;
        private boolean closed = false;
        private int lastRet = -1;

        private LogicIterator(int cursor) {
            this.cursor = cursor;
        }

        /**
         * Closes the LogicIterator.
         */
        @Override
        public void close() {
            closed = true;
            closeIterator(this);
        }

        /**
         * Creates an independent LogicIterator instance positioned at the same instruction as this instance.
         *
         * @return a new LogicIterator instance
         */
        public LogicIterator copy() {
            return createIterator(cursor);
        }

        /**
         * Returns the instruction at given offset from current position. Both positive and negative offsets are valid,
         * offset 0 returns the instruction that would be returned by calling {@link #next()}. If the resulting
         * instruction position lies outside the program range, null is returned and no exception is thrown.
         *
         * @param offset offset relative to current position
         * @return instruction at given offset relative to current position, or null
         */
        public LogicInstruction peek(int offset) {
            return instructionAt(cursor + offset);
        }

        /**
         * @return true if there's a next instruction.
         */
        public boolean hasNext() {
            checkClosed();
            return cursor < program.size();
        }

        /**
         * @return the next instruction
         * @throws NoSuchElementException when at the end of the program
         */
        public LogicInstruction next() {
            checkClosed();
            int i = cursor;
            if (i >= program.size()) {
                throw new NoSuchElementException();
            }
            cursor = i + 1;
            return program.get(lastRet = i);
        }

        /**
         * @return the index of the next instruction
         */
        @Override
        public int nextIndex() {
            checkClosed();
            return cursor;
        }

        /**
         * @return true if there's a previous instruction.
         */
        @Override
        public boolean hasPrevious() {
            checkClosed();
            return cursor != 0;
        }

        /**
         * @return the previous instruction
         * @throws NoSuchElementException when at the start of the program
         */
        public LogicInstruction previous() {
            checkClosed();
            int i = cursor - 1;
            if (i < 0) {
                throw new NoSuchElementException();
            }
            cursor = i;
            return program.get(lastRet = i);
        }

        /**
         * @return the index of the previous instruction
         */
        public int previousIndex() {
            checkClosed();
            return cursor - 1;
        }

        /**
         * Removes the last returned instruction. If no instruction was returned, or it was already removed,
         * an exception is thrown.
         */
        public void remove() {
            checkClosed();
            if (lastRet < 0) {
                throw new IllegalStateException();
            }
            removeInstruction(lastRet);
            // Cursor will be updated in instructionRemoved
            lastRet = -1;
        }

        /**
         * Replaces the last returned instruction with given instruction. If no instruction was returned,
         * or it was removed in the meantime, an exception is thrown.
         *
         * @param instruction instruction with which to replace the last returned instruction
         */
        @Override
        public void set(LogicInstruction instruction) {
            checkClosed();
            if (lastRet < 0) {
                throw new IllegalStateException();
            }
            replaceInstruction(lastRet, instruction);
        }

        /**
         * Inserts the specified instruction into the list.
         * The instruction is inserted immediately before the element that
         * would be returned by {@link #next}, if any, and after the element
         * that would be returned by {@link #previous}, if any.
         * The new element is inserted before the implicit
         * cursor: a subsequent call to {@code next} would be unaffected, and a
         * subsequent call to {@code previous} would return the new element.
         *
         * @param instruction the instruction to insert
         */
        @Override
        public void add(LogicInstruction instruction) {
            checkClosed();
            int index = cursor;
            insertInstruction(index, instruction);
            // Cursor will be updated in instructionAdded
            lastRet = -1;
        }

        /**
         * Provides a stream of instructions between this and upTo (inclusive at this, exclusive at end)
         */
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
                if (context.child(i).subcontextType() != types[i]) {
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
        forEachContext(rootContext, matcher, action);
    }

    protected void forEachContext(AstContextType contextType, AstSubcontextType subcontextType,
            Consumer<AstContext> action) {
        forEachContext(rootContext, c -> c.matches(contextType, subcontextType), action);
    }

    protected List<AstContext> contexts(Predicate<AstContext> matcher) {
        List<AstContext> contexts = new ArrayList<>();
        forEachContext(rootContext, matcher, contexts::add);
        return List.copyOf(contexts);
    }

    protected AstContext context(Predicate<AstContext> matcher) {
        List<AstContext> contexts = contexts(matcher);
        return switch (contexts.size()) {
            case 0 -> null;
            case 1 -> contexts.get(0);
            default -> throw new MindcodeInternalError("More than one context found.");
        };
    }

    /**
     * Creates a list of AST contexts representing inline functions.
     *
     * @return list of inline function node contexts
     */
    protected List<AstContext> getInlineFunctions() {
        return contexts(c -> c.contextType() == AstContextType.INLINED_CALL);
    }

    protected List<AstContext> getOutOfLineFunctions() {
        return contexts(c -> c.contextType() == AstContextType.FUNCTION);
    }
    //</editor-fold>


    //<editor-fold desc="Finding instructions by context">
    protected LogicList contextInstructions(AstContext astContext) {
        return astContext == null ? EMPTY :
                new LogicList(astContext, List.copyOf(instructionStream().filter(ix -> ix.matchesContext(astContext)).toList()));
    }

    protected Stream<LogicInstruction> contextStream(AstContext astContext) {
        return astContext == null ? Stream.empty() : instructionStream().filter(ix -> ix.matchesContext(astContext));
    }

    protected LogicInstruction firstInstruction(AstContext astContext) {
        return firstInstruction(ix -> ix.matchesContext(astContext));
    }

    protected LogicInstruction lastInstruction(AstContext astContext) {
        return lastInstruction(ix -> ix.matchesContext(astContext));
    }


    protected LogicInstruction instructionBefore(AstContext astContext) {
        return instructionAt(firstInstructionIndex(ix -> ix.matchesContext(astContext)) - 1);
    }

    protected LogicInstruction instructionAfter(AstContext astContext) {
        return instructionAt(lastInstructionIndex(ix -> ix.matchesContext(astContext)) + 1);
    }
    //</editor-fold>

    //<editor-fold desc="Logic list">
    private static final LogicList EMPTY = new LogicList(null, java.util.List.of());

    /**
     * Class for accessing context instructions in an organized manner.
     * Is always created from a specific AST context.
     * <p>
     * TODO allow modifications to be done to the LogicList - a block of code could be created in LogicList
     *      and then inserted into the program.
     */
    protected static class LogicList implements Iterable<LogicInstruction> {
        private final AstContext astContext;
        private final List<LogicInstruction> instructions;

        private LogicList(AstContext astContext, List<LogicInstruction> instructions) {
            this.astContext = astContext;
            this.instructions = instructions;
        }

        public AstContext getAstContext() {
            return astContext;
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

        public LogicInstruction getFromEnd(int index) {
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
            return new LogicList(astContext, instructions.subList(fromIndex, toIndex));
        }

        public List<LogicInstruction> toList() {
            return instructions;
        }

        public Stream<LogicInstruction> stream() {
            return instructions.stream();
        }

        public void forEach(Consumer<? super LogicInstruction> action) {
            instructions.forEach(action);
        }

        /**
         * Duplicates this logic list including the context structure. This <b>must</b> be used when duplicating
         * existing instructions, as reusing existing contexts might lead to context discontinuity (even when placing
         * the copy right before or after the original - in this case, encompassing context will be continuous, but
         * child contexts might not be).
         *
         * @return LogicList containing duplicated code.
         */
        public LogicList duplicate() {
            Map<AstContext, AstContext> contextMap = astContext.createDeepCopy();
            return new LogicList(contextMap.get(astContext),
                    stream().map(ix -> ix.withContext(contextMap.get(ix.getAstContext()))).toList());
        }

    }
    //</editor-fold>
}
