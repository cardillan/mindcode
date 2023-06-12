package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.LogicInstructionPrinter;
import info.teksol.mindcode.compiler.MessageLevel;
import info.teksol.mindcode.compiler.instructions.AstContext;
import info.teksol.mindcode.compiler.instructions.AstContextType;
import info.teksol.mindcode.compiler.instructions.EndInstruction;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.JumpInstruction;
import info.teksol.mindcode.compiler.instructions.LabelInstruction;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.OpInstruction;
import info.teksol.mindcode.compiler.instructions.SetInstruction;
import info.teksol.mindcode.logic.ArgumentType;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicLiteral;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.processor.ExpressionEvaluator;

import java.util.*;
import java.util.stream.Collectors;

import static info.teksol.mindcode.compiler.instructions.AstSubcontextType.*;

public class DataFlowOptimizer extends BaseOptimizer {
    public static final boolean DEBUG = false;

    private final ExpressionValue expressionValue;

    public DataFlowOptimizer(InstructionProcessor instructionProcessor) {
        super(instructionProcessor);
        expressionValue = new ExpressionValue(instructionProcessor);
    }

    private final Map<LogicInstruction, LogicInstruction> replacements = new IdentityHashMap<>();

    /**
     * List of instructions defining some variable
     */
    private final Set<LogicInstruction> defines = Collections.newSetFromMap(new IdentityHashMap<>());

    /**
     * List of instructions that need to be kept
     */
    private final Set<LogicInstruction> keep = Collections.newSetFromMap(new IdentityHashMap<>());

    /**
     * List of uninitialized variables
     */
    private final Set<LogicVariable> uninitialized = new HashSet<>();

    private Deque<VariableStates> stateStack;

    private Map<LogicLabel, LogicInstruction> labels;

    @Override
    protected boolean optimizeProgram() {
        defines.clear();
        keep.clear();
        uninitialized.clear();

        labels = instructionStream()
                .filter(LabelInstruction.class::isInstance)
                .map(LabelInstruction.class::cast)
                .collect(Collectors.toMap(LabelInstruction::getLabel, ix -> ix));

        getRootContext().children().forEach(this::processTopContext);

        for (LogicInstruction instruction : defines) {
            // Only remove Set and Op for now
            // TODO create mechanism to identify instructions without side effects
            if ((instruction instanceof SetInstruction || instruction instanceof OpInstruction) && !keep.contains(instruction)) {
                removeInstruction(instruction);
            }
        }

        replacements.forEach((original, replacement) -> {
            int index = instructionIndex(original);
            if (index >= 0) {
                replaceInstruction(index, replacement);
            }
        });

        return true;
    }

    protected void generateFinalMessages() {
        super.generateFinalMessages();

        String uninitializedList = uninitialized.stream()
                .filter(v -> v.getType() != ArgumentType.BLOCK)
                .filter(v -> !v.isGlobalVariable())
                .map(LogicVariable::getFullName)
                .sorted()
                .distinct()
                .collect(Collectors.joining(", "));

        if (!uninitializedList.isEmpty()) {
            emitMessage(MessageLevel.WARNING, "       List of uninitialized variables: %s.", uninitializedList);
        }
    }

    private void processTopContext(AstContext context) {
        VariableStates variableStates = processContext(context, new VariableStates(), true);

        if (context.functionPrefix() == null) {
            // This is the main program context.
            // Variables that were not initialized might be expected to keep their value
            // on program restart (when reaching an end of instruction list/end instruction).
            // Marking them as read now will preserve the last assigned value.
            List.copyOf(uninitialized).forEach(variableStates::valueRead);
        }
    }

    private VariableStates processContext(AstContext context, VariableStates variableStates, boolean propagateConstants) {
        Objects.requireNonNull(variableStates);
        if (!context.matches(BASIC)) {
            return processDefaultContext(context, variableStates, propagateConstants);
        } else {
            return switch (context.contextType()) {
                case LOOP -> processLoopContext(context, variableStates, propagateConstants);
                case IF -> processIfContext(context, variableStates, propagateConstants);
                case CASE -> processCaseContext(context, variableStates, propagateConstants);
                default -> processDefaultContext(context, variableStates, propagateConstants);
            };
        }
    }

