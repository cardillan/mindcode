package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.LogicInstructionPrinter;
import info.teksol.mindcode.compiler.MessageLevel;
import info.teksol.mindcode.compiler.generator.CallGraph;
import info.teksol.mindcode.compiler.generator.CallGraph.Function;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.compiler.optimization.DataFlowVariableStates.VariableStates;
import info.teksol.mindcode.compiler.optimization.DataFlowVariableStates.VariableStates.VariableValue;
import info.teksol.mindcode.logic.ArgumentType;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicBuiltIn;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicLiteral;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;

import java.util.*;
import java.util.stream.Collectors;

import static info.teksol.mindcode.compiler.instructions.AstSubcontextType.*;

public class DataFlowOptimizer extends BaseOptimizer {
    static final boolean DEBUG = false;

    /**
     * Instruction replacement map. All possible replacements are done after the data flow analysis is finished,
     * to avoid concurrent modification exceptions while updating the program during the analysis.
     */
    private final Map<LogicInstruction, LogicInstruction> replacements = new IdentityHashMap<>();

    /**
     * Set of instructions whose sole purpose is to set value to some variable. If the variable isn't subsequently
     * read, the instruction is useless and can be removed.
     */
    final Set<LogicInstruction> defines = Collections.newSetFromMap(new IdentityHashMap<>());

    /**
     * Set of variable producing instructions that were actually used in the program and need to be kept.
     */
    final Set<LogicInstruction> keep = Collections.newSetFromMap(new IdentityHashMap<>());

    /** Exceptions to the keep set */
    private final Set<LogicInstruction> useless = Collections.newSetFromMap(new IdentityHashMap<>());

    /**
     * Set of potentially uninitialized variables. In at least one branch the variable can be read without
     * being assigned a value first.
     */
    final Set<LogicVariable> uninitialized = new HashSet<>();

    /**
     * Maps labels to their respective instructions. Rebuilt at the beginning of each iteration. Allows quickly
     * determine the context of the label instruction and therefore jumps that lead outside their respective context
     * (non-localized jumps). Non-localized jumps are the product of break, continue or return statements.
     */
    private Map<LogicLabel, LogicInstruction> labels;

    /** Maps function prefix to a list of variables directly or indirectly read by the function */
    Map<String, Set<LogicVariable>> functionReads;

    /** Maps function prefix to a list of variables directly or indirectly written by the function */
    Map<String, Set<LogicVariable>> functionWrites;

    private final DataFlowVariableStates dataFlowVariableStates;
    private final DataFlowExpressionEvaluator expressionEvaluator;

    public DataFlowOptimizer(InstructionProcessor instructionProcessor) {
        super(instructionProcessor);
        dataFlowVariableStates = new DataFlowVariableStates(this);
        expressionEvaluator = new DataFlowExpressionEvaluator(instructionProcessor);
    }

