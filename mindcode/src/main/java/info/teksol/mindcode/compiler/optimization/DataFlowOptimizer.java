package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.LogicInstructionPrinter;
import info.teksol.mindcode.compiler.MessageLevel;
import info.teksol.mindcode.compiler.generator.CallGraph;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.compiler.optimization.DataFlowVariableStates.VariableStates;
import info.teksol.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mindcode.logic.*;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static info.teksol.mindcode.compiler.instructions.AstSubcontextType.*;
import static info.teksol.util.CollectionUtils.in;

public class DataFlowOptimizer extends BaseOptimizer {
    static final boolean DEBUG = false;

    /**
     * Stores possible replacement values to each input argument of a replaceable instruction.
     */
    private final Map<LogicInstruction, Map<LogicVariable, LogicValue>> replacements = new IdentityHashMap<>();

    /**
     * Set of instructions whose sole purpose is to set value to some variable. If the variable isn't subsequently
     * read, the instruction is useless and can be removed.
     */
    final Set<LogicInstruction> defines = Collections.newSetFromMap(new IdentityHashMap<>());

    /**
     * Set of variable producing instructions that were actually used in the program and need to be kept.
     */
    final Set<LogicInstruction> keep = Collections.newSetFromMap(new IdentityHashMap<>());

    /**
     * When an END instruction is encountered, the optimizer accumulates current variable definitions here. Definitions
     * from this structure are kept for uninitialized variables, so that any values written before the END instruction
     * executions are preserved.
     * <p/>
     * Only main variables are stored here. Global variable writes are always preserved, and local variables are not
     * expected to keep their value between function calls.
     */
    final Map<LogicVariable, List<LogicInstruction>> orphans = new HashMap<>();

    /** Exceptions to the keep set */
    private final Set<LogicInstruction> useless = Collections.newSetFromMap(new IdentityHashMap<>());

    /**
     * Set of potentially uninitialized variables. In at least one branch the variable can be read without
     * being assigned a value first.
     */
    final Set<LogicVariable> uninitialized = new HashSet<>();

    /**
     * Instructions referencing each variable.
     */
    final Map<LogicVariable, List<LogicInstruction>> references = new HashMap<>();

    /**
     * When a jump outside its context is encountered, current variable state is copied and assigned to the target
     * label. Upon encountering the target label all stored states are merged and flushed.
     * Unresolved label indicates a problem in the data flow analysis -- this would happen if the jump targeted an
     * earlier label. LogicInstructionGenerator should never generate such code.
     */
    private final Map<LogicLabel, List<VariableStates>> labelStates = new HashMap<>();

    /** Maps function prefix to a list of variables directly or indirectly read by the function */
    Map<String, Set<LogicVariable>> functionReads;

    /** Maps function prefix to a list of variables directly or indirectly written by the function */
    Map<String, Set<LogicVariable>> functionWrites;

    /** Contains function prefix of functions that may directly or indirectly call the end() instruction. */
    private Set<String> functionEnds;

    private final List<VariableStates> functionEndStates = new ArrayList<>();

    private final DataFlowVariableStates dataFlowVariableStates;

    public DataFlowOptimizer(OptimizationContext optimizationContext) {
        super(Optimization.DATA_FLOW_OPTIMIZATION, optimizationContext);
        dataFlowVariableStates = new DataFlowVariableStates(this);
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase, int pass, int iteration) {
        defines.clear();
        keep.clear();
        orphans.clear();
        useless.clear();
        uninitialized.clear();
        replacements.clear();
        references.clear();
        labelStates.clear();
        functionEndStates.clear();

        clearVariableStates();

        debug(() -> "\n\n\n*** PASS " + pass + ", ITERATION " + iteration + " ***\n");

        analyzeFunctionVariables();

        getRootContext().children().forEach(this::processTopContext);

        // Keep defining instructions for orphaned, uninitialized variables.
        orphans.entrySet().stream()
                .filter(e -> uninitialized.contains(e.getKey()))
                .forEachOrdered(e -> keep.addAll(e.getValue()));

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

        instructionStream()
                .filter(ix -> replacements.containsKey(ix) || getVariableStates(ix) != null)
                .forEachOrdered(this::replaceInstruction);

        return wasUpdated();
    }

