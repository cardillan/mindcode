package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionParameter;
import info.teksol.mc.mindcode.compiler.optimization.DataFlowVariableStates.Definition;
import info.teksol.mc.mindcode.compiler.optimization.DataFlowVariableStates.VariableStates;
import info.teksol.mc.mindcode.compiler.optimization.DataFlowVariableStates.VariableValue;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mc.mindcode.compiler.postprocess.LogicInstructionPrinter;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.mindcode.logic.opcodes.TypedArgument;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType.*;
import static info.teksol.mc.mindcode.compiler.optimization.OptimizationCoordinator.TRACE;
import static info.teksol.mc.util.CollectionUtils.in;
import static info.teksol.mc.util.CollectionUtils.resultIn;

@NullMarked
class DataFlowOptimizer extends AbstractConditionalOptimizer {
    /// Stores possible replacement values to each input argument of a replaceable instruction.
    private final Map<LogicInstruction, Map<LogicVariable, LogicValue>> replacements = new IdentityHashMap<>();

    /// Set of instructions whose sole purpose is to set value to some variable. If the variable isn't subsequently
    /// read, the instruction is useless and can be removed.
    final Set<LogicInstruction> defines = Collections.newSetFromMap(new IdentityHashMap<>());

    /// Set of variable producing instructions that were actually used in the program and need to be kept.
    final Set<LogicInstruction> keep = Collections.newSetFromMap(new IdentityHashMap<>());

    /// When an END instruction is encountered, the optimizer accumulates current variable definitions here. Definitions
    /// from this structure are kept for uninitialized variables so that any values written before the END instruction
    /// executions are preserved.
    ///
    /// Only the main variables are stored here. Global variable writes are always preserved, and local variables
    /// generally do not keep their value between function calls.
    final Map<LogicVariable, List<LogicInstruction>> orphans = new HashMap<>();

    /// Exceptions to the keep set
    private final Set<LogicInstruction> useless = Collections.newSetFromMap(new IdentityHashMap<>());

    /// Set of potentially uninitialized variables. In at least one branch the variable can be read without
    /// being assigned a value first.
    private final Set<LogicVariable> uninitialized = new HashSet<>();

    /// Instructions referencing each variable. References are accumulated (not cleared during single pass).
    final Map<LogicVariable, List<LogicInstruction>> references = new HashMap<>();

    /// Holds all variable definitions and their uses.
    final Set<Definition> definitions = Collections.newSetFromMap(new IdentityHashMap<>());

    /// When a jump outside its context is encountered, current variable state is copied and assigned to the target
    /// label. Upon encountering the target label all stored states are merged and flushed.
    /// Unresolved label indicates a problem in the data flow analysis -- this would happen if the jump targeted an
    /// earlier label. LogicInstructionGenerator should never generate such code.
    private final Map<LogicLabel, List<VariableStates>> labelStates = new HashMap<>();

    /// List of variable states at each point of a call to a function that may invoke an end instruction.
    private final List<VariableStates> functionEndStates = new ArrayList<>();

    /// An instance used for variable states processing
    private final DataFlowVariableStates dataFlowVariableStates;

    ///  Instruction counter; in encountered order
    private int counter = 0;

    public DataFlowOptimizer(OptimizationContext optimizationContext) {
        super(Optimization.DATA_FLOW_OPTIMIZATION, optimizationContext);
        dataFlowVariableStates = new DataFlowVariableStates(this);
    }

    int getCounter() {
        return counter;
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
        definitions.clear();
        labelStates.clear();
        functionEndStates.clear();

        clearVariableStates();
        optimizationContext.getProgram().forEach(ix -> {
            if (ix instanceof ArrayAccessInstruction aix) aix.resetCompactAccess();
        });

        if (currentPass <= 1) {
            trace("!!! Skipping backpropagation optimization on first pass.\n\n");
        }

        unreachables = optimizationContext.getUnreachableInstructions();

        getRootContext().children().forEach(this::processTopContext);

        if (advancedGlobal() && currentPass > 1) {
            boolean updated = false;
            for (Definition definition : definitions) {
                LogicVariable variable = definition.variable;

                // If some definitions are a side effect, do not perform the optimization
                if (definition.instructions.stream().anyMatch(ix -> ix.outputArgumentsStream().noneMatch(variable::equals))) {
                    continue;
                }

                if (definition.getReference() instanceof SetInstruction set && set.getValue().equals(variable)) {
                    int setIndex = instructionIndex(set);
                    if (setIndex < 0) continue;

                    // Find instruction indexes and bail out if some of them are missing
                    List<Integer> indexes = definition.instructions.stream().map(this::instructionIndex).filter(i -> i >= 0).toList();
                    if (indexes.size() != definition.instructions.size()) continue;

                    // All definitions are replaceable and none of them depends on the variable
                    boolean canReplace = definition.instructions.stream().allMatch(ix ->
                            canEliminate(ix, variable) && ix.inputArgumentsStream().noneMatch(v -> v.equals(variable)));

                    if (!canReplace) continue;

                    for (int index : indexes) {
                        LogicInstruction instruction = instructionAt(index);
                        replaceInstruction(index, replaceAllArgs(instruction, variable, set.getResult()));
                    }
                    invalidateInstruction(setIndex);
                    updated = true;
                }
            }

            if (updated) {
                return true;
            }
        }

        // Keep defining instructions for orphaned, uninitialized variables.
        orphans.entrySet().stream()
                .filter(e -> uninitialized.contains(e.getKey()))
                .forEachOrdered(e -> {
                    if (TRACE) {
                        e.getValue().forEach(ix -> trace("--> Keeping instruction: " + ix.toMlog() + " (orphaned variable)"));
                    }
                    keep.addAll(e.getValue());
                });

        instructionStream()
                .filter(ix -> !replacements.getOrDefault(ix, Map.of()).isEmpty() || getVariableStates(ix) != null)
                .forEachOrdered(this::replaceInstruction);

        for (LogicInstruction instruction : defines) {
            if (instruction.isSafe() && (!keep.contains(instruction) || useless.contains(instruction)) &&
                    instruction.getAllWrites().noneMatch(var -> isVolatile(instruction, var))) {
                int index = instructionIndex(instruction);
                if (index >= 0) {
                    invalidateInstruction(index);
                }
            }
        }

        return wasUpdated();
    }