    private VariableStates processLoopContext(AstContext context, VariableStates variableStates, boolean propagateConstants) {
        List<AstContext> children = context.children();
        int start = 0;
        if (!children.isEmpty() && children.get(0).matches(INIT)) {
            variableStates = processContext(children.get(0), variableStates, propagateConstants);
            start++;
        }

        // If there are two CONDITION contexts, the first one gets executed only once
        if (children.stream().filter(ctx -> ctx.matches(CONDITION)).count() > 1) {
            if (!children.get(start).matches(CONDITION)) {
                throw new MindcodeInternalError("Expected CONDITION context, found " + children.get(start));
            }
            variableStates = processContext(children.get(start++), variableStates, propagateConstants);
        }

        VariableStates initial = variableStates.copy();

        // We'll visit the entire loop twice. Second pass will generate reaches to values generated in first pass.
        for (int i = 0; i < 2; i++) {
            for (int j = start; j < children.size(); j++) {
                // Do not propagate constants on first iteration...
                variableStates = processContext(children.get(j), variableStates, i > 0);
            }
            initial.merge(variableStates);
        }

        return initial;
    }

    private VariableStates processIfContext(AstContext context, VariableStates variableStates, boolean propagateConstants) {
        Iterator<AstContext> children = context.children().iterator();
        boolean foundCondition = false;

        // Process all contexts up to the condition
        while (children.hasNext()) {
            AstContext child = children.next();
            variableStates = processContext(child, variableStates, propagateConstants);
            if (child.matches(CONDITION)) {
                foundCondition = true;
                break;
            }
        }

        // We got a degenerated If context - the condition might have been removed by constant expression optimization.
        if (!foundCondition) {
            return variableStates;
        }

        BranchedVariableStates branchedStates = new BranchedVariableStates(variableStates);
        boolean wasBody = false;
        int bodies = 0;

        while (children.hasNext()) {
            AstContext child = children.next();
            switch (child.subcontextType()) {
                case BODY -> {
                    if (wasBody) {
                        throw new MindcodeInternalError("Expected FLOW_CONTROL, found BODY subcontext in IF context %s", context);
                    }
                    branchedStates.newBranch();
                    branchedStates.processContext(child, propagateConstants);
                    wasBody = true;
                    bodies++;
                }
                case FLOW_CONTROL -> {
                    branchedStates.processContext(child, propagateConstants);
                    wasBody = false;
                }
                default -> throw new MindcodeInternalError("Unexpected subcontext %s in IF context", child);
            }
        }

        // If there's only one body, start a new, empty branch. This will merge the initial state
        // into the final state, covering the case where the one body was skipped.
        if (bodies == 1) {
            branchedStates.newBranch();
        }

        return branchedStates.getFinalStates();
    }

    private VariableStates processCaseContext(AstContext context, VariableStates variableStates, boolean propagateConstants) {
        List<AstContext> children = context.children();
        Iterator<AstContext> iterator = children.iterator();
        if (!children.isEmpty() && children.get(0).matches(INIT)) {
            AstContext child = iterator.next();
            variableStates = processContext(child, variableStates, propagateConstants);
        }

        BranchedVariableStates branchedStates = new BranchedVariableStates(variableStates);
        boolean hasElse = false;

        while (iterator.hasNext()) {
            AstContext child = iterator.next();
            switch (child.subcontextType()) {
                case CONDITION -> {
                    branchedStates.appendToInitialContext(child, propagateConstants);
                    branchedStates.newBranch();
                }
                case BODY, FLOW_CONTROL -> {
                    branchedStates.processContext(child, propagateConstants);
                }
                case ELSE -> {
                    branchedStates.newBranch();
                    branchedStates.processContext(child, propagateConstants);
                    hasElse = true;
                }
                default -> throw new MindcodeInternalError("Unexpected subcontext %s in IF context", child);
            }
        }

        // If there's no else branch, start a new, empty branch representing the missing else.
        if (!hasElse) {
            branchedStates.newBranch();
        }

        return branchedStates.getFinalStates();
    }

    private VariableStates processDefaultContext(AstContext context, VariableStates variableStates, boolean propagateConstants) {
        Objects.requireNonNull(variableStates);
        LogicList instructions = contextInstructions(context);
        List<VariableStates> exitPoints = new ArrayList<>();

        int index = 0;
        while (index < instructions.size()) {
            LogicInstruction instruction = instructions.get(index);
            if (isJumpOutside(context, instruction)) {
                exitPoints.add(variableStates.copy());
            }

            if (instruction.getAstContext() == context) {
                processInstruction(variableStates, instruction, propagateConstants);
                index++;
            } else {
                AstContext childContext = context.findMatchingChild(instruction.getAstContext());
                variableStates = processContext(childContext, variableStates, propagateConstants);

                boolean isExitPoint =
                        childContext.matches(AstContextType.RETURN, BASIC) ||
                                childContext.matches(AstContextType.BREAK, BASIC) ||
                                childContext.matches(AstContextType.CONTINUE, BASIC);

                // Skip the rest of this context
                while (index < instructions.size() && instructions.get(index).belongsTo(childContext)) {
                    isExitPoint |= isJumpOutside(context, instructions.get(index));
                    index++;
                }

                if (isExitPoint) {
                    exitPoints.add(variableStates.copy());
                }
            }
        }

        exitPoints.forEach(variableStates::merge);

        return variableStates;
    }

