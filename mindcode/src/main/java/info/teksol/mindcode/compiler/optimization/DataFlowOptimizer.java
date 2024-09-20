package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.LogicInstructionPrinter;
import info.teksol.mindcode.compiler.MessageLevel;
import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.compiler.generator.CallGraph;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.compiler.optimization.DataFlowVariableStates.VariableStates;
import info.teksol.mindcode.compiler.optimization.DataFlowVariableStates.VariableValue;
import info.teksol.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mindcode.logic.*;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static info.teksol.mindcode.compiler.generator.AstSubcontextType.*;
import static info.teksol.util.CollectionUtils.in;
import static info.teksol.util.CollectionUtils.resultIn;

public class DataFlowOptimizer extends BaseOptimizer {
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
     * Only main variables are stored here. Global variable writes are always preserved, and local variables
     * generally do not keep their value between function calls.
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
     * Instructions referencing each variable. References are accumulated (not cleared during single pass).
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
    Map<CallGraph.LogicFunction, Set<LogicVariable>> functionReads;

    /** Maps function prefix to a list of variables directly or indirectly written by the function */
    Map<CallGraph.LogicFunction, Set<LogicVariable>> functionWrites;

    /** Contains function prefix of functions that may directly or indirectly call the end() instruction. */
    private Set<CallGraph.LogicFunction> functionEnds;

    /** List of variable states at each point of a call to a function that may invoke an end instruction. */
    private final List<VariableStates> functionEndStates = new ArrayList<>();

    /** An instance used for variable states processing */
    private final DataFlowVariableStates dataFlowVariableStates;

    public DataFlowOptimizer(OptimizationContext optimizationContext) {
        super(Optimization.DATA_FLOW_OPTIMIZATION, optimizationContext);
        dataFlowVariableStates = new DataFlowVariableStates(this);
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
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

        unreachables = optimizationContext.getUnreachableInstructions();

        analyzeFunctionVariables();

        getRootContext().children().forEach(this::processTopContext);

        // Keep defining instructions for orphaned, uninitialized variables.
        orphans.entrySet().stream()
                .filter(e -> uninitialized.contains(e.getKey()))
                .forEachOrdered(e -> keep.addAll(e.getValue()));

        instructionStream()
                .filter(ix -> replacements.containsKey(ix) || getVariableStates(ix) != null)
                .forEachOrdered(this::replaceInstruction);

        for (LogicInstruction instruction : defines) {
            // TODO create mechanism to identify instructions without side effects
            //      Will be used by the DeadCodeEliminator too!
            switch (instruction.getOpcode()) {
                case SET, OP, PACKCOLOR, READ -> {
                    if (!keep.contains(instruction) || useless.contains(instruction)) {
                        int index = instructionIndex(instruction);
                        if (index >= 0) {
                            removeInstruction(index);
                        }
                    }
                }
            }
        }

        return wasUpdated();
    }

