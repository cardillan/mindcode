package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.MessageLevel;
import info.teksol.mindcode.compiler.instructions.GotoInstruction;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.JumpInstruction;
import info.teksol.mindcode.compiler.instructions.LabelInstruction;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.LogicLabel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static info.teksol.util.CollectionUtils.findFirstIndex;


// Base class for global optimizers. Consumes the entire program before performing optimizations on it.
// Contains helper method to navigate and manipulate the program.
abstract class GlobalOptimizer extends BaseOptimizer {
    private final List<LogicInstruction> program = new ArrayList<>();
    private int iterations = 0;
    private int modifications = 0;
    private int original;
    private int emitted;
    
    public GlobalOptimizer(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        super(instructionProcessor, next);
    }

    @Override
    public final void emit(LogicInstruction instruction) {
        program.add(instruction);
    }

    @Override
    public final void flush() {
        original = (int) program.stream().filter(ix -> !(ix instanceof LabelInstruction)).count();

        boolean repeat;
        do {
            modifications = 0;
            repeat = optimizeProgram();
            if (!iterators.isEmpty()) {
                throw new IllegalStateException("Unclosed iterators.");
            }
            debugPrinter.iterationFinished(this, ++iterations, program);
        } while (repeat && modifications > 0);

        emitted = (int) program.stream().filter(ix -> !(ix instanceof LabelInstruction)).count();

        generateFinalMessages();
        
        program.forEach(this::emitToNext);
        program.clear();
        super.flush();
    }

    /**
     * Performs one iteration of the optimization. Return true to run another iteration, false when done.
     * @return true to re-run the optimization
     */
    protected abstract boolean optimizeProgram();

    protected void generateFinalMessages() {
        if (emitted != original) {
            String verb = emitted < original ? "eliminated" : "added";
            if (iterations > 1) {
                emitMessage(MessageLevel.INFO, "%6d instructions %s by %s (%d iterations).",
                        Math.abs(original - emitted), verb, getClass().getSimpleName(), iterations - 1);
            } else {
                emitMessage(MessageLevel.INFO, "%6d instructions %s by %s.",
                        Math.abs(original - emitted), verb, getClass().getSimpleName());
            }
        }
    }

    /**
     * Determines whether the code block is localized, i.e. all effects that can happen inside the block are contained
     * in the block itself. In other word, all jumps that target a label inside the code block originate
     * in the code block.
     *
     * @param codeBlock code block to inspect
     * @return true if the block is localized
     */
    protected boolean isLocalized(List<LogicInstruction> codeBlock) {
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

    protected LogicInstruction getInstruction(int index) {
        return program.get(index);
    }

    protected Stream<LogicInstruction> instructionStream() {
        return program.stream();
    }

    protected List<LogicInstruction> instructionSubList(int fromIndex, int toIndex) {
        return Collections.unmodifiableList(program.subList(fromIndex, toIndex));
    }

    // Starting at given index, finds first instruction matching predicate.
    // Returns the index or -1 if not found.
    protected int findInstructionIndex(int startIndex, Predicate<LogicInstruction> matcher) {
        return findFirstIndex(program, startIndex, matcher);
    }
    
    // Starting at given index, find first instruction matching predicate. Return null if not found.
    protected LogicInstruction findInstruction(int startIndex, Predicate<LogicInstruction> matcher) {
        int result = findInstructionIndex(startIndex, matcher);
        return result < 0 ? null : program.get(result);
    }

    // Finds a first non-label instruction following a label
    // Returns the index or -1 if not found.
    protected int findLabeledInstructionIndex(LogicLabel label) {
        int labelIndex = findInstructionIndex(0, ix -> ix instanceof LabelInstruction li && li.getLabel().equals(label));
        return labelIndex >= 0 ? findInstructionIndex(labelIndex + 1, ix -> !(ix instanceof LabelInstruction)) : -1;
    }

    // Return list of instructions matching predicate
    protected List<LogicInstruction> findInstructions(Predicate<LogicInstruction> matcher) {
        return program.stream().filter(matcher).toList();
    }

    protected void replaceInstruction(LogicInstruction original, LogicInstruction replaced) {
        for (int index = 0; index < program.size(); index++) {
            if (program.get(index) == original) {
                program.set(index, replaced);
                modifications++;
                return;
            }
        }
        
        throw new OptimizationException("Instruction to be replaced not found in program." +
                "\nOriginal instruction: " + original +
                "\nReplacement instruction: " + replaced);
    }

    protected void removeInstruction(LogicInstruction original) {
        for (int index = 0; index < program.size(); index++) {
            if (program.get(index) == original) {
                removeInstruction(index);
                return;
            }
        }

        throw new OptimizationException("Instruction to be removed not found in program." +
                "\nInstruction: " + original);
    }

    protected void removeMatchingInstructions(Predicate<LogicInstruction> matcher) {
        for (int index = 0; index < program.size(); index++) {
            if (matcher.test(program.get(index))) {
                removeInstruction(index);
            }
        }
    }

    protected LogicIterator createIterator() {
        return createIterator(0);
    }

    protected LogicIterator createIteratorAtLabel(LogicLabel label) {
        int index = findInstructionIndex(0, ix -> ix instanceof LabelInstruction lx && lx.getLabel().equals(label));
        return index < 0 ? null : createIterator(index);
    }

    protected LogicIterator createIteratorAtLabelledInstruction(LogicLabel label) {
        int index = findLabeledInstructionIndex(label);
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

        // Looks at instruction at given offset
        public LogicInstruction peek(int offset) {
            int index = cursor + offset;
            return index >= 0 && index < program.size() ? program.get(index) : null;
        }

        public boolean hasNext() {
            checkClosed();
            return cursor != program.size();
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

    private List<LogicIterator> iterators = new ArrayList<>();

    private LogicIterator createIterator(int index) {
        LogicIterator iterator = new LogicIterator(index);
        iterators.add(iterator);
        return iterator;
    }

    private void closeIterator(LogicIterator iterator) {
        if (!iterators.remove(iterator)) {
            throw new IllegalStateException("Trying to close unregistered iterator.");
        }
    }

    private void insertInstruction(int index, LogicInstruction instruction) {
        iterators.forEach(iterator -> iterator.instructionAdded(index));
        program.add(index, instruction);
        modifications++;
    }

    protected void removeInstruction(int index) {
        iterators.forEach(iterator -> iterator.instructionRemoved(index));
        program.remove(index);
        modifications++;
    }

    private void replaceInstruction(int index, LogicInstruction instruction) {
        program.set(index, instruction);
        modifications++;
    }
}