    private boolean isJumpOutside(AstContext context, LogicInstruction ix) {
        return ix instanceof JumpInstruction jump && !labels.get(jump.getTarget()).belongsTo(context)
                || ix instanceof EndInstruction end && !end.getAstContext().matches(END);
    }

    private void processInstruction(VariableStates variableStates, LogicInstruction instruction, boolean propagateConstants) {
        Objects.requireNonNull(variableStates);
        Objects.requireNonNull(instruction);

        p(instruction);

        // Process inputs first, to handle instructions reading and writing the same variable (e.g. op add i i 1)
        List<LogicVariable> inputs = instruction.inputArgumentsStream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .toList();

        Map<LogicVariable, LogicLiteral> literals = new HashMap<>();
        for (LogicVariable variable : inputs) {
            LogicLiteral literal = variableStates.valueRead(variable);
            if (literal != null && !variable.isGlobalVariable()) {
                literals.put(variable, literal);
            }
        }

        LogicLiteral value = switch (replacements.getOrDefault(instruction, instruction)) {
            case SetInstruction set && set.getValue() instanceof LogicLiteral literal -> literal;
            case OpInstruction op && op.getOperation().isDeterministic() -> evaluateOpInstruction(op);
            default -> null;
        };

        instruction.outputArgumentsStream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .forEach(arg -> variableStates.valueSet(arg, instruction, value));

        if (propagateConstants && literals.values().stream().anyMatch(v -> v != null)) {
            List<LogicArgument> arguments = new ArrayList<>(instruction.getArgs());
            boolean updated = false;
            for (int i = 0; i < arguments.size(); i++) {
                if (instruction.getParam(i).isInput() && arguments.get(i) instanceof LogicVariable variable
                        && literals.get(variable) != null) {
                    arguments.set(i, literals.get(variable));
                    updated = true;
                }
            }

            if (updated) {
                replacements.put(instruction, replaceArgs(instruction, arguments));
            }
        }
    }

    private LogicLiteral evaluateOpInstruction(OpInstruction op) {
        if (op.getX() instanceof LogicLiteral x && x.isNumericLiteral()) {
            if (op.hasSecondOperand()) {
                if (op.getY() instanceof LogicLiteral y && y.isNumericLiteral()) {
                    ExpressionEvaluator.getOperation(op.getOperation()).execute(expressionValue, x, y);
                    return expressionValue.getLiteral();
                }
            } else {
                ExpressionEvaluator.getOperation(op.getOperation()).execute(expressionValue, x, x);
                return expressionValue.getLiteral();
            }
        }

        return null;
    }

    private boolean canEliminate(LogicVariable variable, LogicInstruction instruction) {
        return switch (variable.getType()) {
            case COMPILER, STORED_RETVAL, FUNCTION_RETADDR, FUNCTION_RETVAL, GLOBAL_VARIABLE -> false;
            case LOCAL_VARIABLE -> canEliminateLocalVariable(variable, instruction);
            default -> true;
        };
    }

    private boolean canEliminateLocalVariable(LogicVariable variable, LogicInstruction instruction) {
        if (variable.isMainVariable()) {
            return aggressive();
        } else {
            // Must not eliminate a function argument being set up
            // Determining recursive function argument being set up is trickier
            // TODO Allow optimization of nested inline function arguments
            return !instruction.getAstContext().matchesRecursively(OUT_OF_LINE_CALL, RECURSIVE_CALL);
        }
    }

    private void p(LogicInstruction instruction) {
        if (DEBUG) {
            System.out.println("Processing instruction #" + instructionIndex(instruction) + ": " + LogicInstructionPrinter.toString(instructionProcessor, instruction));
        }
    }

    private class BranchedVariableStates {
        private VariableStates initial;
        private VariableStates current;
        private VariableStates merged;

        public BranchedVariableStates(VariableStates initial) {
            this.initial = initial;
        }

        public void newBranch() {
            if (current != null) {
                merged = merged == null ? current : merged.merge(current);
            }
            current = initial.copy();
        }