    private void replaceInstruction(LogicInstruction instruction) {
        int index = instructionIndex(instruction);
        if (index < 0) return;

        VariableStates variableStates = Objects.requireNonNull(getVariableStates(instruction));
        Map<LogicVariable, LogicValue> valueReplacements = replacements.getOrDefault(instruction, Map.of());
        if (!valueReplacements.isEmpty()) {
            boolean updated = false;
            List<LogicArgument> arguments = new ArrayList<>(instruction.getArgs());
            for (int i = 0; i < arguments.size(); i++) {
                LogicValue replacement;
                if (instruction.getArgumentType(i).isInput()
                        && instruction.getArgumentType(i) != InstructionParameterType.LABEL
                        && arguments.get(i) instanceof LogicVariable variable
                        && (replacement = valueReplacements.get(variable)) != null) {
                    arguments.set(i, replacement);
                    updated = true;
                }
            }

            SideEffects sideEffects = instruction.getSideEffects().replaceVariables(valueReplacements);

            if (updated || sideEffects != instruction.getSideEffects()) {
                replaceInstruction(index, replaceArgs(instruction, arguments).setSideEffects(sideEffects),
                        variableStates);
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
                    if (op.usesAsInput(set.getResult())) {
                        OpInstruction newInstruction = op.withContext(set.getAstContext()).withResult(set.getResult());
                        OpInstruction normalized = normalize(newInstruction);
                        replaceInstruction(index, normalized, variableStates);
                    }
                }
            }
        }
    }

    private void replaceInstruction(int index, LogicInstruction replacement, VariableStates variableStates) {
        replaceInstruction(index, replacement);
        replacement.inputArgumentsStream()
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

    public void generateFinalMessages() {
        super.generateFinalMessages();

        String uninitializedList = uninitialized.stream()
                .filter(v -> v.getType() != ArgumentType.BLOCK)
                .map(LogicVariable::getFullName)
                .distinct()
                .sorted()
                .collect(Collectors.joining(", "));

        uninitialized.stream()
                .filter(v -> v.getType() != ArgumentType.BLOCK)
                .forEach(optimizationContext::addUninitializedVariable);
    }

    private @Nullable BitSet unreachables;

    /// Iterator pointing at the processed instruction
    private @Nullable LogicIterator iterator;

    private void seek(AstContext context) {
        assert iterator != null;
        iterator.setNextIndex(firstInstructionIndex(context));
    }

    /// Processes a top context, either the main body context, or an out-of-line function context.
    ///
    /// @param context context to process
    private void processTopContext(AstContext context) {
        // Jump tables. Nothing to optimize here; on the contrary, they must not be disturbed.
        if (context.matches(AstContextType.JUMPS)) {
            return;
        }

        if (firstInstructionIndex(context) < 0) {
            // Empty top context. Can happen when using constants from imported libraries.
            return;
        }

        VariableStates topStates = dataFlowVariableStates.createVariableStates();
        trace(() -> "*** Processing top context: created VariableStates instance " + topStates.getId());
        VariableStates variableStates = topStates;

        variableStates.markInitialized(LogicVariable.remoteWaitAddr());
        if (context.isFunction()) {
            MindcodeFunction function = context.function();
            assert function != null;
            // All global variables and all input parameters of a function are initialized when the function is called.
            optimizationContext.getFunctionReads(function).stream()
                    .filter(LogicVariable::isGlobalVariable)
                    .forEach(variableStates::markInitialized);
            function.getParameters().stream().filter(FunctionParameter::isInput).map(LogicVariable.class::cast)
                    .forEach(variableStates::markInitialized);
            variableStates.markInitialized(function.getFnRetAddr());
        }

        iterator = createIteratorAtContext(context);
        variableStates = processContext(context, context, variableStates, true, defaultDfProcessor);
        iterator.close();
        useless.addAll(variableStates.getUseless().values());

        final VariableStates finalVariableStates = variableStates;

        if (!context.isFunction()) {
            // This is the main program context.

            // If this is a remote module, all remote functions might get called
            if (optimizationContext.isRemoteLibrary()) {
                getCallGraph().getFunctions().stream()
                        .filter(f -> f.isEntryPoint() && !f.isMain())
                        .forEach(function -> finalVariableStates.updateAfterFunctionCall(function, null));
            }

            // Variables that were not initialized might be expected to keep their value
            // on program restart (when reaching an end of instruction list/end instruction).
            // Marking them as read now will preserve the last assigned value.
            List.copyOf(uninitialized).forEach(variableStates::valueRead);
            functionEndStates.forEach(vs -> List.copyOf(uninitialized).forEach(vs::valueRead));
        }

        if (context.isFunction()) {
            // Changes to global variables are needed outside the function
            assert context.function() != null;
            optimizationContext.getFunctionWrites(context.function()).stream()
                    .filter(LogicVariable::isGlobalVariable)
                    .forEach(v -> finalVariableStates.valueRead(v, null, false, true));
        }

        finalVariableStates.print("Final states after processing top level context");

        if (!labelStates.isEmpty()) {
            // There was a jump to a label, but this label hasn't been processed.
            throw new MindcodeInternalError("Unresolved variable states associated with labels "
                    + labelStates.keySet().stream().map(LogicLabel::toMlog).collect(Collectors.joining(", ")));

        }
    }

    /// Recursively processes contexts and their instructions. Jumps outside the local context are specifically handled.
    /// Context to be processed is either the same as the local context or a direct child of the local context.
    ///
    /// @param localContext       context which is considered local (jumps outside local context handling)
    /// @param context            context to be processed; typically either localContext or a child of localContext
    /// @param variableStates     variable states at the beginning of the context
    /// @param modifyInstructions true if instructions may be modified in this run based on known variable states
    /// @return the resulting variable states
    private VariableStates processContext(AstContext localContext, AstContext context, VariableStates variableStates,
            boolean modifyInstructions, DataFlowInstructionProcessor dfProcessor) {
        Objects.requireNonNull(variableStates);
        trace(() -> ">>> Entering context cx#" + context.id +
                    " at ix#" + optimizationContext.firstInstructionIndex(context) +
                    ": " + context.hierarchy());
        indentInc();
        final VariableStates result;
        if (!context.matches(BASIC)) {
            result = processDefaultContext(localContext, context, variableStates, modifyInstructions, dfProcessor);
        } else {
            result = switch (context.contextType()) {
                case LOOP           -> processLoopContext(context, variableStates, modifyInstructions, dfProcessor);
                case EACH           -> processIterationLoopContext(context, variableStates, modifyInstructions, dfProcessor);
                case IF             -> processIfContext(context, variableStates, modifyInstructions, dfProcessor);
                case CASE           -> processCaseContext(context, variableStates, modifyInstructions, dfProcessor);
                case JUMPS          -> processJumpsContext(context, variableStates, modifyInstructions, dfProcessor);
                case SCBE_COND,
                     SCBE_OPER      -> processShortCircuitingContext(context, variableStates, modifyInstructions, dfProcessor);
                default             -> processDefaultContext(localContext, context, variableStates, modifyInstructions, dfProcessor);
            };
        }
        indentDec();
        trace(() -> "<<< Exiting  context cx#" + context.id +
                    " at ix#" + optimizationContext.lastInstructionIndex(context) +
                    ": " + context.hierarchy());
        return result;
    }

    /// Specialized processing of a LOOP context.
    ///
    /// @param localContext       context to process (must be a LOOP context)
    /// @param variableStates     variable states at the beginning of the context
    /// @param modifyInstructions true if instructions may be modified in this run based on known variable states.
    ///                           Set to true when doing the second pass through current loop
    /// @return the resulting variable states
    private VariableStates processLoopContext(AstContext localContext, VariableStates variableStates, boolean modifyInstructions,
            DataFlowInstructionProcessor dfProcessor) {
        assert iterator != null;

        // Note: this method processes contexts in unnatural order and uses iterator.setNextIndex to keep
        // the iterator position synchronized with what is expected.
        List<AstContext> children = new ArrayList<>(localContext.children());
        if (children.isEmpty() || children.stream().noneMatch(c -> c.matches(CONDITION, BODY)))
            return variableStates;

        int currentContext = 0;
        boolean propagateUninitialized = false;

        if (children.stream().anyMatch(c -> c.matches(ITR_LEADING, ITR_TRAILING))) {
            throw new MindcodeInternalError("Unexpected structure of non-list iteration loop");
        }

        // If the HOIST context is found here, it means the front condition has been eliminated
        // and the HOIST context gets executed always.
        while (children.get(currentContext).matches(INIT, HOIST)) {
            variableStates = processContext(localContext, children.getFirst(), variableStates, modifyInstructions, dfProcessor);
            currentContext++;
        }

        int savedPosition = iterator.nextIndex();

        // Acquiring variable states before entering the loop. Note we don't advance currentContext here
        if (children.get(currentContext).matches(CONDITION)) {
            // Evaluate the initial condition context fully for LoopRotator in isolation
            VariableStates copy = processDefaultContext(localContext, children.get(currentContext),
                    variableStates.isolatedCopy(), false,
                    (lc, c, vs, ix) -> {
                        VariableStates vs1 = dfProcessor.processInstruction(lc, c, vs, ix);
                        if (ix instanceof JumpInstruction j && j.isConditional()) {
                            optimizationContext.storeFirstPassStates(j, vs1.isolatedCopy());
                        }
                        return vs1;
                    });
            optimizationContext.storeLoopVariables(localContext, copy);
        } else {
            // No condition, the loop is entered directly: store variable states right before entering the body
            // for the first time
            optimizationContext.storeLoopVariables(localContext, variableStates.isolatedCopy());
        }

        List<AstContext> conditions = children.stream().filter(ctx -> ctx.matches(CONDITION)).toList();
        boolean partialRotation = conditions.size() == 2 && optimizationContext.isShortCircuitCondition(conditions.getFirst());

        // If there are two CONDITION contexts and a full loop rotation was performed, the first one gets executed only once.
        // We'll process it here, similarly to the INIT context.
        if (conditions.size() > 1) {
            if (conditions.size() != 2 || children.get(currentContext) != conditions.getFirst()) {
                // There are two CONDITION contexts, currentContext must point to the first one
                throw new MindcodeInternalError("Invalid CONDITION context structure.");
            }

            iterator.setNextIndex(savedPosition);
            variableStates = processContext(localContext, children.get(currentContext++), variableStates,
                    modifyInstructions, dfProcessor);
        }

        // If the condition is before the body, and it is not known that the condition will be true upon the first
        // execution of the loop: we need to merge initial states after first pass through the body, as the body
        // might not be executed at all.
        boolean openingCondition = children.stream()
                .map(AstContext::subcontextType)
                .filter(in(BODY, CONDITION))
                .findFirst().orElseThrow() == CONDITION;

        if (openingCondition) {
            // Evaluate the condition
            AstContext conditionContext = children.stream()
                    .filter(resultIn(AstContext::subcontextType, CONDITION))
                    .findFirst().orElseThrow();

            // If we don't know for sure the loop body will be executed, propagate uninitialized values from
            // before the loop to after the loop.
            LogicBoolean result = optimizationContext.evaluateLoopEntryCondition(conditionContext);
            if (result != LogicBoolean.TRUE) propagateUninitialized = true;
        }

        VariableStates preHoistStates = null;
        if (children.get(currentContext).matches(HOIST)) {
            preHoistStates = variableStates.copy("loop pre-hoist state");
            variableStates = processContext(localContext, children.get(currentContext), variableStates, modifyInstructions, dfProcessor);
            currentContext++;
        }

        int loopStart = currentContext;
        int startIndex = firstInstructionIndex(children.get(loopStart));


        // The remaining CONDITION and BODY contexts are processed here.
        // We'll visit the entire loop twice. The second pass will generate reaches to values generated in the first pass.
        // Only perform the two-pass analysis when the outer loop is also doing the second pass
        // This reduces the number of passes through the innermost loop from 2**n to n**2+1, where n is the nesting
        // level of the innermost loop
        for (int pass = 0; pass < (modifyInstructions ? 2 : 1); pass++) {
            iterator.setNextIndex(startIndex);

            final int loopPass = pass;
            trace(() -> "=== Processing loop cx#" + localContext.id + " - loop pass " + loopPass + " (ix#" + iterator.nextIndex() + ")");
            VariableStates initial = variableStates.copy("loop initial state");

            for (int j = loopStart; j < children.size(); j++) {
                if (partialRotation && children.get(j) == conditions.getLast()) {
                    variableStates = new ShortCircuitedVariableStates().processContext(
                            conditions.getLast().child(0),
                            conditions.getFirst().child(0),
                            variableStates, modifyInstructions, dfProcessor);

                    // Restore the next point if needed
                    if (j + 1 < children.size()) {
                        seek(children.get(j + 1));
                    }
                } else if (!children.get(j).matches(ITR_TRAILING)) {
                    // Do not modify instructions on the first iteration
                    variableStates = processContext(localContext, children.get(j), variableStates,
                            pass > 0, dfProcessor);
                }
            }

            variableStates = variableStates.merge(initial, propagateUninitialized, "inside loop", true);
            propagateUninitialized = true;
        }

        if (preHoistStates != null) {
            variableStates = variableStates.merge(preHoistStates, true, "loop pre-hoist state");
        }

        return variableStates;
    }

    /// Specialized processing of a LOOP context.
    ///
    /// @param localContext       context to process (must be an `EACH` context)
    /// @param variableStates     variable states at the beginning of the context
    /// @param modifyInstructions true if instructions may be modified in this run based on known variable states.
    ///                           Set to true when doing the second pass through current loop
    /// @return the resulting variable states
    private VariableStates processIterationLoopContext(AstContext localContext, VariableStates variableStates,
            boolean modifyInstructions, DataFlowInstructionProcessor dfProcessor) {
        assert iterator != null;

        List<AstContext> children = new ArrayList<>(localContext.children());
        int currentContext = 0;

        List<AstContext> leadingContexts = localContext.findSubcontexts(ITR_LEADING);
        List<AstContext> trailingContexts = localContext.findSubcontexts(ITR_TRAILING);
        List<AstContext> bodyContexts = localContext.children().stream().filter(c -> c.matches(BODY, FLOW_CONTROL)).toList();

        while (currentContext < children.size() && children.get(currentContext).matches(INIT, HOIST)) {
            variableStates = processContext(localContext, children.getFirst(), variableStates, modifyInstructions, dfProcessor);
            currentContext++;
        }

        if (!children.get(currentContext).matches(ITR_LEADING)) {
            // We can't just ignore code we don't understand
            throw new MindcodeInternalError("Unexpected structure of list iteration loop");
        }

        VariableStates initial = null;

        // We'll visit the entire loop twice. Second pass will generate reaches to values generated in first pass.
        for (int pass = 0; pass < (modifyInstructions ? 2 : 1); pass++) {
            boolean modifyInstructionsInPass = pass > 0;

            final int iteration = pass;
            trace(() -> "=== Processing for-each loop cx#" + localContext.id + " - iteration " + iteration + " (ix#" + iterator.nextIndex() + ")");

            // Merge all final states of leading list iterator subcontexts together: the loop body is processed
            // with the final value of every iterator subcontext.
            // First context is without merging to the previous one
            iterator.setNextIndex(firstInstructionIndex(leadingContexts.getFirst()));
            variableStates = processContext(localContext, leadingContexts.getFirst(), variableStates, modifyInstructionsInPass, dfProcessor);

            for (int index = 1; index < leadingContexts.size(); ) {
                AstContext context = leadingContexts.get(index++);
                iterator.setNextIndex(firstInstructionIndex(context));
                VariableStates copy = variableStates.copy("leading loop iterator");
                variableStates = processContext(localContext, context, variableStates, modifyInstructionsInPass, dfProcessor);
                variableStates = variableStates.merge(copy, false, "leading loop iterator", true);
            }

            for (AstContext context : bodyContexts) {
                iterator.setNextIndex(firstInstructionIndex(context));
                variableStates = processContext(localContext, context, variableStates, modifyInstructionsInPass, dfProcessor);
            }

            if (!trailingContexts.isEmpty()) {
                iterator.setNextIndex(firstInstructionIndex(trailingContexts.getFirst()));
                variableStates = processContext(localContext, trailingContexts.getFirst(), variableStates, modifyInstructionsInPass, dfProcessor);

                for (int index = 1; index < trailingContexts.size(); ) {
                    AstContext context = trailingContexts.get(index++);
                    iterator.setNextIndex(firstInstructionIndex(context));
                    VariableStates copy = variableStates.copy("trailing loop iterator");
                    variableStates = processContext(localContext, context, variableStates, modifyInstructionsInPass, dfProcessor);
                    variableStates = variableStates.merge(copy, false, "trailing loop iterator", true);
                }
            }

            if (initial == null) {
                initial = variableStates.copy("for each loop first pass state");
            } else {
                variableStates = variableStates.merge(initial, true, "inside loop", true);
            }
        }

        return variableStates;
    }

    /// Specialized processing of an IF context.
    ///
    /// @param localContext       context to process (must be an IF context)
    /// @param variableStates     variable states at the beginning of the context
    /// @param modifyInstructions true if instructions may be modified in this run based on known variable states
    /// @return the resulting variable states
    private VariableStates processIfContext(AstContext localContext, VariableStates variableStates, boolean modifyInstructions,
            DataFlowInstructionProcessor dfProcessor) {
        Iterator<AstContext> children = localContext.children().iterator();
        AstContext condition = null;

        // Process all contexts up to the condition
        // This may include BODY contexts moved in front of the condition by If Expression Optimization.
        while (children.hasNext()) {
            AstContext child = children.next();
            variableStates = processContext(localContext, child, variableStates, modifyInstructions, dfProcessor);
            if (child.matches(CONDITION)) {
                condition = child;
                break;
            }
        }

        // We got a degenerate If context - the condition might have been removed by constant expression optimization.
        // All child contexts have been processed at this point
        if (condition == null) {
            return variableStates;
        }

        // Data flow optimization might have fixed the value of the condition. It has two possible outcomes: either
        // the condition was false, in which case the jump in the condition is turned to unconditional jump, or the
        // condition was true, in which case the jump was entirely removed. We want to reachably process the body
        // corresponding to the actual value of the condition: the first one if the condition was true, and the
        // second one if the condition was false.
        boolean[] reachable = new boolean[2];

        LogicBoolean jumpResult = optimizationContext.evaluateJumpCondition(condition);
        reachable[0] = jumpResult != LogicBoolean.FALSE;
        reachable[1] = jumpResult != LogicBoolean.TRUE;

        BranchedVariableStates branchedStates = new BranchedVariableStates(localContext, variableStates);
        boolean wasBody = false;
        int bodies = 0;

        while (children.hasNext()) {
            AstContext child = children.next();
            switch (child.subcontextType()) {
                case BODY -> {
                    if (wasBody) {
                        // Two body contexts next to each other aren't allowed
                        throw new MindcodeInternalError("Expected FLOW_CONTROL, found BODY subcontext in IF context %s", localContext);
                    }
                    branchedStates.newBranch(localContext, "[entering if body]", reachable[bodies]);
                    branchedStates.processContext(localContext, child, modifyInstructions, dfProcessor);
                    wasBody = true;
                    bodies++;
                }
                case FLOW_CONTROL -> {
                    if (!wasBody) {
                        // There wasn't a body context before this flow control context. This means the flow context
                        // was preceded by a body that has been since removed, and is unreachable.
                        branchedStates.newBranch(localContext, "[entering flow control]", false);
                    }
                    branchedStates.processContext(localContext, child, modifyInstructions, dfProcessor);
                    wasBody = false;
                }
                default -> throw new MindcodeInternalError("Unexpected subcontext %s in IF context", child);
            }
        }

        if (bodies > 1) {
            // There's more than one body: since this is an if statement, both branches have been processed
            // Needs the merged result
            return branchedStates.getFinalStates(localContext);
        } else if (jumpResult == LogicBoolean.TRUE) {
            // There's only one body, and we know it was processed (jumpResult is TRUE, meaning there was no jump
            // around the body): we need the current state. However, there might have been a flow control context
            // before the body, which would have been processed as unreachable. It can be safely merged with the
            // current context --> we can retrieve the final state.
            return branchedStates.getFinalStates(localContext);
        } else {
            // There's only one body. Either we don't know it was executed, or we know it wasn't executed.
            // In both cases we need a default else branch to merge it with.
            branchedStates.newBranch(localContext, "[default else branch]", true);
            return branchedStates.getFinalStates(localContext);
        }
    }

    /// Specialized processing of a CASE context.
    ///
    /// @param localContext       context to process (must be an CASE context)
    /// @param variableStates     variable states at the beginning of the context
    /// @param modifyInstructions true if instructions may be modified in this run based on known variable states
    /// @return the resulting variable states
    private VariableStates processCaseContext(AstContext localContext, VariableStates variableStates, boolean modifyInstructions,
            DataFlowInstructionProcessor dfProcessor) {
        List<AstContext> children = localContext.children();
        Iterator<AstContext> iterator = children.iterator();
        if (!children.isEmpty() && children.getFirst().matches(INIT)) {
            AstContext child = iterator.next();
            variableStates = processContext(localContext, child, variableStates, modifyInstructions, dfProcessor);
        }

        if (localContext.findSubcontext(CONDITION) == null) {
            // This is an optimized case expression
            BranchedVariableStates branchedStates = new BranchedVariableStates(localContext, variableStates);
            boolean first = true;

            // The context has been optimized by CaseSwitcher
            while (iterator.hasNext()) {
                AstContext child = iterator.next();
                switch (child.subcontextType()) {
                    case BODY, ELSE -> {
                        if (first) {
                            first = false;
                        } else {
                            branchedStates.newBranch(localContext, "[case when]", true);
                        }
                        branchedStates.processContext(localContext, child, modifyInstructions, dfProcessor);
                    }
                    case FLOW_CONTROL -> {
                        branchedStates.processContext(localContext, child, modifyInstructions, dfProcessor);
                    }
                    default -> throw new MindcodeInternalError("Unexpected subcontext %s in CASE context", child);
                }
            }

            return branchedStates.getFinalStates(localContext);
        } else {
            CasedVariableStates casedStates = new CasedVariableStates(variableStates);
            while (iterator.hasNext()) {
                casedStates.processContext(localContext, iterator.next(), modifyInstructions, dfProcessor);
            }

            return casedStates.getOutgoingStates();
        }
    }

    /// Specialized processing of a JUMPS context - a jump table. The subcontext must be ARRAY.
    /// The jump table is expected to have all its effects expressed as side effects stored in the first instruction.
    /// This method applies these side effects and then skips the rest of the context.
    ///
    /// @param localContext       context to process (must be a JUMPS context)
    /// @param variableStates     variable states at the beginning of the context
    /// @param modifyInstructions true if instructions may be modified in this run based on known variable states
    /// @return the resulting variable states
    private VariableStates processJumpsContext(AstContext localContext, VariableStates variableStates, boolean modifyInstructions,
            DataFlowInstructionProcessor dfProcessor) {
        assert iterator != null;
        assert unreachables != null;

        if (!iterator.hasNext()) {
            throw new MindcodeInternalError("Unexpected empty jump table context");
        }

        // Can't use write as a side effect here, use reset instead
        boolean reachable = !unreachables.get(iterator.nextIndex());
        LogicInstruction instruction = iterator.next();
        instruction.getSideEffects().apply(
                variable -> variableStates.valueRead(variable, null, false, reachable),
                variable -> variableStates.valueReset(variable, true),
                variable -> variableStates.valueReset(variable, false));

        VariableStates result = instruction instanceof MultiJumpInstruction
                ? processInstruction(variableStates, instruction, modifyInstructions, reachable)
                : variableStates;

        while (iterator.hasNext()) {
            LogicInstruction ix = iterator.peek(0);
            if (!ix.belongsTo(localContext)) {
                break;
            }

            iterator.next();
        }

        return result;
    }

    /// Specialized processing of a short-circuiting context.
    ///
    /// @param localContext       context to process (must be an CASE context)
    /// @param variableStates     variable states at the beginning of the context
    /// @param modifyInstructions true if instructions may be modified in this run based on known variable states
    /// @return the resulting variable states
    private VariableStates processShortCircuitingContext(AstContext localContext, VariableStates variableStates, boolean modifyInstructions,
            DataFlowInstructionProcessor dfProcessor) {
        return new ShortCircuitedVariableStates().processContext(localContext, variableStates, modifyInstructions, dfProcessor);
    }

    /// Processes instructions inside a given context. Jumps outside the local context are processed by associating
    /// the current variable state with the target label. Local context might be the context being processed,
    /// or a parent context when jumps within that context are handled specifically (such as by processIfContext).
    ///
    /// @param localContext       context which is considered local
    /// @param context            context to process
    /// @param variableStates     variable states instance to update
    /// @param modifyInstructions true if instructions may be modified in this run based on known variable states
    /// @param dfProcessor       specialized instruction processing
    /// @return the resulting variable states
    private VariableStates processDefaultContext(AstContext localContext, AstContext context,
            VariableStates variableStates, boolean modifyInstructions, DataFlowInstructionProcessor dfProcessor) {
        assert iterator != null;
        assert unreachables != null;

        while (iterator.hasNext()) {
            LogicInstruction instruction = iterator.peek(0);
            if (!instruction.belongsTo(context)) {
                break;
            }

            variableStates = dfProcessor.processInstruction(localContext, context, variableStates, instruction);

            if (instruction.getAstContext() == context) {
                boolean reachable = !unreachables.get(iterator.nextIndex());
                iterator.next();
                long digest = variableStates.digest();
                variableStates = processInstruction(variableStates, instruction, modifyInstructions, reachable);
                if (digest == variableStates.digest()) {
                    trace("    No modifications to variable states " + variableStates.getId());
                } else {
                    final VariableStates tmp = variableStates;
                    indent(() -> tmp.print("After processing instruction"));
                }
            } else {
                AstContext childContext = Objects.requireNonNull(context.findDirectChild(instruction.getAstContext()));
                int position = iterator.currentIndex();
                variableStates = processContext(context, childContext, variableStates, modifyInstructions, dfProcessor);
                if (iterator.currentIndex() == position) {
                    throw new MindcodeInternalError("No progress on context.");
                }
            }
        }

        return variableStates;
    }

    private interface DataFlowInstructionProcessor {
        VariableStates processInstruction(AstContext localContext, AstContext context, VariableStates variableStates, LogicInstruction instruction);
    }

    private final DataFlowInstructionProcessor defaultDfProcessor = this::processJumps;

    private VariableStates processJumps(AstContext localContext, AstContext context, VariableStates variableStates, LogicInstruction instruction) {
        // This needs to be done for each active context: do not move this code into processInstruction
        // Jumps inside a RETURN context are caused by the return instruction and do not leave the local context,
        // but they do break the control flow and therefore need to be handled here.
        if (!variableStates.isIsolated() && instruction instanceof JumpInstruction jump
                && !context.matches(ARRAY, REMOTE_INIT)
                && !jump.getAstContext().matches(ARRAY, REMOTE_INIT)
                && (context.matches(AstContextType.RETURN, FLOW_CONTROL) ||
                    !getLabelInstruction(jump.getTarget()).belongsTo(localContext))) {

            if (jump.getTarget().isStateTransfer()) {
                VariableStates copy = variableStates.copy("nonlocal jump");
                copy.print("*** Storing variable states for label " + jump.getTarget().toMlog());
                labelStates.computeIfAbsent(jump.getTarget(), ix -> new ArrayList<>()).add(copy);
            }

            if (jump.isUnconditional()) {
                variableStates.setDead(false);
            }
        }

        return variableStates;
    }

    /// Updates given variable states associated with a given label instruction to include all states deposited on the
    /// label by all nonlocal jumps targeting that label.
    ///
    /// Note: Data Flow Optimizer only expects nonlocal jumps to target labels that are found later in the code than
    /// the jump. This is sufficient at the moment, as all nonlocal jumps are only generated by break, continue or
    /// return statements and adhere to the requirement.
    ///
    /// @param variableStates    current variable states
    /// @param label             label to process
    /// @return given variable states merged with all variable states stored at label
    private VariableStates resolveLabel(VariableStates variableStates, LogicLabel label) {
        List<VariableStates> states = labelStates.remove(label);
        if (states != null) {
            for (VariableStates state : states) {
                variableStates = variableStates.merge(state, true, "states for label " + label.toMlog());
            }
        }
        return variableStates;
    }

    /// Processes a single instruction.
    ///
    /// @param variableStates        variable states before executing the instruction
    /// @param instruction           instruction to process
    /// @param modifyInstructions    true if instructions may be modified in this run based on known variable states
    /// @param reachable             true if the instruction being processed is reachable
    /// @return variable states after executing the instruction
    private VariableStates processInstruction(VariableStates variableStates, LogicInstruction instruction,
            boolean modifyInstructions, boolean reachable) {
        Objects.requireNonNull(variableStates);
        Objects.requireNonNull(instruction);

        counter++;

        if (TRACE) {
            trace("*" + counter + " Processing instruction ix#" + instructionIndex(instruction) +
                    ": " + LogicInstructionPrinter.toString(instructionProcessor, instruction));

            if (counter == -1) {
                trace("Breakpoint");
            }
        }

        // Handle special cases
        switch (instruction) {
            case EmptyInstruction ix    -> { return variableStates; }
            case PushInstruction ix     -> { return variableStates.pushVariable(ix.getVariable()); }
            case PopInstruction ix      -> { return variableStates.popVariable(ix.getVariable()); }
            case LabeledInstruction ix  -> { return resolveLabel(variableStates, ix.getLabel()); }
            case EndInstruction ix      -> { return variableStates.setDead(true); }
            default -> { }
        }

        indentInc();

        if (modifyInstructions) {
            VariableStates copy = variableStates.isolatedCopy();
            VariableStates previous = getVariableStates(instruction);
            if (previous != null) {
                copy = copy.merge(previous, true, "split context merge");
            }
            trace("Storing variable states for instruction ix#" + instructionIndex(instruction) + ": " + copy.getId());
            putVariableStates(instruction, copy);
        }

        // Process inputs first, to handle instructions reading and writing the same variable
        // This needs to be done even when not modifying instructions, because it keeps track of read variables.

        // Try to find possible replacements of input arguments to this instruction
        List<TypedArgument> inputs = instruction.typedArgumentsStream()
                .filter(TypedArgument::isInput)
                .filter(a -> a.argument() instanceof LogicVariable)
                .toList();

        Map<LogicVariable, LogicValue> valueReplacements = new HashMap<>();
        for (TypedArgument argument : inputs) {
            LogicVariable variable = (LogicVariable) argument.argument();
            LogicValue constantValue = variableStates.valueRead(variable, instruction, reachable);
            if (argument.isInputOnly() && canEliminate(instruction, variable)) {
                if (constantValue != null) {
                    valueReplacements.put(variable, constantValue);
                } else {
                    LogicVariable equivalent = variableStates.findEquivalent(variable);
                    if (equivalent != null && !equivalent.equals(variable)) {
                        valueReplacements.put(variable, equivalent);
                    }
                }
            }
        }

        if (modifyInstructions) {
            Map<LogicVariable, LogicValue> prior = replacements.get(instruction);
            if (valueReplacements.isEmpty() || prior != null && !prior.equals(valueReplacements)) {
                replacements.put(instruction, Map.of());
                if (prior != null) {
                    trace("    Removed prior value replacements for current instruction.");
                }
            } else if (prior == null) {
                replacements.put(instruction, valueReplacements);
                if (TRACE) {
                    trace("    Detected the following possible value replacements for current instruction:");
                    valueReplacements.forEach((k, v) -> trace("       " + k.toMlog() + " --> " + v.toMlog()));
                }
            }
        }

        LogicValue value = evaluateInstruction(instruction,
                modifyInstructions || variableStates.isIsolated() ? valueReplacements : Map.of());

        // The instruction sets all its output values. Instructions not processed above will set all their output
        // variables to an unknown state (represented by null - actual null value in mlog would be represented by
        // a LogicNull instance).
        // The actual inferred values aren't reused unless modifyInstruction is true (prevents inferred values from
        // the first pass through a loop from being used during the second pass).
        instruction.outputArgumentsStream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .forEach(arg -> variableStates.valueSet(arg, instruction, value, modifyInstructions));

        // Function call side effects
        switch (instruction.getOpcode()) {
            case CALL, CALLREC -> {
                // Function may be null for array access
                MindcodeFunction function = instruction.getAstContext().function();
                if (function != null) {
                    variableStates.updateAfterFunctionCall(function, instruction);
                    if (modifyInstructions && optimizationContext.getEndingFunctions().contains(function)) {
                        functionEndStates.add(variableStates.copy("function end handling"));
                    }
                }
            }
        }

        // General side effects
        instruction.getSideEffects().apply(
                variable -> variableStates.valueRead(variable, instruction, false, reachable),
                variable -> variableStates.valueSet(variable, instruction, null, modifyInstructions),
                variable -> variableStates.valueReset(variable, false));

        indentDec();
        return variableStates;
    }

    // Try to evaluate the instruction
    private @Nullable LogicValue evaluateInstruction(LogicInstruction instruction, Map<LogicVariable, LogicValue> valueReplacements) {
        return switch (instruction) {
            case SetInstruction set when set.getValue() instanceof LogicLiteral literal -> literal;
            case SetInstruction set when set.getValue() instanceof LogicBuiltIn builtIn && !builtIn.isVolatile() -> builtIn;
            case OpInstruction op when op.getOperation().isDeterministic() -> evaluateOpInstruction(op, valueReplacements);
            default -> null;
        };
    }

    public void addUninitialized(LogicVariable variable) {
        if (variable.getType().isCompiler()) {
            if (OptimizationCoordinator.IGNORE_UNINITIALIZED) {
                warn("Internal error: compiler-generated variable '%s' is uninitialized.", variable.toMlog());
            } else {
                throw new MindcodeInternalError("Internal error: compiler-generated variable '%s' is uninitialized.", variable.toMlog());
            }
        }
        uninitialized.add(variable);
    }

    /// Determines whether it is possible to eliminate an assignment to a variable by given instruction. Elimination
    /// is generally allowed except in cases requiring special protection.
    ///
    /// @param instruction instruction setting the variable
    /// @param variable    variable being inspected
    /// @return true if this assignment can be safely eliminated
    boolean canEliminate(LogicInstruction instruction, LogicVariable variable) {
        if (isVolatile(instruction, variable)) return false;
        if (variable.getType() == ArgumentType.FUNCTION_RETVAL
                && (instruction.getOpcode() == Opcode.CALL || instruction.getOpcode() == Opcode.CALLREC)) return false;

        if (instruction.getAstContext().matches(AstContextType.MLOG) && !instruction.getLocalProfile().isMlogBlockOptimization()) return false;

        return switch (variable.getType()) {
            case MLOG_VARIABLE, PRESERVED, GLOBAL_PRESERVED, FUNCTION_RETADDR, PARAMETER -> false;

            case LOCAL_VARIABLE, FUNCTION_RETVAL -> {
                if (variable.isPreserved()) yield false;

                // Function output variables cannot be eliminated inside their functions - at this point we have no
                // information whether they're read somewhere. Outside their functions, they're processed normally
                // (can be optimized freely).
                // Output values in remote functions aren't protected either, since output values get copied to the
                // main processor within the function and aren't accessed elsewhere.
                // If they aren't read at all in the entire program, they'll be removed by DeadCodeEliminator.
                AstContext functionCtx = instruction.getAstContext().findTopContextOfType(AstContextType.FUNCTION_DEF);
                yield !variable.isOutput()
                      || functionCtx == null || functionCtx.function() == null                  // Not inside a function
                      || !variable.getFunctionPrefix().equals(functionCtx.functionPrefix())     // Different function
                      || functionCtx.function().isInline()
                      || functionCtx.function().isEntryPoint();                                 // Is a remotely called function
            }

            // Includes global variables
            default -> true;
        };
    }

    /// Tries to evaluate an OP instruction down to a constant value. Replaces variable arguments to the instruction
    /// with their inferred values if possible.
    ///
    /// @param op                instruction to evaluate
    /// @param valueReplacements value replacements to use
    /// @return a LogicLiteral representing the determined resulting value of the instruction,
    ///         or null if the instruction cannot be evaluated
    private @Nullable LogicLiteral evaluateOpInstruction(OpInstruction op, Map<LogicVariable, LogicValue> valueReplacements) {
        OpInstruction op1 = tryReplace(op, valueReplacements, op::getX, op::withX);
        OpInstruction op2 = op1.hasSecondOperand()
                ? tryReplace(op1, valueReplacements, op1::getY, op1::withY)
                : op1;

        return evaluateOpInstruction(op2);
    }

    /// Replaces a variable argument to the instruction with its inferred values if possible.
    ///
    /// @param op                instruction to be modified
    /// @param valueReplacements value replacements to use
    /// @param getArgument       lambda expression to extract the desired argument from the instruction
    /// @param replaceArgument   lambda to replace the desired argument with a new value
    /// @return a new, updated instruction, or the original one if no replacement is possible
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

    /// Helper class to manage variable states of branching statements (ifs, switched cases).
    private class BranchedVariableStates {
        /// The initial state of the statement, before branching. Set by constructor.
        private final VariableStates initial;

        /// State of the currently processed branch. Null if no branch was processed.
        private @Nullable VariableStates current;

        /// Final states of all branches merged together.
        private @Nullable VariableStates merged;

        private boolean wasReachable;

        private BranchedVariableStates(AstContext localContext, VariableStates initial) {
            this.initial = initial;
            trace(() -> "*** Creating branch states for local context cx#" + localContext.id);
        }

        /// Called when a body of a new branch is encountered. Closes the previous branch (if any) by merging it into
        /// the final states, and then creates variable states for the new branch by copying the initial state.
        ///
        /// @param reachable true if the context is reachable, false otherwise
        private void newBranch(AstContext localContext, String caller, boolean reachable) {
            wasReachable |= reachable;
            if (current != null) {
                merged = merged == null ? current : merged.merge(current, true, "old branch before starting new branch");
            }
            current = initial.copy("Local context cx#" + localContext.id + ": new conditional branch " + caller + (reachable ? "" : " (unreachable)"), reachable);
        }

        /// Processes the given context as part of the current branch.
        ///
        /// @param localContext       context which is considered local
        /// @param context            context to be processed
        /// @param modifyInstructions true if instructions may be modified in this run based on known variable states
        private void processContext(AstContext localContext, AstContext context, boolean modifyInstructions,
                DataFlowInstructionProcessor dfProcessor) {
            current = DataFlowOptimizer.this.processContext(localContext, context,
                    current == null ? initial.copy("new conditional branch") : current, modifyInstructions, dfProcessor);
        }

        /// Returns variable states obtained by merging final states of all processed branches together.
        ///
        /// @return final variable states of the branched expression
        private VariableStates getFinalStates(AstContext localContext) {
            trace(() -> "Getting final states (local context cx#" + localContext.id + ")");
            if (current == null) {
                throw new MindcodeInternalError("No current branch");
            }
            if (wasReachable) {
                return merged == null ? current : merged.merge(current, true, " obtaining final states");
            } else {
                // None of the branches was actually reachable. We use the initial context
                return initial;
            }
        }

        /// Returns variable states corresponding to the currently active branch.
        ///
        /// @return final variable states of the branch that was just processed
        private VariableStates getCurrentStates() {
            return current == null ? initial : current;
        }
    }

    /// Helper class to manage variable states of branching case statements.
    private class CasedVariableStates {
        private boolean skipFlow;

        /// Variable state matching the flow that enters the case expression. Each condition is processed on this flow.
        private VariableStates incoming;

        /// Variable state which accumulates all the conditions that occur before a body, and then it processes
        /// the body and flow contexts. ELSE context also creates a body. A separate instance is created before
        /// each body by combining the exit states of conditions leading to that body.
        private @Nullable VariableStates body;

        /// Variable state which combines all the exit states of individual bodies. Represents the final state
        /// of the entire case statement.
        private @Nullable VariableStates outgoing;

        private CasedVariableStates(VariableStates incoming) {
            this.incoming = incoming;
        }

        /// Processes the given case statement subcontext.
        ///
        /// @param localContext       context which is considered local
        /// @param context            context to be processed
        /// @param modifyInstructions true if instructions may be modified in this run based on known variable states
        private void processContext(AstContext localContext, AstContext context, boolean modifyInstructions,
                DataFlowInstructionProcessor dfProcessor) {
            switch (context.subcontextType()) {
                case CONDITION -> {
                    incoming = DataFlowOptimizer.this.processContext(localContext, context, incoming,
                            modifyInstructions, dfProcessor);
                    if (body == null) {
                        body = incoming.copy("case statement body");
                    } else {
                        body = body.merge(incoming, true, "merging case condition");
                    }
                    skipFlow = true;
                }

                case ELSE -> {
                    if (body != null) {
                        throw new MindcodeInternalError("Unexpected case statement structure (CONDITION before ELSE)");
                    }
                    body = DataFlowOptimizer.this.processContext(localContext, context, incoming, modifyInstructions, dfProcessor);
                }

                case BODY -> {
                    if (body == null) {
                        throw new MindcodeInternalError("Unexpected case statement structure (BODY without CONDITION)");
                    }
                    body = DataFlowOptimizer.this.processContext(localContext, context, body, modifyInstructions, dfProcessor);
                }

                case FLOW_CONTROL -> {
                    if (body == null) {
                        throw new MindcodeInternalError("Unexpected case statement structure (BODY without CONDITION)");
                    }
                    body = DataFlowOptimizer.this.processContext(localContext, context, body, modifyInstructions, dfProcessor);

                    if (skipFlow) {
                        skipFlow = false;
                    } else {
                        if (outgoing == null) {
                            outgoing = body;
                        } else {
                            outgoing = outgoing.merge(body, true, "merging body");
                        }
                        body = null;
                    }
                }

                default -> {
                    throw new MindcodeInternalError("Unexpected subcontext type: " + context.subcontextType());
                }
            }
        }

        /// @return outgoing variable states of the case expression
        private VariableStates getOutgoingStates() {
            trace(() -> "Getting final states");
            if (outgoing == null || body != null) {
                throw new MindcodeInternalError("Unexpected case statement structure (missing FLOW)");
            }
            return outgoing;
        }
    }

    /// Helper class to manage variable states of short-circuiting expression
    private class ShortCircuitedVariableStates {
        private final Set<LogicLabel> targets = new HashSet<>();

        /// Variable states valid at given labels. States are stored for all jump targets; states for labels lying
        /// outside the short-circuiting context are all merged in at the end of the context.
        private final Map<LogicLabel, VariableStates> labelStates = new HashMap<>();

        private ShortCircuitedVariableStates() {
        }

        /// Processes the given case statement subcontext.
        ///
        /// @param context            context to process
        /// @param modifyInstructions true if instructions may be modified in this run based on known variable states
        private VariableStates processContext(AstContext context, VariableStates variableStates, boolean modifyInstructions,
                DataFlowInstructionProcessor dfProcessor) {
            variableStates = DataFlowOptimizer.this.processDefaultContext(context, context, variableStates, modifyInstructions,
                    (lc, c, vs, mod) -> processInstruction(lc, context, vs,mod, dfProcessor));
            return getFinalStates(variableStates);
        }

        /// Processes a condition split in two parts
        ///
        /// @param leadingPart              first context to process
        /// @param trailingPart             second context to process
        /// @param modifyInstructions true if instructions may be modified in this run based on known variable states
        private VariableStates processContext(AstContext leadingPart, AstContext trailingPart, VariableStates variableStates,
                boolean modifyInstructions, DataFlowInstructionProcessor dfProcessor) {
            trace(() -> "=== Processing leading part of short-circuiting context; cx#" + leadingPart.id);
            seek(leadingPart);
            variableStates = DataFlowOptimizer.this.processDefaultContext(leadingPart, leadingPart,
                    variableStates, modifyInstructions,
                    (lc, c, vs, mod) -> processInstruction(lc, leadingPart, vs,mod, dfProcessor));

            trace(() -> "=== Processing trailing part of short-circuiting context; cx#" + trailingPart.id);
            seek(trailingPart);

            // Find the active part of the context
            assert iterator != null;
            while (iterator.hasNext()) {
                LogicInstruction instruction = iterator.peek(0);
                if (!instruction.belongsTo(trailingPart)) {
                    return getFinalStates(variableStates);
                }
                if (instruction instanceof LabelInstruction label && targets.contains(label.getLabel())) {
                    break;
                }
                iterator.next();
            }

            variableStates = DataFlowOptimizer.this.processDefaultContext(trailingPart, trailingPart,
                    variableStates, modifyInstructions,
                    (lc, c, vs, mod) -> processInstruction(lc, trailingPart, vs,mod, dfProcessor));

            trace("=== End of combined short-circuiting context processing");
            return getFinalStates(variableStates);
        }

        private VariableStates processInstruction(AstContext localContext, AstContext context, VariableStates variableStates,
                LogicInstruction instruction, DataFlowInstructionProcessor dfProcessor) {
            if (instruction.getAstContext() == context) {
                switch (instruction) {
                    case JumpInstruction jump -> {
                        targets.add(jump.getTarget());
                        VariableStates stored = labelStates.get(jump.getTarget());
                        if (stored == null) {
                            labelStates.put(jump.getTarget(), variableStates.copy("short-circuiting jump to " + jump.getTarget().toMlog()));
                        } else {
                            stored.merge(variableStates, true, "short-circuiting jump to " + jump.getTarget().toMlog());
                        }
                    }
                    case LabelInstruction label -> {
                        VariableStates stored = labelStates.remove(label.getLabel());
                        if (stored != null) {
                            variableStates.merge(stored, true, "short-circuiting label " + label.getLabel().toMlog());
                        }
                    }
                    default -> {}
                }
            }
            return dfProcessor == defaultDfProcessor && instruction.getAstContext() == context
                    ? variableStates
                    : dfProcessor.processInstruction(localContext, context, variableStates, instruction);
        }

        /// @return outgoing variable states of the case expression
        private VariableStates getFinalStates(VariableStates variableStates) {
            trace(() -> "Getting final states");

            labelStates.values().forEach(
                    state -> variableStates.merge(state, true, "merging outside labels"));
            return variableStates;
        }
    }
}