    private void replaceInstruction(LogicInstruction instruction) {
        int index = instructionIndex(instruction);
        if (index < 0) return;

        Map<LogicVariable, LogicValue> valueReplacements = replacements.get(instruction);
        if (valueReplacements != null) {
            boolean updated = false;
            List<LogicArgument> arguments = new ArrayList<>(instruction.getArgs());
            for (int i = 0; i < arguments.size(); i++) {
                LogicValue replacement;
                if (instruction.getParam(i).isInput() && arguments.get(i) instanceof LogicVariable variable
                        && (replacement = valueReplacements.get(variable)) != null) {
                    arguments.set(i, replacement);
                    updated = true;
                }
            }

            if (updated) {
                replaceInstruction(index, replaceArgs(instruction, arguments));
            }
        } else if (instruction instanceof OpInstruction op && op.hasSecondOperand()) {
            // Trying for extended evaluation
            VariableStates variableStates = Objects.requireNonNull(getVariableStates(instruction));
            OpInstruction newInstruction = extendedEvaluate(variableStates, op);
            if (newInstruction != null && canReplace(instruction, newInstruction)) {
                replaceInstruction(index, normalize(newInstruction));
            }
        } else if (instruction instanceof SetInstruction set) {
            // Specific optimization to streamline self-modifying statements in recursive calls
            VariableStates variableStates = Objects.requireNonNull(getVariableStates(instruction));
            if (canEliminate(set.getResult()) && set.getValue().isTemporaryVariable()) {
                VariableStates.VariableValue val = variableStates.findVariableValue(set.getValue());
                if (val != null && val.isExpression() && val.getInstruction() instanceof OpInstruction op) {
                    if (op.inputArgumentsStream().anyMatch(set.getResult()::equals)) {
                        OpInstruction newInstruction = op.withContext(set.getAstContext()).withResult(set.getResult());
                        replaceInstruction(index, normalize(newInstruction));
                    }
                }
            }
        }
    }

    private int countReferences(LogicVariable variable) {
        return references.containsKey(variable) ? references.get(variable).size() : 0;
    }

    private boolean canReplace(LogicInstruction original, LogicInstruction replacement) {
        int maxOriginal = original.inputArgumentsStream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .filter(Predicate.not(replacement.getArgs()::contains))
                .mapToInt(this::countReferences)
                .max().orElse(0);

        int maxReplacement = replacement.inputArgumentsStream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .filter(Predicate.not(original.getArgs()::contains))
                .mapToInt(this::countReferences)
                .max().orElse(0);

        return maxReplacement >= maxOriginal || maxOriginal < 2;
    }

