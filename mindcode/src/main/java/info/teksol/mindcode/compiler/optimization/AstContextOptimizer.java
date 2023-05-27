package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.MindcodeException;
import info.teksol.mindcode.compiler.instructions.AstContext;
import info.teksol.mindcode.compiler.instructions.AstContextType;
import info.teksol.mindcode.compiler.instructions.AstSubcontextType;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

// TODO Urgent: AST context structure needs to be updated when optimizers modify the program.
//      Doing it after each modification would be prohibitively expensive; we also expect the structure not to change
//      What if moving instructions around makes context discontinuous?
public abstract class AstContextOptimizer extends BaseOptimizer {

    public AstContextOptimizer(InstructionProcessor instructionProcessor) {
        super(instructionProcessor);
    }

    protected void insertInstructions(int index, LogicList instructions) {
        for (LogicInstruction instruction : instructions) {
            insertInstruction(index++, instruction);
        }
    }

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

    //<editor-fold desc="Finding contexts">
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

    private static final LogicList EMPTY = new LogicList(List.of());

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
}