    @Override
    protected boolean optimizeProgram() {
        defines.clear();
        keep.clear();
        useless.clear();
        uninitialized.clear();
        replacements.clear();

        debug("\n\n\n*** NEW ITERATION ***\n");

        analyzeFunctionVariables();

        labels = instructionStream()
                .filter(LabelInstruction.class::isInstance)
                .map(LabelInstruction.class::cast)
                .collect(Collectors.toMap(LabelInstruction::getLabel, ix -> ix));

        getRootContext().children().forEach(this::processTopContext);

        for (LogicInstruction instruction : defines) {
            // TODO create mechanism to identify instructions without side effects
            //      Will be used by the DeadCodeEliminator too!
            switch (instruction.getOpcode()) {
                case SET, OP, PACKCOLOR, READ -> {
                    if (!keep.contains(instruction) || useless.contains(instruction)) {
                        removeInstruction(instruction);
                    }
                }
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

    private void analyzeFunctionVariables() {
        functionReads = new HashMap<>();
        functionWrites = new HashMap<>();

        getRootContext().children().stream()
                .filter(c -> c.functionPrefix() != null)
                .forEachOrdered(this::analyzeFunctionVariables);

        while (propagateFunctionReadsAndWrites());
    }

    private boolean propagateFunctionReadsAndWrites() {
        CallGraph callGraph = getCallGraph();
        boolean modified = false;
        for (Function function : callGraph.getFunctions()) {
            if (!function.isInline()) {
                Set<LogicVariable> reads = functionReads.get(function.getLocalPrefix());
                Set<LogicVariable> writes = functionWrites.get(function.getLocalPrefix());
                int size = reads.size() + writes.size();
                function.getCalls().keySet().stream()
                        .filter(callGraph::containsFunction)            // Filter out built-in functions
                        .map(callGraph::getFunction)
                        .map(Function::getLocalPrefix)
                        .filter(Objects::nonNull)                       // Filter out main function
                        .forEachOrdered(callee -> {
                            reads.addAll(functionReads.get(callee));
                            writes.addAll(functionWrites.get(callee));
                        });

                // Repeat if there are changes
                modified |= size != reads.size() + writes.size();
            }
        }

        return modified;
    }

    private void analyzeFunctionVariables(AstContext context) {
        Set<LogicVariable> reads = new HashSet<>();
        Set<LogicVariable> writes = new HashSet<>();

        contextStream(context)
                .filter(ix -> !(ix instanceof PushOrPopInstruction))
                .forEach(ix -> {
                    ix.inputArgumentsStream()
                            .filter(LogicVariable.class::isInstance)
                            .map(LogicVariable.class::cast)
                            .forEachOrdered(reads::add);

                    ix.outputArgumentsStream()
                            .filter(LogicVariable.class::isInstance)
                            .map(LogicVariable.class::cast)
                            .forEachOrdered(writes::add);
                });

        functionReads.put(context.functionPrefix(), reads);
        functionWrites.put(context.functionPrefix(), writes);
    }

    protected void generateFinalMessages() {
        super.generateFinalMessages();

        String uninitializedList = uninitialized.stream()
                .filter(v -> v.getType() != ArgumentType.BLOCK)
                .filter(v -> !v.isGlobalVariable())
                .map(LogicVariable::getFullName)
                .distinct()
                .sorted()
                .collect(Collectors.joining(", "));

        if (!uninitializedList.isEmpty()) {
            emitMessage(MessageLevel.WARNING, "       List of uninitialized variables: %s.", uninitializedList);
        }
    }

    private void processTopContext(AstContext context) {
        VariableStates variableStates = dataFlowVariableStates.createVariableStates();
        if (context.functionPrefix() != null) {
            Function function = getCallGraph().getFunctionByPrefix(context.functionPrefix());
            function.getLogicParameters().forEach(variableStates::markInitialized);
        }

        variableStates = processContext(context, variableStates, true);
        useless.addAll(variableStates.getUseless().values());

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

        // We'll visit the entire loop twice. Second pass will generate reaches to values generated in first pass.
        for (int i = 0; i < 2; i++) {
            VariableStates initial = variableStates.copy();
            debug("=== Processing loop - iteration " + i);

            for (int j = start; j < children.size(); j++) {
                // Do not propagate constants on first iteration...
                variableStates = processContext(children.get(j), variableStates, propagateConstants && i > 0);
            }
            variableStates.merge(initial);
        }

        return variableStates;
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

        if (DEBUG) {
            System.out.println("Processing instruction #" + instructionIndex(instruction) + ": " + LogicInstructionPrinter.toString(instructionProcessor, instruction));
        }

        switch (instruction) {
            case PushInstruction ix:        variableStates.pushVariable(ix.getVariable()); return;
            case PopInstruction ix:         variableStates.popVariable(ix.getVariable()); return;
            case LabelInstruction ix:       return;
            case GotoLabelInstruction ix:   return;
            default:                        break;
        }

        // Process inputs first, to handle instructions reading and writing the same variable
        // Try to find possible replacements of input arguments to this instruction
        List<LogicVariable> inputs = instruction.inputArgumentsStream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .filter(this::canEliminate)
                .toList();

        Map<LogicVariable, LogicValue> valueReplacements = new HashMap<>();
        for (LogicVariable variable : inputs) {
            LogicValue constantValue = variableStates.valueRead(variable);
            if (constantValue != null) {
                valueReplacements.put(variable, constantValue);
            } else {
                LogicVariable equivalent = variableStates.findEquivalent(variable);
                if (equivalent != null && !equivalent.equals(variable)) {
                    valueReplacements.put(variable, equivalent);
                }
            }
        }

        // We're not evaluating PackColor, because the result can never be converted to mlog representation.
        LogicValue value = switch (replacements.getOrDefault(instruction, instruction)) {
            case SetInstruction set && set.getValue() instanceof LogicLiteral literal -> literal;
            case SetInstruction set && set.getValue() instanceof LogicBuiltIn builtIn && !builtIn.isVolatile() -> builtIn;
            case OpInstruction op && op.getOperation().isDeterministic() -> expressionEvaluator.evaluateOpInstruction(op);
            default -> null;
        };

        if (DEBUG) {
            if (!valueReplacements.isEmpty()) {
                System.out.println("    Detected the following possible value replacements for current instruction:");
                valueReplacements.forEach((k, v) -> System.out.println("       " + k.toMlog() + " --> " + v.toMlog()));
            }
        }

        instruction.outputArgumentsStream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .forEach(arg -> variableStates.valueSet(arg, instruction, value));

        switch (instruction.getOpcode()) {
            case CALL, CALLREC ->
                    variableStates.updateAfterFunctionCall(instruction.getAstContext().functionPrefix());
        }

        if (propagateConstants) {
            if (valueReplacements.values().stream().anyMatch(Objects::nonNull)) {
                List<LogicArgument> arguments = new ArrayList<>(instruction.getArgs());
                boolean updated = false;
                for (int i = 0; i < arguments.size(); i++) {
                    if (instruction.getParam(i).isInput() && arguments.get(i) instanceof LogicVariable variable
                            && valueReplacements.get(variable) != null) {
                        arguments.set(i, valueReplacements.get(variable));
                        updated = true;
                    }
                }

                if (updated) {
                    replacements.put(instruction, replaceArgs(instruction, arguments));
                }
            } else if (instruction instanceof OpInstruction op && op.hasSecondOperand()) {
                // Trying for extended evaluation
                OpInstruction newInstruction = expressionEvaluator.extendedEvaluate(variableStates, op);
                if (newInstruction != null) {
                    replacements.put(instruction, expressionEvaluator.normalize(newInstruction));
                }
            } else if (instruction instanceof SetInstruction set) {
                // Specific optimization to streamline self-modifying statements in recursive calls
                if (canEliminate(set.getResult()) && set.getValue().isTemporaryVariable()) {
                    VariableValue val = variableStates.findVariableValue(set.getValue());
                    if (val != null && val.isExpression() && val.getInstruction() instanceof OpInstruction op) {
                        if (op.inputArgumentsStream().anyMatch(set.getResult()::equals)) {
                            OpInstruction newInstruction = op.withContext(set.getAstContext()).withResult(set.getResult());
                            replacements.put(instruction, expressionEvaluator.normalize(newInstruction));
                        }
                    }
                }
            }
        }

        variableStates.print("  after processing instruction");
    }

    boolean canEliminate(LogicVariable variable) {
        // We need to protect return values of functions: when processing them, we have no information on
        // whether they're read. They're only useless if they aren't read at all, in which case they'll be removed
        // by DeadCodeEliminator.
        // STORED_RETVAL is removed, if possible, by ReturnValueOptimizer
        return switch (variable.getType()) {
            case COMPILER, FUNCTION_RETADDR, FUNCTION_RETVAL, GLOBAL_VARIABLE -> false;
            case LOCAL_VARIABLE -> aggressive() || !variable.isMainVariable();
            default -> true;
        };
    }

    private void debug(String text) {
        if (DEBUG) {
            System.out.println(text);
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
}