        public void appendToInitialContext(AstContext context, boolean propagateConstants) {
            initial = DataFlowOptimizer.this.processContext(context, initial, propagateConstants);
        }

        public void processContext(AstContext context, boolean propagateConstants) {
            current = DataFlowOptimizer.this.processContext(context,
                    current == null ? initial.copy() : current, propagateConstants);
        }

        public VariableStates getFinalStates() {
            newBranch();
            return merged == null ? initial : merged;
        }
    }

    private class VariableStates {
        private final Map<LogicVariable, LogicLiteral> values;

        /**
         * Maps variables to a list of instructions defining its current value
         */
        private final Map<LogicVariable, List<LogicInstruction>> definitions;

        /**
         * Set of initialized variables
         */
        private final Set<LogicVariable> initialized;

        public VariableStates() {
            values = new HashMap<>();
            definitions = new HashMap<>();
            initialized = new HashSet<>();
        }

        private VariableStates(VariableStates other) {
            values = new HashMap<>(other.values);
            definitions = new HashMap<>(other.definitions);
            initialized = new HashSet<>(other.initialized);
        }

        public VariableStates copy() {
            return new VariableStates(this);
        }

        private void p(LogicInstruction instruction) {
            if (DEBUG) {
                System.out.println("   " + instructionIndex(instruction) + ": " + LogicInstructionPrinter.toString(instructionProcessor, instruction));
            }
        }

        public void valueSet(LogicVariable variable, LogicInstruction instruction, LogicLiteral value) {
            if (DEBUG) {
                System.out.println("Value set: " + variable);
                p(instruction);
            }

            if (canEliminate(variable, instruction)) {
                values.put(variable, value);
                defines.add(instruction);
            }
            initialized.add(variable);
            definitions.put(variable, List.of(instruction));
        }

        public LogicLiteral valueRead(LogicVariable variable) {
            if (DEBUG) {
                System.out.println("Value read: " + variable);
            }
            if (!initialized.contains(variable)) {
                uninitialized.add(variable);
            }

            if (definitions.containsKey(variable)) {
                if (DEBUG) {
                    definitions.get(variable).forEach(this::p);
                }
                keep.addAll(definitions.get(variable));     // We need to keep all defining instructions
            }

            return values.get(variable);
        }

        /**
         * Merges two variable states. Each state was produced by a code path, and it isn't known which one was
         * executed.
         *
         * @param other states to merge to this one.
         */
        public VariableStates merge(VariableStates other) {
            print("*** Merge:\n  this: ");
            other.print("  other:");

            for (LogicVariable variable : other.definitions.keySet()) {
                if (definitions.containsKey(variable)) {
                    List<LogicInstruction> current = definitions.get(variable);
                    List<LogicInstruction> theOther = other.definitions.get(variable);
                    if (!sameInstances(current, theOther)) {
                        // Merge the two lists
                        if (current.size() == 1 && theOther.size() == 1) {
                            definitions.put(variable, List.of(current.get(0), theOther.get(0)));
                        } else {
                            Set<LogicInstruction> union = Collections.newSetFromMap(new IdentityHashMap<>(current.size() + theOther.size()));
                            union.addAll(current);
                            union.addAll(theOther);
                            definitions.put(variable, List.copyOf(union));
                        }
                    }
                } else {
                    definitions.put(variable, other.definitions.get(variable));
                }
            }

            // Only keep values that are the same in both instances
            values.keySet().retainAll(other.values.keySet());
            for (LogicVariable variable : other.values.keySet()) {
                if (!Objects.equals(values.get(variable), other.values.get(variable))) {
                    values.remove(variable);
                }
            }

            // Variable is initialized only if it was initialized by both code paths
            initialized.retainAll(other.initialized);

            print("  result:");

            return this;
        }

        private void print(String title) {
            if (DEBUG) {
                System.out.println(title);
                definitions.forEach((k, v) -> {
                    System.out.println("    Variable " + k.getFullName());
                    v.forEach(ix -> {
                        System.out.println("      " + instructionIndex(ix) + ": " + LogicInstructionPrinter.toString(instructionProcessor, ix));
                    });
                });
                System.out.println("    Initialized: " + initialized.stream().map(LogicVariable::getFullName).collect(Collectors.joining(", ")));
            }
        }

        private <E> boolean sameInstances(List<E> list1, List<E> list2) {
            if (list1.size() != list2.size()) {
                return false;
            }

            for (int i = 0; i < list1.size(); i++) {
                if (list1.get(i) != list2.get(i)) {
                    return false;
                }
            }

            return true;
        }
    }
}