    private void analyzeFunctionVariables() {
        functionReads = new HashMap<>();
        functionWrites = new HashMap<>();
        functionEnds = new HashSet<>();

        getRootContext().children().stream()
                .filter(c -> c.functionPrefix() != null)
                .forEachOrdered(this::analyzeFunctionVariables);

        while (propagateFunctionReadsAndWrites());
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

                    if (ix instanceof EndInstruction) {
                        functionEnds.add(context.functionPrefix());
                    }
                });

        functionReads.put(context.functionPrefix(), reads);
        functionWrites.put(context.functionPrefix(), writes);
    }


    private boolean propagateFunctionReadsAndWrites() {
        CallGraph callGraph = getCallGraph();
        boolean modified = false;
        for (CallGraph.Function function : callGraph.getFunctions()) {
            if (!function.isInline()) {
                Set<LogicVariable> reads = functionReads.computeIfAbsent(function.getLocalPrefix(), f -> new HashSet<>());
                Set<LogicVariable> writes = functionWrites.computeIfAbsent(function.getLocalPrefix(), f -> new HashSet<>());
                int size = reads.size() + writes.size() + functionEnds.size();
                function.getCalls().keySet().stream()
                        .filter(callGraph::containsFunction)            // Filter out built-in functions
                        .map(callGraph::getFunction)
                        .map(CallGraph.Function::getLocalPrefix)
                        .filter(Objects::nonNull)                       // Filter out main function
                        .forEachOrdered(callee -> {
                            reads.addAll(functionReads.get(callee));
                            writes.addAll(functionWrites.get(callee));
                            if (functionEnds.contains(callee)) {
                                functionEnds.add(function.getLocalPrefix());
                            }
                        });

                // Repeat if there are changes
                modified |= size != reads.size() + writes.size() + functionEnds.size();
            }
        }

        return modified;
    }

    public void generateFinalMessages() {
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
            CallGraph.Function function = getCallGraph().getFunctionByPrefix(context.functionPrefix());
            function.getLogicParameters().forEach(variableStates::markInitialized);
        }

        variableStates = processContext(context, context, variableStates, true);
        useless.addAll(variableStates.getUseless().values());

        if (context.functionPrefix() == null) {
            // This is the main program context.
            // Variables that were not initialized might be expected to keep their value
            // on program restart (when reaching an end of instruction list/end instruction).
            // Marking them as read now will preserve the last assigned value.
            List.copyOf(uninitialized).forEach(variableStates::valueRead);
            functionEndStates.forEach(vs -> List.copyOf(uninitialized).forEach(vs::valueRead));

            if (!aggressive()) {
                // On basic optimization level, provide some protection to main variables.
                // Specifically, latest values assigned to main variables are preserved.
                // Variables that were part of unrolled loops are NOT preserved, regardless of their other use.
                VariableStates finalVariableStates = variableStates;
                contextStream(context)
                        .flatMap(LogicInstruction::outputArgumentsStream)
                        .filter(LogicVariable.class::isInstance)
                        .map(LogicVariable.class::cast)
                        .filter(LogicVariable::isMainVariable)
                        .filter(variable -> !optimizationContext.isUnrolledVariable(variable))
                        .distinct()
                        .forEachOrdered(v -> finalVariableStates.valueRead(v, null, false));
            }
        }

        variableStates.print("Final states after processing top level context");

        if (!labelStates.isEmpty()) {
            throw new MindcodeInternalError("Unresolved variable states associated with labels "
                    + labelStates.keySet().stream().map(LogicLabel::toMlog).collect(Collectors.joining(", ")));

        }
    }

    private VariableStates processContext(AstContext context, AstContext localContext, VariableStates variableStates,
            boolean modifyInstructions) {
        Objects.requireNonNull(variableStates);
        debug(() -> ">>> Entering context " + context.hierarchy());
        final VariableStates result;
        if (!context.matches(BASIC)) {
            result = processDefaultContext(context, localContext, variableStates, modifyInstructions);
        } else {
            result = switch (context.contextType()) {
                case LOOP   -> processLoopContext(context, variableStates, modifyInstructions);
                case IF     -> processIfContext(context, variableStates, modifyInstructions);
                case CASE   -> processCaseContext(context, variableStates, modifyInstructions);
                default     -> processDefaultContext(context, localContext, variableStates, modifyInstructions);
            };
        }
        debug(() -> "<<< Exiting  context " + context.hierarchy());
        return result;
    }

    private VariableStates processLoopContext(AstContext context, VariableStates variableStates, boolean modifyInstructions) {
        List<AstContext> children = context.children();
        int start = 0;
        boolean mergeInitial = false;

        if (children.stream().anyMatch(ctx -> ctx.matches(ITERATOR))) {
            if (!children.get(0).matches(ITERATOR)) {
                // We can't just ignore code we don't understand
                throw new MindcodeInternalError("Unexpected structure of for-each loop");
            }

            // First context is without merging to the prior
            variableStates = processContext(children.get(start++), context, variableStates, modifyInstructions);

            while (start < children.size() && children.get(start).matches(ITERATOR)) {
                VariableStates copy = variableStates.copy("loop iterator");
                variableStates = processContext(children.get(start++), context, variableStates, modifyInstructions);
                variableStates = variableStates.merge(copy, "iterator loop");
            }
        } else {
            if (!children.isEmpty() && children.get(0).matches(INIT)) {
                variableStates = processContext(children.get(0), context, variableStates, modifyInstructions);
                start++;
            }

            if (children.get(start).matches(CONDITION)) {
                // Evaluate the initial condition context fully for LoopOptimizer
                VariableStates copy = processDefaultContext(children.get(start), context,
                        variableStates.isolatedCopy(), false);
                optimizationContext.storeLoopVariables(context, copy);
            } else {
                // Store variable states right before enttering the body for the first time
                optimizationContext.storeLoopVariables(context, variableStates.isolatedCopy());
            }

            // If there are two CONDITION contexts, the first one gets executed only once
            if (children.stream().filter(ctx -> ctx.matches(CONDITION)).count() > 1) {
                if (!children.get(start).matches(CONDITION)) {
                    throw new MindcodeInternalError("Expected CONDITION context, found " + children.get(start));
                }
                variableStates = processContext(children.get(start++), context, variableStates, modifyInstructions);
            }

            // If the condition is before the body, we need to merge initial states after first pass through the body,
            // as we don't know whether the body will actually be executed.
            mergeInitial = children.stream()
                    .map(AstContext::subcontextType)
                    .filter(in(BODY, CONDITION))
                    .findFirst().get() == CONDITION;
        }

        // We'll visit the entire loop twice. Second pass will generate reaches to values generated in first pass.
        // Only perform the two-pass analysis when the outer loop is doing the second (and final) pass
        for (int i = 0; i < (modifyInstructions ? 2 : 1); i++) {
            VariableStates initial = variableStates.copy("loop initial state");
            final int iteration = i;
            debug(() -> "=== Processing loop - iteration " + iteration);

            for (int j = start; j < children.size(); j++) {
                // Do not propagate constants on first iteration...
                variableStates = processContext(children.get(j), context, variableStates, modifyInstructions && i > 0);
            }
            if (mergeInitial) {
                variableStates = variableStates.merge(initial, " inside loop");
            } else {
                mergeInitial = true;
            }
        }

        return variableStates;
    }

    private VariableStates processIfContext(AstContext context, VariableStates variableStates, boolean modifyInstructions) {
        Iterator<AstContext> children = context.children().iterator();
        AstContext condition = null;
        boolean bodyBefore = false;

        // Process all contexts up to the condition
        while (children.hasNext()) {
            AstContext child = children.next();
            variableStates = processContext(child, context, variableStates, modifyInstructions);
            if (child.matches(CONDITION)) {
                condition = child;
                break;
            }
            if (child.matches(BODY)) {
                bodyBefore = true;
            }
        }

        // We got a degenerated If context - the condition might have been removed by constant expression optimization.
        if (condition == null) {
            return variableStates;
        }

        // Data flow optimization might have fixed the value of the condition. It has two possible outcomes: either
        // the condition was false, in which case the jump in the condition is turned to unconditional jump, or the
        // condition was true, in which case the jump was entirely removed. We want to only process the body
        // corresponding to the actual value of the condition: first one if the condition was true, and second one
        // if the condition was false.
        boolean[] process = { true, true };
        boolean avoidMerge;

        // TODO Will need better condition processing after implementing short-circuit boolean eval
        LogicInstruction conditionIx = lastInstruction(condition);

        if (conditionIx instanceof JumpInstruction jump) {
            // Process first body only if jump is conditional
            process[0] = jump.isConditional();

            // If the jump is unconditional, the control flow is linear - do not merge branches
            avoidMerge = jump.isUnconditional();
        } else {
            // No jump: don't process second body
            process[1] = false;

            // The control flow is linear - do not merge branches
            avoidMerge = true;
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
                    if (process[bodies]) {
                        branchedStates.newBranch();
                        branchedStates.processContext(child, context, modifyInstructions);
                    }
                    wasBody = true;
                    bodies++;
                }
                case FLOW_CONTROL -> {
                    branchedStates.processContext(child, context, modifyInstructions);
                    wasBody = false;
                }
                default -> throw new MindcodeInternalError("Unexpected subcontext %s in IF context", child);
            }
        }

        if (avoidMerge) {
            return branchedStates.getCurrentStates();
        }

        // If there's only one body, start a new, empty branch. This will merge the initial state
        // into the final state, covering the case where the one body was skipped.
        if (bodies == 1) {
            branchedStates.newBranch();
        }

        return branchedStates.getFinalStates();
    }

    private VariableStates processCaseContext(AstContext context, VariableStates variableStates, boolean modifyInstructions) {
        List<AstContext> children = context.children();
        Iterator<AstContext> iterator = children.iterator();
        if (!children.isEmpty() && children.get(0).matches(INIT)) {
            AstContext child = iterator.next();
            variableStates = processContext(child, context, variableStates, modifyInstructions);
        }

        BranchedVariableStates branchedStates = new BranchedVariableStates(variableStates);
        boolean hasElse = false;

        while (iterator.hasNext()) {
            AstContext child = iterator.next();
            switch (child.subcontextType()) {
                case CONDITION -> {
                    branchedStates.appendToInitialContext(child, context, modifyInstructions);
                    branchedStates.newBranch();
                }
                case BODY, FLOW_CONTROL -> {
                    branchedStates.processContext(child, context, modifyInstructions);
                }
                case ELSE -> {
                    branchedStates.newBranch();
                    branchedStates.processContext(child, context, modifyInstructions);
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

    /**
     * Processes instructions inside a given context. Jumps outside local context are processed by associating
     * current variable state with target label. Local context might be the context being processed, or a parent
     * context when jumps within that context are handled specifically (such as by processIfContext).
     *
     * @param context context to process
     * @param localContext context which is considered local
     * @param variableStates variable states instance to update
     * @param modifyInstructions true if instructions may be modified in this run based on known variable states
     * @return the resulting variable states
     */
    private VariableStates processDefaultContext(AstContext context, AstContext localContext, VariableStates variableStates, boolean modifyInstructions) {
        Objects.requireNonNull(variableStates);
        LogicList instructions = contextInstructions(context);

        int index = 0;
        while (index < instructions.size()) {
            LogicInstruction instruction = instructions.get(index);
            // This needs to be done for each active context
            // Do not move into processInstruction
            if (!variableStates.isIsolated() && instruction instanceof JumpInstruction jump
                    && !getLabelInstruction(jump.getTarget()).belongsTo(localContext)) {
                VariableStates copy = variableStates.copy("nonlocal jump");
                copy.print("*** Storing variable states for label " + jump.getTarget().toMlog());
                labelStates.computeIfAbsent(jump.getTarget(), ix -> new ArrayList<>()).add(copy);
                if (jump.isUnconditional()) {
                    variableStates.setDead(false);
                }
            }

            if (instruction.getAstContext() == context) {
                variableStates = processInstruction(variableStates, instruction, modifyInstructions);
                variableStates.print("  after processing instruction");
                index++;
            } else {
                AstContext childContext = context.findMatchingChild(instruction.getAstContext());
                variableStates = processContext(childContext, context, variableStates, modifyInstructions);

                // Skip the rest of this context
                while (index < instructions.size() && instructions.get(index).belongsTo(childContext)) {
                    index++;
                }
            }
        }

        return variableStates;
    }

    private VariableStates resolveLabel(VariableStates variableStates, LogicLabel label) {
        List<VariableStates> states = labelStates.remove(label);
        if (states != null) {
            for (VariableStates state : states) {
                variableStates = variableStates.merge(state, "states for label " + label.toMlog());
            }
        }
        return variableStates;
    }

    private VariableStates processInstruction(VariableStates variableStates, LogicInstruction instruction, boolean modifyInstructions) {
        Objects.requireNonNull(variableStates);
        Objects.requireNonNull(instruction);

        if (DEBUG) {
            System.out.println("Processing instruction #" + instructionIndex(instruction) +
                    ": " + LogicInstructionPrinter.toString(instructionProcessor, instruction));
        }

        switch (instruction) {
            case PushInstruction ix:        return variableStates.pushVariable(ix.getVariable());
            case PopInstruction ix:         return variableStates.popVariable(ix.getVariable());
            case LabeledInstruction ix:     return resolveLabel(variableStates, ix.getLabel());
            case EndInstruction ix:         return variableStates.setDead(true);
            default:                        break;
        }

        // Process inputs first, to handle instructions reading and writing the same variable
        // Try to find possible replacements of input arguments to this instruction
        List<LogicVariable> inputs = instruction.inputArgumentsStream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .filter(this::canEliminate)
                .toList();

        // This needs to be done even when not modifying instructions, because it keeps track of read variables.
        Map<LogicVariable, LogicValue> valueReplacements = new HashMap<>();
        for (LogicVariable variable : inputs) {
            LogicValue constantValue = variableStates.valueRead(variable, instruction);
            if (constantValue != null) {
                valueReplacements.put(variable, constantValue);
            } else {
                LogicVariable equivalent = variableStates.findEquivalent(variable);
                if (equivalent != null && !equivalent.equals(variable)) {
                    valueReplacements.put(variable, equivalent);
                }
            }
        }

        if (modifyInstructions && !valueReplacements.isEmpty()) {
            replacements.put(instruction, valueReplacements);
            if (DEBUG) {
                System.out.println("    Detected the following possible value replacements for current instruction:");
                valueReplacements.forEach((k, v) -> System.out.println("       " + k.toMlog() + " --> " + v.toMlog()));
            }
        }

        // We're not evaluating PackColor, because the result can never be converted to mlog representation.
        LogicValue value = switch (instruction) {
            case SetInstruction set && set.getValue() instanceof LogicLiteral literal -> literal;
            case SetInstruction set && set.getValue() instanceof LogicBuiltIn builtIn && !builtIn.isVolatile() -> builtIn;
            case OpInstruction op && op.getOperation().isDeterministic() -> evaluateOpInstruction(op,
                    modifyInstructions || variableStates.isIsolated() ? valueReplacements : Map.of());
            default -> null;
        };

        instruction.outputArgumentsStream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .forEach(arg -> variableStates.valueSet(arg, instruction, value));

        switch (instruction.getOpcode()) {
            case CALL, CALLREC -> {
                String functionPrefix = instruction.getAstContext().functionPrefix();
                variableStates.updateAfterFunctionCall(functionPrefix, instruction);
                if (modifyInstructions && functionEnds.contains(functionPrefix)) {
                    functionEndStates.add(variableStates.copy("function end handling"));
                }
            }
        }

        if (modifyInstructions) {
            if (instruction instanceof OpInstruction op && op.hasSecondOperand()
                    || instruction instanceof SetInstruction
                    || instruction instanceof JumpInstruction) {
                putVariableStates(instruction, variableStates.isolatedCopy());
            }
        }

        return variableStates;
    }

    boolean canEliminate(LogicVariable variable) {
        // We need to protect return values of functions: when processing them, we have no information on
        // whether they're read. They're only useless if they aren't read at all, in which case they'll be removed
        // by DeadCodeEliminator.
        // STORED_RETVAL is removed, if possible, by ReturnValueOptimizer
        return switch (variable.getType()) {
            case COMPILER, FUNCTION_RETADDR, FUNCTION_RETVAL, GLOBAL_VARIABLE -> false;
            default -> true;
        };
    }

    private LogicLiteral evaluateOpInstruction(OpInstruction op, Map<LogicVariable, LogicValue> valueReplacements) {
        OpInstruction op1 = tryReplace(op, valueReplacements, op::getX, op::withX);
        OpInstruction op2 = op1.hasSecondOperand()
                ? tryReplace(op1, valueReplacements, op1::getY, op1::withY)
                : op1;

        return evaluateOpInstruction(op2);
    }

    private OpInstruction tryReplace(OpInstruction op, Map<LogicVariable, LogicValue> valueReplacements,
            Supplier<LogicValue> getArgument, Function<LogicValue, OpInstruction> replaceArgument) {
        LogicValue value = getArgument.get();
        if (value instanceof LogicVariable variable && valueReplacements.containsKey(variable)
                && valueReplacements.get(variable).isNumericLiteral()) {
            return replaceArgument.apply(valueReplacements.get(variable));
        } else {
            return op;
        }
    }

    private void debug(Supplier<String> text) {
        if (DEBUG) {
            System.out.println(text.get());
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
                merged = merged == null ? current : merged.merge(current, " old branch before starting new branch");
            }
            current = initial.copy("new conditional branch");
        }

        public void appendToInitialContext(AstContext context, AstContext localContext, boolean modifyInstructions) {
            initial = DataFlowOptimizer.this.processContext(context, localContext, initial, modifyInstructions);
        }

        public void processContext(AstContext context, AstContext localContext, boolean modifyInstructions) {
            current = DataFlowOptimizer.this.processContext(context, localContext,
                    current == null ? initial.copy("new conditional branch") : current, modifyInstructions);
        }

        public VariableStates getFinalStates() {
            newBranch();
            return merged == null ? initial : merged;
        }

        public VariableStates getCurrentStates() {
            return current == null ? initial : current;
        }
    }
}