    private void replaceInstruction(LogicInstruction instruction) {
        int index = instructionIndex(instruction);
        if (index < 0) return;

        VariableStates variableStates = Objects.requireNonNull(getVariableStates(instruction));
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
                replaceInstruction(index, replaceArgs(instruction, arguments), variableStates);
            }
        } else if (instruction instanceof OpInstruction op && op.hasSecondOperand()) {
            // Trying for extended evaluation
            OpInstruction newInstruction = extendedEvaluate(variableStates, op);
            if (newInstruction != null && canReplace(instruction, newInstruction)) {
                OpInstruction normalized = normalize(newInstruction);
                replaceInstruction(index, normalized, variableStates);
            }
        } else if (instruction instanceof SetInstruction set) {
            // Specific optimization to streamline self-modifying statements in recursive calls
            if (canEliminate(set, set.getResult()) && set.getValue().isTemporaryVariable()) {
                VariableValue val = variableStates.findVariableValue(set.getValue());
                if (val != null && val.isExpression() && val.getInstruction() instanceof OpInstruction op) {
                    if (op.inputArgumentsStream().anyMatch(set.getResult()::equals)) {
                        OpInstruction newInstruction = op.withContext(set.getAstContext()).withResult(set.getResult());
                        OpInstruction normalized = normalize(newInstruction);
                        replaceInstruction(index, normalized, variableStates);
                    }
                }
            }
        }
    }

    private void replaceInstruction(int index, LogicInstruction instruction, VariableStates variableStates) {
        replaceInstruction(index, instruction);
        instruction.inputArgumentsStream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .forEachOrdered(variableStates::protectVariable);
    }


    private int countReferences(LogicVariable variable) {
        return references.containsKey(variable) ? references.get(variable).size() : 0;
    }

    private boolean canReplace(LogicInstruction original, LogicInstruction replacement) {
        // Maximal number of references to input variables not present in replacement instruction
        int maxOriginal = original.inputArgumentsStream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .filter(Predicate.not(replacement.getArgs()::contains))
                .mapToInt(this::countReferences)
                .max().orElse(0);

        // Maximal number of references to input variables not present in original instruction
        int maxReplacement = replacement.inputArgumentsStream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .filter(Predicate.not(original.getArgs()::contains))
                .mapToInt(this::countReferences)
                .max().orElse(0);

        return maxReplacement >= maxOriginal || maxOriginal < 2;
    }

    /**
     * Creates lists of variables directly or indirectly read/written by each function, and a list of functions
     * directly or indirectly calling end()
     */
    private void analyzeFunctionVariables() {
        functionReads = new HashMap<>();
        functionWrites = new HashMap<>();
        functionEnds = new HashSet<>();

        getRootContext().children().stream()
                .filter(AstContext::isFunction)
                .forEachOrdered(this::analyzeFunctionVariables);

        while (propagateFunctionReadsAndWrites()) ;
    }

    /**
     * Analyzes reads, writes and end() calls by a single function.
     *
     * @param context context of the function to analyze
     */
    private void analyzeFunctionVariables(AstContext context) {
        Set<LogicVariable> reads = new HashSet<>();
        Set<LogicVariable> writes = new HashSet<>();

        try (LogicIterator it = createIteratorAtContext(context)) {
            while (it.hasNext()) {
                int index = it.nextIndex();
                LogicInstruction ix = it.next();
                if (!(ix instanceof PushOrPopInstruction) && !unreachables.get(index)) {
                    ix.inputArgumentsStream()
                            .filter(LogicVariable.class::isInstance)
                            .map(LogicVariable.class::cast)
                            .forEachOrdered(reads::add);

                    ix.outputArgumentsStream()
                            .filter(LogicVariable.class::isInstance)
                            .map(LogicVariable.class::cast)
                            .forEachOrdered(writes::add);

                    if (ix instanceof EndInstruction) {
                        functionEnds.add(context.function());
                    }
                }
            }
        }

        functionReads.put(context.function(), reads);
        functionWrites.put(context.function(), writes);
    }

    /**
     * Propagates variable reads/writes and end() calls to calling functions.
     *
     * @return true if the propagation lead to some modifications
     */
    private boolean propagateFunctionReadsAndWrites() {
        CallGraph callGraph = getCallGraph();
        boolean modified = false;
        for (CallGraph.LogicFunction function : callGraph.getFunctions()) {
            if (!function.isInline()) {
                Set<LogicVariable> reads = functionReads.computeIfAbsent(function, f -> new HashSet<>());
                Set<LogicVariable> writes = functionWrites.computeIfAbsent(function, f -> new HashSet<>());
                int size = reads.size() + writes.size() + functionEnds.size();
                function.getCalls().keySet().stream()
                        .filter(callGraph::containsFunction)            // Filter out built-in functions
                        .map(callGraph::getFunction)
                        .filter(f -> !f.isInline() && !f.isMain())
                        .forEachOrdered(callee -> {
                            reads.addAll(functionReads.get(callee));
                            writes.addAll(functionWrites.get(callee));
                            if (functionEnds.contains(callee)) {
                                functionEnds.add(function);
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

    private BitSet unreachables;

    /** Iterator pointing at the processed instruction */
    private LogicIterator iterator;

    /**
     * Processes a top context, either the main body context, or an out-of-line function context.
     *
     * @param context context to process
     */
    private void processTopContext(AstContext context) {
        VariableStates variableStates = dataFlowVariableStates.createVariableStates();
        if (context.isFunction()) {
            // All parameters of a function are initialized when the function is called.
            context.function().getLogicParameters().forEach(variableStates::markInitialized);
        }

        iterator = createIteratorAtContext(context);
        variableStates = processContext(context, context, variableStates, true);
        iterator.close();
        useless.addAll(variableStates.getUseless().values());

        if (!context.isFunction()) {
            // This is the main program context.
            // Variables that were not initialized might be expected to keep their value
            // on program restart (when reaching an end of instruction list/end instruction).
            // Marking them as read now will preserve the last assigned value.
            List.copyOf(uninitialized).forEach(variableStates::valueRead);
            functionEndStates.forEach(vs -> List.copyOf(uninitialized).forEach(vs::valueRead));

            if (!aggressive()) {
                // On basic optimization level, provide limited protection to main variables.
                // Specifically, latest values assigned to main variables are preserved.
                // Variables that were part of unrolled loops are NOT preserved, regardless of their other use.
                // Uninitialized variables aren't reported, because these variables aren't actually read, we only want
                // to keep the instructions that produced them in the code.
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
            // There was a jump to a label, but this label hasn't been processed.
            throw new MindcodeInternalError("Unresolved variable states associated with labels "
                    + labelStates.keySet().stream().map(LogicLabel::toMlog).collect(Collectors.joining(", ")));

        }
    }

    /**
     * Recursively processes contexts and their instructions. Jumps outside local context are specifically handled.
     * Context to be processed is either the same as the local context, or a direct child of the local context.
     *
     * @param localContext       context which is considered local (jumps outside local context handling)
     * @param context            context to be processed
     * @param variableStates     variable states at the beginning of the context
     * @param modifyInstructions true if instructions may be modified in this run based on known variable states
     * @return the resulting variable states
     */
    private VariableStates processContext(AstContext localContext, AstContext context, VariableStates variableStates,
            boolean modifyInstructions) {
        Objects.requireNonNull(variableStates);
        trace(() -> ">>> Entering context " + context.id + ": " + context.hierarchy());
        final VariableStates result;
        if (!context.matches(BASIC)) {
            result = processDefaultContext(localContext, context, variableStates, modifyInstructions);
        } else {
            result = switch (context.contextType()) {
                case LOOP   -> processLoopContext(context, variableStates, modifyInstructions);
                case IF     -> processIfContext(context, variableStates, modifyInstructions);
                case CASE   -> processCaseContext(context, variableStates, modifyInstructions);
                default     -> processDefaultContext(localContext, context, variableStates, modifyInstructions);
            };
        }
        trace(() -> "<<< Exiting  context " + context.id + ": " + context.hierarchy());
        return result;
    }

    /**
     * Specialized processing of a LOOP context.
     *
     * @param localContext       context to process (must be a LOOP context)
     * @param variableStates     variable states at the beginning of the context
     * @param modifyInstructions true if instructions may be modified in this run based on known variable states.
     *                           Set to true when doing the second pass through current loop
     * @return the resulting variable states
     */
    private VariableStates processLoopContext(AstContext localContext, VariableStates variableStates, boolean modifyInstructions) {
        // Note: this method processes contexts in unnatural order and uses iterator.setNextIndex to keep
        // the iterator position synchronized with what is expected.
        List<AstContext> children = new ArrayList<>(localContext.children());
        int currentContext = 0;
        boolean propagateUninitialized = false;

        if (children.stream().anyMatch(ctx -> ctx.matches(ITERATOR))) {
            if (children.get(currentContext).matches(INIT)) {
                variableStates = processContext(localContext, children.get(0), variableStates, modifyInstructions);
                currentContext++;
            }

            if (!children.get(currentContext).matches(ITERATOR)) {
                // We can't just ignore code we don't understand
                throw new MindcodeInternalError("Unexpected structure of for-each loop");
            }

            // Merge all final states of iterator subcontexts together: the loop body is processed with the final
            // value of every iterator subcontext.
            // First context is without merging to the previous one
            variableStates = processContext(localContext, children.get(currentContext++), variableStates, modifyInstructions);
            while (currentContext < children.size() && children.get(currentContext).matches(ITERATOR)) {
                VariableStates copy = variableStates.copy("loop iterator");
                variableStates = processContext(localContext, children.get(currentContext++), variableStates, modifyInstructions);
                variableStates = variableStates.merge(copy, true, "loop iterator");
            }
        } else {
            if (!children.isEmpty() && children.get(0).matches(INIT)) {
                variableStates = processContext(localContext, children.get(0), variableStates, modifyInstructions);
                currentContext++;
            }

            int savedPosition = iterator.nextIndex();

            // Acquiring variable states before entering the loop. Note we don't advance currentContext here
            if (children.get(currentContext).matches(CONDITION)) {
                // Evaluate the initial condition context fully for LoopOptimizer in isolation
                VariableStates copy = processDefaultContext(localContext, children.get(currentContext),
                        variableStates.isolatedCopy(), false);
                optimizationContext.storeLoopVariables(localContext, copy);
            } else {
                // No condition, the loop is entered directly: store variable states right before entering the body
                // for the first time
                optimizationContext.storeLoopVariables(localContext, variableStates.isolatedCopy());
            }

            // If there are two CONDITION contexts, the first one gets executed only once
            // We'll process it here, similarly to the INIT context
            if (children.stream().filter(ctx -> ctx.matches(CONDITION)).count() > 1) {
                if (!children.get(currentContext).matches(CONDITION)) {
                    // There are two CONDITION contexts, currentContext must point to the first one
                    throw new MindcodeInternalError("Expected CONDITION context, found " + children.get(currentContext));
                }
                iterator.setNextIndex(savedPosition);
                variableStates = processContext(localContext, children.get(currentContext++), variableStates, modifyInstructions);
            }

            // If the condition is before the body, and it is not known that the condition will be true upon the first
            // execution of the loop, we need to merge initial states after first pass through the body, as the body
            // might not be executed at all.
            boolean openingCondition = children.stream()
                    .map(AstContext::subcontextType)
                    .filter(in(BODY, CONDITION))
                    .findFirst().get() == CONDITION;

            if (openingCondition) {
                // Evaluate the condition
                AstContext conditionContext = children.stream()
                        .filter(resultIn(AstContext::subcontextType, CONDITION))
                        .findFirst().get();
                OptimizationContext.LogicList condition = contextInstructions(conditionContext);
                LogicInstruction last = condition.getLast();
                if (last instanceof NoOpInstruction) {
                    // NoOp means the jump was already eliminated --> the body WILL be executed
                    propagateUninitialized = false;
                } else if (last instanceof JumpInstruction jump) {
                    // If the jump evaluates to false, it means it doesn't skip over the loop body
                    LogicBoolean initialValue = optimizationContext.evaluateLoopConditionJump(jump, localContext);
                    propagateUninitialized = initialValue != LogicBoolean.FALSE;
                } else {
                    // We cannot guarantee the loop will be executed at least once
                    // Variable that are initialized in the loop body, but not before the loop, need to remain uninitialized
                    propagateUninitialized = true;
                }
            }
        }

        int loopStart = currentContext;
        int startIndex = firstInstructionIndex(children.get(loopStart));

        // The remaining CONDITION and BODY contexts are processed here.
        // We'll visit the entire loop twice. Second pass will generate reaches to values generated in first pass.
        // Only perform the two-pass analysis when the outer loop is also doing the second pass
        // This reduces the number of passes through the innermost loop from 2**n to n**2+1, where n is the nesting
        // level of the innermost loop
        for (int pass = 0; pass < (modifyInstructions ? 2 : 1); pass++) {
            iterator.setNextIndex(startIndex);

            VariableStates initial = variableStates.copy("loop initial state");
            final int iteration = pass;
            trace(() -> "=== Processing loop " + localContext.id + " - iteration " + iteration + ": position " + iterator.nextIndex());

            for (int j = loopStart; j < children.size(); j++) {
                // Do not modify instructions on the first iteration
                variableStates = processContext(localContext, children.get(j), variableStates, pass > 0);
            }

            variableStates = variableStates.merge(initial, propagateUninitialized, "inside loop");
            propagateUninitialized = true;
        }

        return variableStates;
    }

    /**
     * Specialized processing of an IF context.
     *
     * @param localContext       context to process (must be an IF context)
     * @param variableStates     variable states at the beginning of the context
     * @param modifyInstructions true if instructions may be modified in this run based on known variable states
     * @return the resulting variable states
     */
    private VariableStates processIfContext(AstContext localContext, VariableStates variableStates, boolean modifyInstructions) {
        Iterator<AstContext> children = localContext.children().iterator();
        AstContext condition = null;

        // Process all contexts up to the condition
        // This may include BODY contexts moved in front of the condition by If Expression Optimization.
        while (children.hasNext()) {
            AstContext child = children.next();
            variableStates = processContext(localContext, child, variableStates, modifyInstructions);
            if (child.matches(CONDITION)) {
                condition = child;
                break;
            }
        }

        // We got a degenerate If context - the condition might have been removed by constant expression optimization.
        if (condition == null) {
            return variableStates;
        }

        // Data flow optimization might have fixed the value of the condition. It has two possible outcomes: either
        // the condition was false, in which case the jump in the condition is turned to unconditional jump, or the
        // condition was true, in which case the jump was entirely removed. We want to only process the body
        // corresponding to the actual value of the condition: first one if the condition was true, and second one
        // if the condition was false.
        boolean[] process = {true, true};
        boolean avoidMerge;

        // TODO Will need better condition processing after implementing short-circuit boolean eval
        //      Maybe could just process the contexts based on unreachable code information like the case expression.
        LogicBoolean jumpResult = (lastInstruction(condition) instanceof JumpInstruction jump)
                ? evaluateJumpInstruction(jump)
                : LogicBoolean.FALSE;

        if (jumpResult != LogicBoolean.FALSE) {
            // Process first body only if jump evaluation is unknown to us (we know it isn't false)
            process[0] = jumpResult != LogicBoolean.TRUE;

            // If the jump is unconditional, the control flow is linear - do not merge branches
            avoidMerge = jumpResult == LogicBoolean.TRUE;
        } else {
            // The jump doesn't exist or is always false: don't process second body
            process[1] = false;

            // The control flow is linear - do not merge branches
            avoidMerge = true;
        }

        BranchedVariableStates branchedStates = new BranchedVariableStates(variableStates);
        boolean wasBody = false;
        int body = 0;

        while (children.hasNext()) {
            AstContext child = children.next();
            switch (child.subcontextType()) {
                case BODY -> {
                    if (wasBody) {
                        throw new MindcodeInternalError("Expected FLOW_CONTROL, found BODY subcontext in IF context %s", localContext);
                    }
                    if (process[body]) {
                        branchedStates.newBranch(false);
                        branchedStates.processContext(localContext, child, modifyInstructions);
                    } else {
                        skipContext(child);
                    }
                    wasBody = true;
                    body++;
                }
                case FLOW_CONTROL -> {
                    branchedStates.processContext(localContext, child, modifyInstructions);
                    wasBody = false;
                }
                default -> throw new MindcodeInternalError("Unexpected subcontext %s in IF context", child);
            }
        }

        if (avoidMerge) {
            return branchedStates.getCurrentStates();
        }

        // There was only one body, no else branch. Create a new branch to represent the missing else branch.
        if (body == 1) {
            branchedStates.newBranch(false);
        }

        return branchedStates.getFinalStates();
    }

    /**
     * Specialized processing of a CASE context.
     *
     * @param localContext       context to process (must be an IF context)
     * @param variableStates     variable states at the beginning of the context
     * @param modifyInstructions true if instructions may be modified in this run based on known variable states
     * @return the resulting variable states
     */
    private VariableStates processCaseContext(AstContext localContext, VariableStates variableStates, boolean modifyInstructions) {
        List<AstContext> children = localContext.children();
        Iterator<AstContext> iterator = children.iterator();
        if (!children.isEmpty() && children.get(0).matches(INIT)) {
            AstContext child = iterator.next();
            variableStates = processContext(localContext, child, variableStates, modifyInstructions);
        }

        BranchedVariableStates branchedStates = new BranchedVariableStates(variableStates);
        boolean hasElse = false;

        if (localContext.findSubcontext(CONDITION) == null) {
            // The context has been optimized by CaseSwitcher
            while (iterator.hasNext()) {
                AstContext child = iterator.next();
                switch (child.subcontextType()) {
                    case BODY -> {
                        branchedStates.processContext(localContext, child, modifyInstructions);
                        branchedStates.newBranch(false);
                    }
                    case ELSE -> {
                        branchedStates.processContext(localContext, child, modifyInstructions);
                        hasElse = true;
                    }
                    case FLOW_CONTROL -> {
                        branchedStates.processContext(localContext, child, modifyInstructions);
                    }
                    default -> throw new MindcodeInternalError("Unexpected subcontext %s in CASE context", child);
                }
            }
        } else {
            while (iterator.hasNext()) {
                AstContext child = iterator.next();
                switch (child.subcontextType()) {
                    case CONDITION -> {
                        branchedStates.mergeWithInitialState(localContext, child, modifyInstructions);
                    }
                    case BODY -> {
                        branchedStates.newBranch(true);
                        branchedStates.processContext(localContext, child, modifyInstructions);
                    }
                    case FLOW_CONTROL -> {
                        branchedStates.processContext(localContext, child, modifyInstructions);
                    }
                    case ELSE -> {
                        branchedStates.newBranch(true);
                        branchedStates.processContext(localContext, child, modifyInstructions);
                        hasElse = true;
                    }
                    default -> throw new MindcodeInternalError("Unexpected subcontext %s in CASE context", child);
                }
            }
        }

        // If there's no else branch, start a new, empty branch representing the missing else.
        if (!hasElse) {
            branchedStates.newBranch(false);
        }

        return branchedStates.getFinalStates();
    }

    /**
     * Processes instructions inside a given context. Jumps outside local context are processed by associating
     * current variable state with target label. Local context might be the context being processed, or a parent
     * context when jumps within that context are handled specifically (such as by processIfContext).
     *
     * @param localContext       context which is considered local
     * @param context            context to process
     * @param variableStates     variable states instance to update
     * @param modifyInstructions true if instructions may be modified in this run based on known variable states
     * @return the resulting variable states
     */
    private VariableStates processDefaultContext(AstContext localContext, AstContext context,
            VariableStates variableStates, boolean modifyInstructions) {
        Objects.requireNonNull(variableStates);

        while (iterator.hasNext()) {
            LogicInstruction instruction = iterator.peek(0);
            if (!instruction.belongsTo(context)) {
                break;
            }

            // This needs to be done for each active context
            // Do not move into processInstruction
            // Jumps inside a RETURN context are caused by the return instruction and do not leave the local context,
            // but they do break the control flow and therefore need to be handled here.
            if (!variableStates.isIsolated() && instruction instanceof JumpInstruction jump
                    && (context.matches(AstContextType.RETURN) ||
                    !getLabelInstruction(jump.getTarget()).belongsTo(localContext))) {
                VariableStates copy = variableStates.copy("nonlocal jump");
                copy.print("*** Storing variable states for label " + jump.getTarget().toMlog());
                labelStates.computeIfAbsent(jump.getTarget(), ix -> new ArrayList<>()).add(copy);
                if (jump.isUnconditional()) {
                    variableStates.setDead(false);
                }
            }

            if (instruction.getAstContext() == context) {
                boolean reachable = !unreachables.get(iterator.nextIndex());
                iterator.next();
                variableStates = processInstruction(variableStates, instruction, modifyInstructions, reachable);
                variableStates.print("  after processing instruction");
            } else {
                AstContext childContext = context.findDirectChild(instruction.getAstContext());
                variableStates = processContext(context, childContext, variableStates, modifyInstructions);
            }
        }

        return variableStates;
    }

    /**
     * Updates given variable states associated with a given label instruction to include all states deposited on the
     * label by all nonlocal jumps targeting that label.
     * <p>
     * Note: Data Flow Optimizer only expects nonlocal jumps to target labels that are found later in the code than
     * the jump. This is sufficient at the moment, as all nonlocal jumps are only generated by break, continue or
     * return statements and adhere to the requirement.
     *
     * @param variableStates    current variable states
     * @param label             label to process
     * @return given variable states merged with all variable states stored at label
     */
    private VariableStates resolveLabel(VariableStates variableStates, LogicLabel label) {
        List<VariableStates> states = labelStates.remove(label);
        if (states != null) {
            for (VariableStates state : states) {
                variableStates = variableStates.merge(state, true, "states for label " + label.toMlog());
            }
        }
        return variableStates;
    }

    private int counter = 0;

    /**
     * Processes a single instruction.
     *
     * @param variableStates        variable states before executing the instruction
     * @param instruction           instruction to process
     * @param modifyInstructions    true if instructions may be modified in this run based on known variable states
     * @param reachable             true if the instruction being processed is reachable
     * @return variable states after executing the instruction
     */
    private VariableStates processInstruction(VariableStates variableStates, LogicInstruction instruction,
            boolean modifyInstructions, boolean reachable) {
        Objects.requireNonNull(variableStates);
        Objects.requireNonNull(instruction);

        counter++;
        trace(() -> "*" + counter + " Processing instruction #" + instructionIndex(instruction) +
                ": " + LogicInstructionPrinter.toString(instructionProcessor, instruction));

        switch (instruction) {
            case NoOpInstruction ix:        return variableStates;
            case PushInstruction ix:        return variableStates.pushVariable(ix.getVariable());
            case PopInstruction ix:         return variableStates.popVariable(ix.getVariable());
            case LabeledInstruction ix:     return resolveLabel(variableStates, ix.getLabel());
            case EndInstruction ix:         return variableStates.setDead(true);
            default:                        break;
        }

        if (reachable) {
            variableStates.setReachable();
        } else if (TRACE) {
            System.out.println("UNREACHABLE");
        }

        // Process inputs first, to handle instructions reading and writing the same variable
        // This needs to be done even when not modifying instructions, because it keeps track of read variables.

        // Try to find possible replacements of input arguments to this instruction
        List<LogicVariable> inputs = instruction.inputArgumentsStream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .filter(variable -> canEliminate(instruction, variable))
                .toList();

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
            if (TRACE) {
                System.out.println("    Detected the following possible value replacements for current instruction:");
                valueReplacements.forEach((k, v) -> System.out.println("       " + k.toMlog() + " --> " + v.toMlog()));
            }
        }

        // Try to evaluate the instruction
        // We're not evaluating PackColor, because the result can never be converted to mlog representation.
        LogicValue value = switch (instruction) {
            case SetInstruction set && set.getValue() instanceof LogicLiteral literal -> literal;
            case SetInstruction set && set.getValue() instanceof LogicBuiltIn builtIn && !builtIn.isVolatile() -> builtIn;
            case OpInstruction op && op.getOperation().isDeterministic() -> evaluateOpInstruction(op,
                    modifyInstructions || variableStates.isIsolated() ? valueReplacements : Map.of());
            default -> null;
        };

        // The instruction sets all its output values. Instructions not processed above will set all their output
        // variables to an unknown state (represented by null - actual null value in mlog would be represented by
        // a LogicNull instance).
        // The actual inferred values aren't reused unless modifyInstruction is true (prevents inferred values from
        // the first pass through a loop from being used during the second pass).
        instruction.outputArgumentsStream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .forEach(arg -> variableStates.valueSet(arg, instruction, value, modifyInstructions));

        switch (instruction.getOpcode()) {
            case CALL, CALLREC -> {
                CallGraph.LogicFunction function = instruction.getAstContext().function();
                variableStates.updateAfterFunctionCall(function, instruction);
                if (modifyInstructions && functionEnds.contains(function)) {
                    functionEndStates.add(variableStates.copy("function end handling"));
                }
            }
        }

        if (modifyInstructions) {
            putVariableStates(instruction, variableStates.isolatedCopy());
        }

        return variableStates;
    }

    /**
     * Skip (move to an end of) the given context. Does nothing if the iterator is already past the context.
     *
     * @param context context to skip
     */
    private void skipContext(AstContext context) {
        while (iterator.hasNext() && iterator.peek(0).belongsTo(context)) {
            iterator.next();
        }
    }

    /**
     * Determines whether it is possible to eliminate an assignment to a variable by given instruction. Elimination
     * is generally allowed except cases requiring special protection.
     *
     * @param instruction instruction setting the variable
     * @param variable    variable being inspected
     * @return true if this assignment can be safely eliminated
     */
    boolean canEliminate(LogicInstruction instruction, LogicVariable variable) {
        return switch (variable.getType()) {
            case COMPILER, FUNCTION_RETADDR, PARAMETER, GLOBAL_VARIABLE -> false;
            case FUNCTION_RETVAL -> {
                // Function return values cannot be eliminated inside their functions - at this point we have no
                // information whether they're read somewhere. Outside their functions they're processed normally
                // (can be optimized freely).
                // If they aren't read at all in the entire program, they'll be removed by DeadCodeEliminator.
                AstContext functionCtx = instruction.getAstContext().findTopContextOfType(AstContextType.FUNCTION);
                yield functionCtx == null || !variable.getFunctionPrefix().equals(functionCtx.functionPrefix());
            }
            default -> true;
        };
    }

    /**
     * Tries to evaluate an OP instruction down to a constant value. Replaces variable arguments to the instruction
     * with their inferred values if possible.
     *
     * @param op                instruction to evaluate
     * @param valueReplacements value replacements to use
     * @return a LogicLiteral representing the determined resulting value of the instruction,
     * or null if the instruction cannot be evaluated
     */
    private LogicLiteral evaluateOpInstruction(OpInstruction op, Map<LogicVariable, LogicValue> valueReplacements) {
        OpInstruction op1 = tryReplace(op, valueReplacements, op::getX, op::withX);
        OpInstruction op2 = op1.hasSecondOperand()
                ? tryReplace(op1, valueReplacements, op1::getY, op1::withY)
                : op1;

        return evaluateOpInstruction(op2);
    }

    /**
     * Replaces a variable argument to the instruction with its inferred values if possible.
     *
     * @param op                instruction to be modified
     * @param valueReplacements value replacements to use
     * @param getArgument       lambda expression to extract the desired argument from the instruction
     * @param replaceArgument   lambda to replace the desired argument with a new value
     * @return a new, updated instruction, or the original one if no replacement is possible
     */
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

    /**
     * Helper class to manage variable states of branching statements (if, case).
     */
    private class BranchedVariableStates {
        /** The initial state of the statement, before branching. Set by constructor. */
        private final VariableStates initial;

        /** State of the currently processed branch. Null if no branch was processed. */
        private VariableStates current;

        /** Final states of all branches merged together. */
        private VariableStates merged;

        public BranchedVariableStates(VariableStates initial) {
            this.initial = initial;
        }

        /**
         * Called when a body of a new branch is encountered. Closes the previous branch (if any) by merging it into
         * the final states, and then creates variable states for the new branch by copying the initial state.
         */
        public void newBranch(boolean startUnreachable) {
            if (current != null) {
                merged = merged == null ? current : merged.merge(current, true, " old branch before starting new branch");
            }
            current = initial.copy("new conditional branch");
            if (startUnreachable) {
                current.setUnreachable();
            }
        }

        /**
         * TODO review javadoc
         * Processes the given context and appends the results into the initial state. Used to process conditions of
         * individual branches of case expressions, which are all being processed until a match is found; the executed
         * branch therefore contains the results of all conditions evaluated before it.
         *
         * @param localContext       context which is considered local
         * @param context            context to be processed
         * @param modifyInstructions true if instructions may be modified in this run based on known variable states
         */
        public void mergeWithInitialState(AstContext localContext, AstContext context, boolean modifyInstructions) {
//            initial = DataFlowOptimizer.this.processContext(localContext, context, initial, modifyInstructions);
            VariableStates processed = DataFlowOptimizer.this.processContext(localContext, context, initial.copy("new when condition"), modifyInstructions);
            initial.merge(processed, true, "processed condition to initial state");
        }

        /**
         * Processes the given context as part of the current branch.
         *
         * @param localContext       context which is considered local
         * @param context            context to be processed
         * @param modifyInstructions true if instructions may be modified in this run based on known variable states
         */
        public void processContext(AstContext localContext, AstContext context, boolean modifyInstructions) {
            current = DataFlowOptimizer.this.processContext(localContext, context,
                    current == null ? initial.copy("new conditional branch") : current, modifyInstructions);
        }

        /**
         * Returns variable states obtained by merging final states of all processed branches together.
         *
         * @return final variable states of the branched expression
         */
        public VariableStates getFinalStates() {
            trace(() -> "Getting final states");
            newBranch(false);        // Force merging the previous branch
            return merged == null ? initial : merged;
        }

        /**
         * Returns variable states corresponding to the currently active branch.
         *
         * @return final variable states of the branch that was just processed
         */
        public VariableStates getCurrentStates() {
            return current == null ? initial : current;
        }
    }
}
