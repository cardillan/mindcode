package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.evaluator.LogicReadable;
import info.teksol.mc.messages.CompilerMessage;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.messages.WARN;
import info.teksol.mc.mindcode.compiler.InstructionCounter;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraph;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.generation.variables.OptimizerContext;
import info.teksol.mc.mindcode.compiler.optimization.DataFlowVariableStates.VariableStates;
import info.teksol.mc.mindcode.compiler.postprocess.LogicInstructionPrinter;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.util.CollectionUtils;
import info.teksol.mc.util.TraceFile;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@NullMarked
public class OptimizationContext {
    private final CompilerProfile globalProfile;
    private final MessageConsumer messageConsumer;
    private final OptimizerExpressionEvaluator expressionEvaluator;
    private final InstructionProcessor instructionProcessor;
    private final OptimizerContext optimizerContext;
    private final List<LogicInstruction> program;
    private final CallGraph callGraph;
    private final AstContext rootContext;
    private final boolean remoteLibrary;

    private @Nullable FunctionDataFlow functionDataFlow;
    private @Nullable BitSet unreachableInstructions;

    private final TraceFile traceFile;

    /// Maps instructions to input variable states (i.e., states before the instruction is executed)
    private final Map<LogicInstruction, VariableStates> variableStates = new IdentityHashMap<>();

    /// Holds evaluation of first loop condition variables for loop rotator/unroller.
    private final Map<LogicInstruction, VariableStates> firstPassStates = new HashMap<>();

    /// Holds evaluation of first loop condition variables for loop rotator/unroller.
    private final Map<AstContext, VariableStates> loopVariables = new HashMap<>();

    /// Variables affected by added, removed or changed instructions are added to the stale list
    /// The information collected by DFO about these variables is unusable.
    private final Set<LogicVariable> staleVariables = new HashSet<>();

    /// Set of variables which were detected as uninitialized.
    private final Set<LogicVariable> uninitializedVariables = new HashSet<>();

    /// Maps labels to their respective instructions. Built once and then updated after each modification
    /// of the program.
    private final Map<LogicLabel, LabelInstruction> labels = new HashMap<>();

    /// Tracks all instructions that target a label. Unused labels (labels whose list of instructions would be empty)
    /// are removed from the program after each iteration finishes.
    private final Map<LogicLabel, List<LogicInstruction>> labelReferences = new HashMap<>();

    /// Tracks instructions in which variables appear (including side effects).
    private final Map<LogicVariable, List<LogicInstruction>> variableReferences = new HashMap<>();

    private int currentRun = 0;
    private int modifications = 0;
    private int insertions = 0;
    private int deletions = 0;
    private boolean updated;

    private boolean traceActive = true;

    OptimizationContext(TraceFile traceFile, MessageConsumer messageConsumer, CompilerProfile globalProfile,
            InstructionProcessor instructionProcessor, OptimizerContext optimizerContext, List<LogicInstruction> program,
            CallGraph callGraph, AstContext rootAstContext, boolean remoteLibrary) {
        this.traceFile = traceFile;
        this.messageConsumer = messageConsumer;
        this.globalProfile = globalProfile;
        this.instructionProcessor = instructionProcessor;
        this.optimizerContext = optimizerContext;
        this.program = program;
        this.callGraph = callGraph;
        this.rootContext = rootAstContext;
        this.remoteLibrary = remoteLibrary;

        expressionEvaluator = new OptimizerExpressionEvaluator(instructionProcessor);

        rebuildIndexes();
        adjustWeights();
    }

    public void rebuildIndexes() {
        labels.clear();
        labelReferences.clear();
        variableReferences.clear();

        instructionStream()
                .filter(LabelInstruction.class::isInstance)
                .map(LabelInstruction.class::cast)
                .forEach(this::addLabelInstruction);

        /* Create label references */
        instructionStream().forEachOrdered(this::addReferences);
    }

    public MessageConsumer getMessageConsumer() {
        return messageConsumer;
    }

    public CompilerProfile getGlobalProfile() {
        return globalProfile;
    }

    public InstructionProcessor getInstructionProcessor() {
        return instructionProcessor;
    }

    public void addDiagnosticData(Object data) {
        optimizerContext.addDiagnosticData(data);
    }

    public <T> void addDiagnosticData(Class<T> dataClass, List<T> data) {
        optimizerContext.addDiagnosticData(dataClass, data);
    }

    List<LogicInstruction> getProgram() {
        return program;
    }

    CallGraph getCallGraph() {
        return callGraph;
    }

    AstContext getRootContext() {
        return rootContext;
    }

    public boolean isRemoteLibrary() {
        return remoteLibrary;
    }

    int getModifications() {
        return modifications;
    }

    int getInsertions() {
        return insertions;
    }

    int getDeletions() {
        return deletions;
    }

    boolean isUpdated() {
        return updated;
    }

    public String getProgramText() {
        BitSet unreachables = computeUnreachableInstructions();
        return LogicInstructionPrinter.toStringWithSourceCode(instructionProcessor, program,
                index -> String.format(" [%c] cx#%-4d", unreachables.get(index) ? ' ' : 'x', program.get(index).getAstContext().id));
    }

    public String getProgramTextFullAst() {
        return LogicInstructionPrinter.toStringWithContextsFull(instructionProcessor, program);
    }

    void debugPrintProgram(String title, boolean outputTitle) {
        if (OptimizationCoordinator.DEBUG_PRINT && traceActive) {
            if (!OptimizationCoordinator.TRACE || outputTitle) {
                traceFile.outputProgram(title);
            }
            traceFile.outputProgram("Program before optimization:");
            traceFile.outputProgram(getProgramText());
        }
    }

    /// Prepares the instance for the next round of program modification/optimization.
    public int prepare() {
        if (updated) {
            unreachableInstructions = null;
            functionDataFlow = null;
            rebuildAstContextTree();
        }
        modifications = 0;
        insertions = 0;
        deletions = 0;
        updated = false;

        return ++currentRun;
    }

    public void finish() {
        if (!iterators.isEmpty()) {
            throw new IllegalStateException("Unclosed iterators.");
        }
    }

    public void outputUninitializedVariables(MessageConsumer messageConsumer) {
        uninitializedVariables.stream().filter(v -> !v.isNoinit())
                .sorted(Comparator.comparing(LogicVariable::sourcePosition))
                .map(v -> CompilerMessage.warn(v.sourcePosition(), WARN.VARIABLE_NOT_INITIALIZED, v.getFullName()))
                .forEach(messageConsumer);
    }

    //<editor-fold desc="Common optimizer functionality">
    private static class FunctionDataFlow {
        /// Maps function prefix to a list of variables directly or indirectly read by the function
        private final Map<MindcodeFunction, Set<LogicVariable>> functionReads = new HashMap<>();

        /// Maps function prefix to a list of variables directly or indirectly written by the function
        private final Map<MindcodeFunction, Set<LogicVariable>> functionWrites = new HashMap<>();

        /// Contains function prefix of functions that may directly or indirectly call the end() instruction.
        private final Set<MindcodeFunction> endingFunctions = new HashSet<>();
    }

    /// This method analyzes the control flow of the program. It starts at the first instruction and visits
    /// every instruction reachable from there, either by advancing to the next instruction, or by a jump/goto/call.
    /// Bits of visited instruction are cleared and aren't inspected again. When all code paths have reached an
    /// end or an already visited instruction, the analysis stops, and what is left are all unreachable instructions.
    ///
    /// @return a BitSet containing positions of unreachable instructions
    public BitSet getUnreachableInstructions() {
        if (unreachableInstructions == null) {
            if (globalProfile.getOptimizationLevel(Optimization.UNREACHABLE_CODE_ELIMINATION) == OptimizationLevel.NONE) {
                unreachableInstructions = new BitSet(program.size());
            } else {
                unreachableInstructions = computeUnreachableInstructions();
            }
        }
        return unreachableInstructions;
    }

    private BitSet computeUnreachableInstructions() {
        BitSet unreachableInstructions = new BitSet(program.size());
        // Also serves as a data stop for reaching the end of the instruction list naturally.
        unreachableInstructions.set(0, program.size());
        Queue<Integer> heads = new ArrayDeque<>();
        heads.offer(0);
        callGraph.getFunctions().stream()
                .map(MindcodeFunction::getEntryPointLabel)
                .filter(Objects::nonNull)
                .map(label -> firstInstructionIndex(ix -> ix instanceof LabelInstruction l && l.getLabel().equals(label)))
                .filter(i -> i >= 0)
                .forEach(heads::offer);

        MainLoop:
        while (!heads.isEmpty()) {
            int index = heads.poll();
            while (unreachableInstructions.get(index)) {
                unreachableInstructions.clear(index);
                LogicInstruction ix = program.get(index);
                switch (ix.getOpcode()) {
                    case END -> {
                        // `end` inside an mlog block is ignored - we don't see into the data flow there
                        if (!ix.getAstContext().matches(AstContextType.MLOG)) continue MainLoop;
                    }
                    case RETURN, RETURNREC -> {
                        continue MainLoop;
                    }
                    case JUMP -> {
                        JumpInstruction jump = (JumpInstruction) ix;
                        int labelIndex = findLabelIndex(jump.getTarget());
                        if (labelIndex >= 0) {
                            heads.offer(labelIndex);
                        }
                        if (jump.isUnconditional()) {
                            continue MainLoop;
                        }
                    }
                    case CALL -> {
                        CallInstruction call = (CallInstruction) ix;
                        heads.offer(findLabelIndex(call.getCallAddr()));
                    }
                    case CALLREC -> {
                        CallRecInstruction call = (CallRecInstruction) ix;
                        heads.offer(findLabelIndex(call.getCallAddr()));
                        heads.offer(findLabelIndex(call.getRetAddr()));
                        continue MainLoop;
                    }
                    case MULTIJUMP, MULTICALL -> {
                        for (int i = 0; i < program.size(); i++) {
                            if (program.get(i) instanceof MultiLabelInstruction gli && gli.getMarker().equals(ix.getMarker())) {
                                heads.offer(i);
                            }
                        }
                        // Terminate control flow for MultiJump
                        if (ix.getOpcode() == Opcode.MULTIJUMP) continue MainLoop;
                    }
                }

                if (ix.getCallReturn() != LogicLabel.EMPTY) {
                    heads.offer(findLabelIndex(ix.getCallReturn()));
                    continue MainLoop;
                }

                index++;
            }
        }

        return unreachableInstructions;
    }

    private int findLabelIndex(LogicLabel label) {
        int index = firstInstructionIndex(ix -> ix instanceof LabelInstruction l && l.getLabel().equals(label));
        if (index < 0) {
            //throw new IllegalArgumentException("Illegal label: " + label);
        }
        return index;
    }

    /// Creates lists of variables directly or indirectly read/written by each function, and a list of functions
    /// directly or indirectly calling end()
    private FunctionDataFlow getFunctionDataFlow() {
        if (functionDataFlow == null) {
            getUnreachableInstructions();               // Make sure they're initialized
            functionDataFlow = new FunctionDataFlow();

            getRootContext().children().stream()
                    .filter(AstContext::isFunction)
                    .forEachOrdered(this::analyzeFunctionVariables);

            //noinspection StatementWithEmptyBody
            while (propagateFunctionReadsAndWrites()) ;
        }

        return functionDataFlow;
    }

    public Map<MindcodeFunction, Set<LogicVariable>> getAllFunctionReads() {
        return getFunctionDataFlow().functionReads;
    }

    public Set<LogicVariable> getFunctionReads(MindcodeFunction function) {
        return getFunctionDataFlow().functionReads.getOrDefault(function, Set.of());
    }

    public Map<MindcodeFunction, Set<LogicVariable>> getAllFunctionWrites() {
        return getFunctionDataFlow().functionWrites;
    }

    public Set<LogicVariable> getFunctionWrites(MindcodeFunction function) {
        return getFunctionDataFlow().functionWrites.getOrDefault(function, Set.of());
    }

    public Set<MindcodeFunction> getEndingFunctions() {
        return getFunctionDataFlow().endingFunctions;
    }

    /// Analyzes reads, writes and end() calls by a single function.
    ///
    /// @param context context of the function to analyze
    private void analyzeFunctionVariables(AstContext context) {
        assert unreachableInstructions != null;
        assert functionDataFlow != null;
        assert context.function() != null;

        Set<LogicVariable> reads = new HashSet<>();
        Set<LogicVariable> writes = new HashSet<>();

        try (LogicIterator it = createIteratorAtContext(context)) {
            while (it.hasNext()) {
                int index = it.nextIndex();
                LogicInstruction ix = it.next();
                if (!(ix instanceof PushOrPopInstruction) && !unreachableInstructions.get(index)) {
                    ix.inputArgumentsStream()
                            .filter(LogicVariable.class::isInstance)
                            .map(LogicVariable.class::cast)
                            .forEachOrdered(reads::add);

                    ix.outputArgumentsStream()
                            .filter(LogicVariable.class::isInstance)
                            .map(LogicVariable.class::cast)
                            .forEachOrdered(writes::add);

                    if (ix.getSideEffects() != SideEffects.NONE) {
                        reads.addAll(ix.getSideEffects().reads());
                        writes.addAll(ix.getSideEffects().writes());
                        writes.addAll(ix.getSideEffects().resets());
                    }

                    if (ix instanceof EndInstruction) {
                        functionDataFlow.endingFunctions.add(context.function());
                    }
                }
            }
        }

        functionDataFlow.functionReads.put(context.function(), reads);
        functionDataFlow.functionWrites.put(context.function(), writes);
    }

    /// Propagates variable reads/writes and end() calls to calling functions.
    ///
    /// @return true if the propagation leads to some modifications
    private boolean propagateFunctionReadsAndWrites() {
        assert functionDataFlow != null;

        CallGraph callGraph = getCallGraph();
        boolean modified = false;
        for (MindcodeFunction function : callGraph.getFunctions()) {
            if (!function.isInline()) {
                Set<LogicVariable> reads = functionDataFlow.functionReads.computeIfAbsent(function, f -> new HashSet<>());
                Set<LogicVariable> writes = functionDataFlow.functionWrites.computeIfAbsent(function, f -> new HashSet<>());
                int size = reads.size() + writes.size() + functionDataFlow.endingFunctions.size();
                function.getDirectCalls().stream()
                        .filter(f -> f.isUsed() && !f.isInline() && !f.isMain())
                        .forEachOrdered(callee -> {
                            reads.addAll(functionDataFlow.functionReads.get(callee));
                            writes.addAll(functionDataFlow.functionWrites.get(callee));
                            if (functionDataFlow.endingFunctions.contains(callee)) {
                                functionDataFlow.endingFunctions.add(function);
                            }
                        });

                // Repeat if there are changes
                modified |= size != reads.size() + writes.size() + functionDataFlow.endingFunctions.size();
            }
        }

        return modified;
    }
    //</editor-fold>

    //<editor-fold desc="Label & variable tracking">
    public void putVariableStates(LogicInstruction instruction, VariableStates variableStates) {
        this.variableStates.put(instruction, variableStates);
    }

    public @Nullable VariableStates getVariableStates(LogicInstruction instruction) {
        return variableStates.get(instruction);
    }

    public void storeFirstPassStates(LogicInstruction instruction, VariableStates variableStates) {
        firstPassStates.put(instruction, variableStates);
    }

    public @Nullable VariableStates getFirstPassStates(LogicInstruction instruction) {
        return firstPassStates.get(instruction);
    }

    public void storeLoopVariables(AstContext loopContext, VariableStates variableStates) {
        loopVariables.put(loopContext, variableStates);
    }

    public @Nullable VariableStates getLoopVariables(AstContext loopContext) {
        return loopVariables.get(loopContext);
    }

    public LogicValue resolveValue(@Nullable VariableStates variableStates, LogicValue value) {
        if (variableStates != null && value instanceof LogicVariable v && !staleVariables.contains(v)) {
            var newValue = variableStates.findVariableValue(v);
            if (newValue != null && newValue.getConstantValue() != null) {
                return newValue.getConstantValue();
            }
        }
        return value;
    }

    public @Nullable LogicInstruction findDefiningInstruction(LogicInstruction instruction, LogicVariable variable) {
        if (!staleVariables.contains(variable)) {
            VariableStates variableStates = getVariableStates(instruction);
            if (variableStates != null) {
                DataFlowVariableStates.VariableValue variableValue = variableStates.findVariableValue(variable);
                return variableValue != null ? variableValue.getInstruction() : null;
            }
        }
        return null;
    }

    public boolean isStale(LogicVariable variable) {
        return staleVariables.contains(variable);
    }

    public void clearVariableStates() {
        variableStates.clear();
        firstPassStates.clear();
        staleVariables.clear();
    }

    public LabelInstruction getLabelInstruction(LogicLabel label) {
        LabelInstruction labelInstruction = labels.get(label);
        if (labelInstruction == null) {
            throw new MindcodeInternalError("Unknown label " + label);
        }
        return labelInstruction;
    }

    public int getLabelInstructionIndex(LogicLabel label) {
        LabelInstruction labelInstruction = getLabelInstruction(label);
        return firstInstructionIndex(ix -> ix == labelInstruction);
    }

    private void addLabelInstruction(LabelInstruction instruction) {
        if (labels.containsKey(instruction.getLabel())) {
            throw new MindcodeInternalError("Adding duplicate label %s.", instruction.getLabel());
        }
        labels.put(instruction.getLabel(), instruction);
    }

    private void removeLabelInstruction(LabelInstruction instruction) {
        if (labels.remove(instruction.getLabel()) == null) {
            throw new MindcodeInternalError("Removing nonexistent label %s.", instruction.getLabel());
        }
    }

    private void addReferences(LogicInstruction instruction) {
        final Function<LogicLabel, List<LogicInstruction>> mappingFunction = _ -> new ArrayList<>();
        switch (instruction) {
            case JumpInstruction ix         -> labelReferences.computeIfAbsent(ix.getTarget(), mappingFunction).add(ix);
            case SetAddressInstruction ix   -> labelReferences.computeIfAbsent(ix.getLabel(), mappingFunction).add(ix);
            case CallInstruction ix         -> labelReferences.computeIfAbsent(ix.getCallAddr(), mappingFunction).add(ix);
            case MultiTargetInstruction ix  -> {
                if (ix.getTarget() instanceof LogicLabel label) {
                    labelReferences.computeIfAbsent(label, mappingFunction).add(ix);
                }
                labelReferences.computeIfAbsent(ix.getMarker(), mappingFunction).add(ix);
            }
            case CallRecInstruction ix -> {
                labelReferences.computeIfAbsent(ix.getCallAddr(), mappingFunction).add(ix);
                labelReferences.computeIfAbsent(ix.getRetAddr(), mappingFunction).add(ix);
            }
            default -> { }
        }

        instruction.getAllArguments()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .distinct()
                .forEach(v -> variableReferences.computeIfAbsent(v, _ -> new ArrayList<>()).add(instruction));
    }

    private void removeReferences(LogicInstruction instruction) {
        switch (instruction) {
            case JumpInstruction ix         -> clearReferences(ix.getTarget(), ix);
            case SetAddressInstruction ix   -> clearReferences(ix.getLabel(), ix);
            case CallInstruction ix         -> clearReferences(ix.getCallAddr(), ix);
            case MultiTargetInstruction ix  -> {
                if (ix.getTarget() instanceof LogicLabel label) {
                    clearReferences(label, ix);
                }
                clearReferences(ix.getMarker(), ix);
            }
            case CallRecInstruction ix -> {
                clearReferences(ix.getCallAddr(), ix);
                clearReferences(ix.getRetAddr(), ix);
            }
            default -> { }
        }

        instruction.getAllArguments()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .distinct()
                .forEach(v -> variableReferences.get(v).remove(instruction));
    }

    private void clearReferences(LogicLabel label, LogicInstruction reference) {
        List<LogicInstruction> references = labelReferences.get(label);
        references.removeIf(ix -> ix == reference);
    }

    public void removeInactiveInstructions() {
        try (LogicIterator it = createIterator()) {
            while (it.hasNext()) {
                switch (it.next()) {
                    case LabelInstruction l      when !l.getLabel().isRemote() && hasNoReferences(l.getLabel()) -> it.remove();
                    case MultiLabelInstruction g when hasNoReferences(g.getLabel()) && hasNoReferences(g.getMarker()) -> it.remove();
                    case EmptyInstruction noop -> it.remove();
                    default -> { }
                }
            }
        }
    }

    private boolean hasNoReferences(LogicLabel label) {
        List<LogicInstruction> references = labelReferences.get(label);
        return references == null || references.isEmpty();
    }

    public boolean isActive(LogicLabel label) {
        List<LogicInstruction> references = labelReferences.get(label);
        return references != null && !references.isEmpty();
    }

    public List<LogicInstruction> getLabelReferences(LogicLabel label) {
        return labelReferences.getOrDefault(label, List.of());
    }

    public List<LogicInstruction> getVariableReferences(LogicVariable variable) {
        return variableReferences.getOrDefault(variable, List.of());
    }

    public boolean isUsedOutsideOf(LogicVariable variable, AstContext context) {
        return getVariableReferences(variable).stream().anyMatch(ix -> !ix.getAstContext().belongsTo(context));
    }

    public Set<LogicVariable> getUninitializedVariables() {
        return uninitializedVariables;
    }

    public void addUninitializedVariable(LogicVariable variable) {
        if (variable.getType().isCompiler()) {
             if (OptimizationCoordinator.IGNORE_UNINITIALIZED) {
                 instructionProcessor.addMessage(OptimizerMessage.warn("Internal error: compiler-generated variable '%s' is uninitialized.", variable.toMlog()));
             } else {
                 throw new MindcodeInternalError("Internal error: compiler-generated variable '%s' is uninitialized.", variable.toMlog());
             }
        }
        uninitializedVariables.add(variable);
    }

    public void addUninitializedVariables(Collection<LogicVariable> variables) {
        variables.forEach(this::addUninitializedVariable);
    }

    //</editor-fold>

    //<editor-fold desc="Expression evaluation">
    public OptimizerExpressionEvaluator getExpressionEvaluator() {
        return expressionEvaluator;
    }

    public @Nullable LogicLiteral evaluateOpInstruction(OpInstruction op) {
        return expressionEvaluator.evaluateOpInstruction(op);
    }

    public @Nullable OpInstruction extendedEvaluate(VariableStates variableStates, OpInstruction op1) {
        return expressionEvaluator.extendedEvaluate(variableStates, op1);
    }

    public OpInstruction normalize(OpInstruction op) {
        return expressionEvaluator.normalize(op);
    }

    public OpInstruction normalizeMul(OpInstruction op, LogicVariable variable, LogicNumber number) {
        return expressionEvaluator.normalizeMul(op, variable, number);
    }

    public @Nullable LogicLiteral evaluate(SourcePosition sourcePosition, Operation operation, LogicReadable a, LogicReadable b) {
        return expressionEvaluator.evaluate(sourcePosition, operation, a, b);
    }

    /// Evaluates the given condition. Supports both short-circuited and fully evaluated ones. Returns values:
    /// - `TRUE` if the condition's value is true (true branch is entered)
    /// - `FALSE` if the condition's value is false (false branch is entered)
    /// - `null` id the condition's value cannot be determined
    ///
    /// @param conditionContext context of the condition to evaluate
    /// @return the condition's value, or null if it cannot be determined'
    public @Nullable LogicBoolean evaluateJumpCondition(AstContext conditionContext) {
        return evaluateCondition(conditionContext, variableStates);
    }

    /// Evaluates the given condition. Supports both short-circuited and fully evaluated ones. Returns values:
    /// - `TRUE` if the condition's value is true (loop is entered)
    /// - `FALSE` if the condition's value is false (loop is skipped)
    /// - `null` id the condition's value cannot be determined
    ///
    /// @param conditionContext context of the condition to evaluate
    /// @return the condition's value, or null if it cannot be determined'
    public @Nullable LogicBoolean evaluateLoopEntryCondition(AstContext conditionContext) {
        return evaluateCondition(conditionContext, firstPassStates);
    }

    public boolean isShortCircuitCondition(AstContext conditionContext) {
        return conditionContext.children().size() == 1 && conditionContext.children().getFirst()
                .matches(AstContextType.SCBE_COND, AstContextType.SCBE_OPER);
    }

    /// Evaluates the given condition. Supports both short-circuited and fully evaluated ones. Returns values:
    /// - `TRUE` if the condition's value is true (true branch or loop is entered)
    /// - `FALSE` if the condition's value is false (false branch is entered or loop is skipped)
    /// - `null` id the condition's value cannot be determined
    ///
    /// @param conditionContext context of the condition to evaluate
    /// @param variableStates variable states to use for evaluation (first-pass or full)
    /// @return the condition's value, or null if it cannot be determined'
    private @Nullable LogicBoolean evaluateCondition(AstContext conditionContext, Map<LogicInstruction, VariableStates> variableStates) {
        if (!conditionContext.matches(AstSubcontextType.CONDITION)) {
            throw new IllegalArgumentException("Expected condition context, got " + conditionContext);
        }

        LogicList instructions = contextInstructions(conditionContext);
        if (isShortCircuitCondition(conditionContext)) {
            AstContext shortCircuitContext = conditionContext.children().getFirst();

            // Simple control-flow analysis
            // We know only forward jumps are possible
            LogicInstruction lastInstruction = instructions.getLast();
            Set<LogicLabel> activeTargets = new HashSet<>();
            boolean active = true;

            for (LogicInstruction ix : instructions) {
                if (ix.getAstContext() != shortCircuitContext) continue;

                switch (ix) {
                    case JumpInstruction jump -> {
                        // When waiting for a specific target, skip jump
                        if (active) {
                            LogicBoolean jumpResult = evaluateConditionalInstruction(jump, variableStates);
                            if (jumpResult == LogicBoolean.TRUE) {
                                activeTargets.add(jump.getTarget());
                                active = false;
                                println(jump + ": TRUE, control flow inactive");
                                printActiveTargets(activeTargets);
                            } else if (jumpResult == null) {
                                println(jump + ": unknown, adding active target");
                                activeTargets.add(jump.getTarget());
                                printActiveTargets(activeTargets);
                            } else {
                                println(jump + ": FALSE");
                            }
                        }
                    }
                    case LabelInstruction l -> {
                        if (l == lastInstruction) {
                            // The last label in the context, if it exists, is the resulting label
                            if (active) {
                                activeTargets.add(l.getLabel());
                                println(l + ": arrived at final target while active");
                                printActiveTargets(activeTargets);
                            }
                        } else {
                            boolean removed = activeTargets.remove(l.getLabel());
                            println(l + (removed ? ": active target, control flow activated" : ": inactive target"));
                            printActiveTargets(activeTargets);
                            if (removed) active = true;
                        }
                    }
                    case EmptyInstruction noop -> { }
                    default -> { }
                }
            }

            LogicBoolean result = switch (activeTargets.size()) {
                // If there's no active target, the condition flows into the true branch naturally
                case 0 -> LogicBoolean.TRUE;

                // If there's only one active target, and the condition exit point isn't active, the active target decides the fate
                // (should be always false, btw)
                case 1 -> active ? null : activeTargets.stream().findFirst()
                        .map(l -> LogicBoolean.get(getLabelInstruction(l).getAstContext() == shortCircuitContext)).get();

                // Multiple active targets: undecidable
                default -> null;
            };

            println("RESULT: " + result + "\n\n");
            return result;
        } else {
            long jumps = instructions.stream().filter(JumpInstruction.class::isInstance).count();
            LogicBoolean jumpResult = jumps == 0 ? LogicBoolean.FALSE :
                    jumps == 1 && (instructions.getFromEnd(0) instanceof JumpInstruction jump)
                            ? evaluateConditionalInstruction(jump, variableStates)  // Evaluate the jump
                            : null;                                                       // We don't know

            return negate(jumpResult);
        }
    }

    private @Nullable LogicBoolean negate(@Nullable LogicBoolean value) {
        return value == null ? null : value.not();
    }

    private void println(String message) {
//        System.out.println(message);
    }

    private void printActiveTargets(Set<LogicLabel> possibleTargets) {
        println(possibleTargets.stream().map(LogicLabel::toMlog).sorted()
                .collect(Collectors.joining(", ", "    Active targets: [", "]")));
    }


    private @Nullable LogicBoolean evaluateConditionalInstruction(ConditionalInstruction cond,
            Map<LogicInstruction, VariableStates> variableStates) {
        return cond.isUnconditional()
                ? LogicBoolean.TRUE
                : evaluatePlainCondition(cond, cond.getX(), cond.getY(), variableStates);
    }

    public @Nullable LogicBoolean evaluatePlainCondition(ConditionalInstruction cond, LogicValue valueX, LogicValue valueY) {
        return evaluatePlainCondition(cond, valueX, valueY, variableStates);
    }

    private @Nullable LogicBoolean evaluatePlainCondition(ConditionalInstruction cond, LogicValue valueX, LogicValue valueY,
            Map<LogicInstruction, VariableStates> variableStates) {
        if (cond.isUnconditional()) return LogicBoolean.TRUE;

        VariableStates instructionVariableStates = variableStates.get(cond);

        // Avoid creating new instruction just for the evaluation
        LogicValue x = resolveValue(instructionVariableStates, valueX);
        LogicValue y = resolveValue(instructionVariableStates, valueY);

        if (x.isConstant() && y.isConstant()) {
            LogicLiteral result = expressionEvaluator.evaluate(cond.sourcePosition(), cond.getCondition().toOperation(), x, y);
            if (result instanceof LogicBoolean b) {
                return b;
            }
        }

        return null;
    }
    //</editor-fold>

    //<editor-fold desc="AST context tree updates">
    private void adjustWeights() {
        // Weights of stackless functions are recomputed until they stabilize
        Map<LogicLabel, Double> updatedWeights;
        boolean modified;
        do {
            updatedWeights = program.stream()
                    .filter(CallingInstruction.class::isInstance)
                    .map(CallingInstruction.class::cast)
                    .collect(Collectors.groupingBy(CallingInstruction::getCallAddr,
                            Collectors.summingDouble(ix -> ix.getAstContext().totalWeight())));

            modified = updateWeights(updatedWeights, false);
        } while (modified);

        // Weights of recursive functions are updated just once
        updateWeights(updatedWeights, true);
    }

    private boolean updateWeights(Map<LogicLabel, Double> updatedWeights, boolean recursive) {
        boolean modified = false;
        for (AstContext topContext : getRootContext().children()) {
            if (topContext.function() != null) {
                MindcodeFunction function = topContext.function();
                if (function.isRecursive() == recursive) {
                    Double weight = updatedWeights.get(function.getLabel());
                    if (weight != null && topContext.weight() != weight) {
                        modified = true;
                        topContext.updateWeight(weight);
                    }
                }
            }
        }
        return modified;
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
            } else if (children.getLast() != context) {
                for (AstContext ctx : children) {
                    if (context == ctx) {
                        // Some optimization moved instructions in such a way that
                        // instructions in this context do not form a continuous region.
                        throw new MindcodeInternalError("Discontinuous AST context " + context);
                    }
                }
                children.add(context);
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="Finding instructions by position">
    /// Return the instruction at the given position in the program.
    ///
    /// @param index index of the instruction
    /// @return the instruction at given index
    public LogicInstruction instructionAt(int index) {
        if (index < 0 || index >= program.size()) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for length " + program.size());
        }
        return program.get(index);
    }

    /// Returns the instruction preceding the given instruction. If such an instruction doesn't exist
    /// (because the reference instruction is the first one), returns null. If the reference instruction
    /// isn't found in the program, an exception is thrown.
    ///
    /// @param instruction instruction to find in the program
    /// @return instruction before the reference instruction
    @Nullable LogicInstruction instructionBefore(LogicInstruction instruction) {
        int index = existingInstructionIndex(instruction);
        return index == 0 ? null : instructionAt(index - 1);
    }

    /// Returns the instruction following the given instruction. If such an instruction doesn't exist
    /// (because the reference instruction is the last one), returns null. If the reference instruction
    /// isn't found in the program, an exception is thrown.
    ///
    /// @param instruction instruction to find in the program
    /// @return instruction after the reference instruction
    @Nullable LogicInstruction instructionAfter(LogicInstruction instruction) {
        int index = existingInstructionIndex(instruction) + 1;
        return index == program.size() ? null : instructionAt(index);
    }

    /// Provides a sublist of the current program. Will fail when such a sublist cannot be created.
    /// The returned list won't reflect further changes to the program.
    ///
    /// @param fromIndex starting index
    /// @param toIndex ending index (exclusive)
    /// @return a List containing given instructions.
    List<LogicInstruction> instructionSubList(int fromIndex, int toIndex) {
        return List.copyOf(program.subList(fromIndex, toIndex));
    }

    /// Provides a sublist of the current program. Will fail when such a sublist cannot be created.
    /// The returned list won't reflect further changes to the program.
    ///
    /// @param fromInstruction first instruction in the list
    /// @param toInstruction last instruction in the list (inclusive)
    /// @return a List containing given instructions.
    public List<LogicInstruction> instructionSubList(LogicInstruction fromInstruction, LogicInstruction toInstruction) {
        return instructionSubList(instructionIndex(fromInstruction), instructionIndex(toInstruction) + 1);
    }

    /// Provides a stream of all instructions in the program.
    ///
    /// @return an instruction stream
    Stream<LogicInstruction> instructionStream() {
        return program.stream();
    }

    /// Locates an instruction based on instance identity and returns its index.
    /// Returns -1 when such instruction doesn't exist.
    ///
    /// @param instruction instruction to locate
    /// @return index of the instruction, or -1 if the instruction isn't found.
    int instructionIndex(LogicInstruction instruction) {
        for (int i = 0; i < program.size(); i++) {
            if (instruction == program.get(i)) {
                return i;
            }
        }
        return -1;
    }

    /// Returns the index of the given instruction. When the instruction isn't found, an exception is thrown.
    int existingInstructionIndex(LogicInstruction instruction) {
        for (int i = 0; i < program.size(); i++) {
            if (instruction == program.get(i)) {
                return i;
            }
        }
        throw new NoSuchElementException("Instruction not found in program.\nInstruction: " + instruction);
    }
    //</editor-fold>

    //<editor-fold desc="Finding instructions by properties">

    /// Starting at the given index, finds the first instruction matching the predicate.
    /// Returns the index or -1 if not found.
    ///
    /// @param startIndex index to start search from, inclusive
    /// @param matcher predicate matching sought instruction
    /// @return index of the first instruction matching the predicate
    public int firstInstructionIndex(int startIndex, Predicate<LogicInstruction> matcher) {
        return CollectionUtils.indexOf(program, startIndex, matcher);
    }

    /// Finds the first instruction matching predicate in the entire program.
    /// Returns the index or -1 if not found.
    ///
    /// @param matcher predicate matching sought instruction
    /// @return index of the first instruction matching the predicate
    public int firstInstructionIndex(Predicate<LogicInstruction> matcher) {
        return CollectionUtils.indexOf(program, 0, matcher);
    }

    /// Finds the last instruction matching predicate up to the given instruction index.
    /// Returns the index or -1 if not found.
    ///
    /// @param startIndex index to end search at, inclusive
    /// @param matcher predicate matching sought instruction
    /// @return index of the last instruction matching the predicate, up to the specified index
    public int lastInstructionIndex(int startIndex, Predicate<LogicInstruction> matcher) {
        return CollectionUtils.lastIndexOf(program, startIndex, matcher);
    }

    /// Finds the last instruction matching predicate in the entire program.
    /// Returns the index or -1 if not found.
    ///
    /// @param matcher predicate matching sought instruction
    /// @return index of the last instruction matching the predicate
    public int lastInstructionIndex(Predicate<LogicInstruction> matcher) {
        return CollectionUtils.lastIndexOf(program, program.size() - 1, matcher);
    }

    /// Finds a first non-label instruction following a label
    /// Returns the index or -1 if not found.
    /// If the label doesn't exist in the program, an exception is thrown.
    ///
    /// @param label target label
    /// @return first non-label instruction following the label
    public int labeledInstructionIndex(LogicLabel label) {
        LabelInstruction labelInstruction = getLabelInstruction(label);
        int labelIndex = firstInstructionIndex(ix -> ix == labelInstruction);
        if (labelIndex < 0) {
            throw new MindcodeInternalError("Label not found in program.\nLabel: " + label);
        }
        return firstInstructionIndex(labelIndex + 1,
                ix -> !(ix instanceof LabeledInstruction) && !(ix instanceof EmptyInstruction));
    }

    /// Starting at the given index, finds the first instruction matching the predicate. Return null if not found.
    ///
    /// @param startIndex index to start search from, inclusive
    /// @param matcher predicate matching sought instruction
    /// @return the first instruction matching the predicate
    public @Nullable LogicInstruction firstInstruction(int startIndex, Predicate<LogicInstruction> matcher) {
        int result = firstInstructionIndex(startIndex, matcher);
        return result < 0 ? null : program.get(result);
    }

    /// Finds the first instruction matching predicate. Returns null if not found.
    ///
    /// @param matcher predicate matching sought instruction
    /// @return the first instruction in the entire program matching the predicate
    public @Nullable LogicInstruction firstInstruction(Predicate<LogicInstruction> matcher) {
        int result = firstInstructionIndex(matcher);
        return result < 0 ? null : program.get(result);
    }

    /// Finds the last instruction matching predicate up to the given instruction index.
    /// Returns null if not found.
    ///
    /// @param startIndex index to end search at, inclusive
    /// @param matcher predicate matching sought instruction
    /// @return index of the last instruction matching the predicate, up to the specified index
    public @Nullable LogicInstruction lastInstruction(int startIndex, Predicate<LogicInstruction> matcher) {
        int result = lastInstructionIndex(startIndex, matcher);
        return result < 0 ? null : program.get(result);
    }

    /// Finds the last instruction matching predicate. Returns null if not found.
    ///
    /// @param matcher predicate matching sought instruction
    /// @return the last instruction matching the predicate
    public @Nullable LogicInstruction lastInstruction(Predicate<LogicInstruction> matcher) {
        int result = lastInstructionIndex(matcher);
        return result < 0 ? null : program.get(result);
    }

    /// Finds a first non-label instruction following a label.
    /// Return null when not found
    public @Nullable LogicInstruction labeledInstruction(LogicLabel label) {
        int index = labeledInstructionIndex(label);
        return index < 0 ? null : instructionAt(index);
    }

    /// Return an independent list of instructions matching predicate.
    ///
    /// @param matcher predicate matching sought instructions
    /// @return list of all instructions matching the predicate.
    public List<LogicInstruction> instructions(Predicate<LogicInstruction> matcher) {
        return program.stream().filter(matcher).toList();
    }

    /// Return a list of indexes corresponding to instructions matching predicate.
    ///
    /// @param matcher predicate matching sought instructions
    /// @return list of all predicate-matching instructions indexes.
    public List<Integer> instructionIndexes(Predicate<LogicInstruction> matcher) {
        return IntStream.range(0, program.size())
                .filter(i -> matcher.test(program.get(i)))
                .boxed()
                .toList();
    }

    /// Return a number of instructions matching predicate.
    ///
    /// @param matcher predicate matching sought instructions
    /// @return number of matching instructions;
    public int instructionCount(Predicate<LogicInstruction> matcher) {
        return (int) program.stream().filter(matcher).count();
    }
    //</editor-fold>

    //<editor-fold desc="General code structure methods">
    /// Determines whether the given code block is contained, meaning it doesn't contain jumps outside.
    /// Outside jumps are generated as a result of break, continue or return statements.
    ///
    /// @param codeBlock code block to inspect
    /// @return true if the code block always exits though its last instruction.
    public boolean isContained(List<LogicInstruction> codeBlock) {
        Set<LogicLabel> localLabels = codeBlock.stream()
                .filter(LabeledInstruction.class::isInstance)
                .map(LabeledInstruction.class::cast)
                .map(LabeledInstruction::getLabel)
                .collect(Collectors.toSet());

        // No end/return instructions
        // All jump/goto instructions from this context must target only local labels
        return codeBlock.stream().noneMatch(ix -> ix instanceof ReturnRecInstruction
                    || ix instanceof ReturnInstruction
                    || ix instanceof EndInstruction && !ix.getAstContext().matches(AstSubcontextType.END))
                && codeBlock.stream()
                    .filter(ix -> ix instanceof JumpInstruction || ix instanceof ReturnInstruction || ix instanceof MultiLabelInstruction)
                    .allMatch(ix -> getPossibleTargetLabels(ix).allMatch(localLabels::contains));
    }

    /// Determines whether the given code block is contained, meaning it doesn't contain jumps outside.
    /// Outside jumps are generated as a result of break, continue or return statements.
    ///
    /// @param logicList code block to inspect
    /// @return true if the code block always exits though its last instruction.
    public boolean isContained(LogicList logicList) {
        return isContained(logicList.instructions);
    }

    private Stream<LogicLabel> getPossibleTargetLabels(LogicInstruction instruction) {
        return switch (instruction) {
            case JumpInstruction ix         -> Stream.of(ix.getTarget());
            case MultiTargetInstruction ix  -> markedLabels(ix);
            default                         -> Stream.empty();
        };
    }

    private Stream<LogicLabel> markedLabels(LogicInstruction instruction) {
        return instructionStream()
                .filter(in -> in instanceof MultiLabelInstruction gl && gl.matches(instruction))
                .map(MultiLabelInstruction.class::cast)
                .map(MultiLabelInstruction::getLabel);
    }
    //</editor-fold>

    //<editor-fold desc="Program modification">
    private void addStaleVariable(LogicVariable variable) {
        staleVariables.add(variable);
    }

    private void instructionAdded(LogicInstruction instruction) {
        if (instruction instanceof LabelInstruction label) {
            addLabelInstruction(label);
        }
        addReferences(instruction);

        instruction.outputArgumentsStream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .forEachOrdered(this::addStaleVariable);
    }

    private void instructionRemoved(LogicInstruction instruction) {
        variableStates.remove(instruction);
        if (instruction instanceof LabelInstruction label) {
            removeLabelInstruction(label);
        }
        removeReferences(instruction);

        instruction.outputArgumentsStream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .forEachOrdered(this::addStaleVariable);
    }

    /// Inserts a new instruction at the given index. The instruction must be assigned an AST context suitable for its
    /// position in the program. An instruction must not be placed into the program twice; when an instruction
    /// truly needs to be duplicated, an independent copy with proper AST context needs to be created.
    ///
    /// @param index where to place the instruction
    /// @param instruction instruction to add
    /// @throws MindcodeInternalError when the new instruction is already present elsewhere in the program
    public void insertInstruction(int index, LogicInstruction instruction) {
        for (LogicInstruction logicInstruction : program) {
            if (logicInstruction == instruction) {
                throw new MindcodeInternalError("Trying to insert the same instruction twice.\n" + instruction);
            }
        }

        insertInstructionUnchecked(index, instruction);
    }

    /// Inserts a new instruction at the given index. The instruction must be assigned an AST context suitable for its
    /// position in the program. An instruction must not be placed into the program twice; when an instruction
    /// truly needs to be duplicated, an independent copy with proper AST context needs to be created.
    ///
    /// @param index where to place the instruction
    /// @param instruction instruction to add
    /// @throws MindcodeInternalError when the new instruction is already present elsewhere in the program
    public void insertInstructionUnchecked(int index, LogicInstruction instruction) {
        iterators.forEach(iterator -> iterator.instructionAdded(index));
        program.add(index, instruction);
        instructionAdded(instruction);
        updated = true;
        insertions += instruction.getRealSize(null);
    }

    /// Inserts all instructions in the list to the program, starting at given index.
    /// See [#insertInstruction(int,LogicInstruction)].
    ///
    /// @param index where to place the instructions
    /// @param instructions instructions to add
    /// @throws MindcodeInternalError when any of the new instructions is already present elsewhere in the program
    public void insertInstructions(int index, LogicList instructions) {
        for (LogicInstruction instruction : instructions) {
            insertInstruction(index++, instruction);
        }
    }

    /// Replaces an instruction at the given index. The replacement instruction should either reuse the AST context
    /// of the original instruction at this position or use a new one specifically created for the replacement.
    ///
    /// Replacing an instruction with the same instruction isn't supported and causes an OptimizationException
    /// to be thrown.
    ///
    /// @param index index of an instruction to be replaced
    /// @param replacement new instruction for given index
    /// @return the new instruction
    /// @throws MindcodeInternalError when trying to replace an instruction with itself, or when the replaced
    /// instruction is already present elsewhere in the program
    public LogicInstruction replaceInstruction(int index, LogicInstruction replacement) {
        for (LogicInstruction logicInstruction : program) {
            if (logicInstruction == replacement) {
                throw new MindcodeInternalError("Trying to insert the same instruction twice.\n" + replacement);
            }
        }
        LogicInstruction original = program.set(index, replacement);
        instructionRemoved(original);
        instructionAdded(replacement);
        updated = true;
        int difference = original.getRealSize(null) - replacement.getRealSize(null);
        if (difference == 0) {
            modifications++;
        } else if (difference > 0) {
            deletions += difference;
        } else {
            insertions -= difference;   // Flip sign
        }

        return replacement;
    }

    /// Removes an instruction at the given index.
    ///
    /// @param index index of an instruction to be removed
    public void removeInstruction(int index) {
        iterators.forEach(iterator -> iterator.instructionRemoved(index));
        LogicInstruction instruction = program.remove(index);
        instructionRemoved(instruction);
        updated = true;
        deletions += instruction.getRealSize(null);
    }

    public void forceUpdate() {
        this.updated = true;
    }

    /// Inserts a new instruction before given, existing instruction. The new instruction must be assigned
    /// an AST context suitable for its position in the program. An instruction must not be placed into the
    /// program twice; when an instruction truly needs to be duplicated, an independent copy with proper
    /// AST context needs to be created.
    ///
    /// If the reference instruction isn't found in the program, an exception is thrown.
    ///
    /// @param anchor instruction before which to place the new instruction
    /// @param inserted instruction to add
    public void insertBefore(LogicInstruction anchor, LogicInstruction inserted) {
        insertInstruction(existingInstructionIndex(anchor), inserted);
    }

    /// Inserts a list of instructions before given, existing instruction.
    ///
    /// If the reference instruction isn't found in the program, an exception is thrown.
    ///
    /// @param anchor instruction before which to place the new instruction
    /// @param instructions instructions to add
    public void insertBefore(LogicInstruction anchor, LogicList instructions) {
        insertInstructions(existingInstructionIndex(anchor), instructions);
    }

    /// Inserts a new instruction after given, existing instruction. The new instruction must be assigned
    /// an AST context suitable for its position in the program. An instruction must not be placed into the
    /// program twice; when an instruction truly needs to be duplicated, an independent copy with proper
    /// AST context needs to be created.
    ///
    /// If the reference instruction isn't found in the program, an exception is thrown.
    ///
    /// @param anchor instruction after which to place the new instruction
    /// @param inserted instruction to add
    public void insertAfter(LogicInstruction anchor, LogicInstruction inserted) {
        insertInstruction(existingInstructionIndex(anchor) + 1, inserted);
    }

    /// Replaces a given instruction with a new one. The new instruction should either reuse the AST context
    /// of the original instruction at this position or use a new one specifically created for the replacement.
    ///
    /// If the original instruction isn't found in the program, an exception is thrown.
    ///
    /// @param original index of an instruction to be replaced
    /// @param replacement new instruction to replace the old one
    /// @return the new instruction
    public LogicInstruction replaceInstruction(LogicInstruction original, LogicInstruction replacement) {
        return replaceInstruction(existingInstructionIndex(original), replacement);
    }

    public LogicInstruction replaceInstructionArguments(LogicInstruction instruction, List<LogicArgument> newArgs) {
        return replaceInstruction(instruction, instructionProcessor.replaceArgs(instruction, newArgs));
    }


    /// Removes an existing instruction from the program. If the instruction isn't found, an exception is thrown.
    ///
    /// @param instruction instruction to be removed
    public void removeInstruction(LogicInstruction instruction) {
        removeInstruction(existingInstructionIndex(instruction));
    }

    /// Removes an instruction immediately preceding the given instruction.
    /// If the given instruction isn't found, an exception is thrown.
    ///
    /// @param anchor the reference instruction
    public void removePrevious(LogicInstruction anchor) {
        removeInstruction(existingInstructionIndex(anchor) - 1);
    }

    /// Removes an instruction immediately following the given instruction.
    /// If the given instruction isn't found, an exception is thrown.
    ///
    /// @param anchor the reference instruction
    public void removeFollowing(LogicInstruction anchor) {
        removeInstruction(existingInstructionIndex(anchor) + 1);
    }

    /// Removes all instructions matching the given predicate.
    ///
    /// @param matcher predicate to match instructions to be removed
    public void removeMatchingInstructions(Predicate<LogicInstruction> matcher) {
        for (int index = 0; index < program.size(); index++) {
            if (matcher.test(program.get(index))) {
                removeInstruction(index);
                index--;
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="Program modification using contexts">

    /// Returns a label at the very beginning of the given AST context. If such a label doesn't exist, creates it.
    /// Note that the label must belong directly to the given context to be reused; labels belonging to
    /// child contexts aren't considered. Care needs to be taken to provide the correct context; for nodes modeled
    /// with subcontexts a subcontext should always be provided.
    public LogicLabel obtainContextLabel(AstContext astContext) {
        LogicInstruction instruction = Objects.requireNonNull(firstInstruction(astContext));
        if (instruction instanceof LabelInstruction label && label.getAstContext() == astContext) {
            return label.getLabel();
        } else {
            LogicLabel label = instructionProcessor.nextLabel();
            insertBefore(instruction, instructionProcessor.createLabel(astContext, label));
            return label;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Logic iterator">
    private final List<LogicIterator> iterators = new ArrayList<>();

    /// Creates a new LogicIterator positioned at the start of the program.
    ///
    /// @return a new LogicIterator instance
    public LogicIterator createIterator() {
        return createIteratorAtIndex(0, _ -> true);
    }

    /// Creates a new LogicIterator positioned at the given index.
    ///
    /// @param index initial position of the iterator
    /// @return a new LogicIterator instance
    public LogicIterator createIteratorAtIndex(int index) {
        LogicIterator iterator = new LogicIterator(index, _ -> true);
        iterators.add(iterator);
        return iterator;
    }

    /// Creates a new LogicIterator positioned at the given index.
    ///
    /// @param index initial position of the iterator
    /// @return a new LogicIterator instance
    public LogicIterator createIteratorAtIndex(int index, Predicate<LogicInstruction> matcher) {
        LogicIterator iterator = new LogicIterator(index, matcher);
        iterators.add(iterator);
        return iterator;
    }

    /// Creates a new LogicIterator positioned at the given instruction.
    ///
    /// @param instruction target instruction
    /// @return LogicIterator positioned at the given instruction
    public LogicIterator createIteratorAtInstruction(LogicInstruction instruction) {
        return createIteratorAtIndex(existingInstructionIndex(instruction), _ -> true);
    }

    /// Creates a new LogicIterator positioned at the beginning of the given context.
    ///
    /// @param context target context
    /// @return LogicIterator positioned at the beginning of the given context
    public LogicIterator createIteratorAtContext(AstContext context) {
        return createIteratorAtIndex(firstInstructionIndex(context), _ -> true);
    }

    /// Creates a new LogicIterator representing instructions within a given context.
    ///
    /// @param context target context
    /// @return LogicIterator positioned at the beginning of the given context
    public LogicIterator createIteratorForContext(AstContext context) {
        int index = firstInstructionIndex(context);
        return index < 0 ? new LogicIterator() : createIteratorAtIndex(index, ix -> ix.belongsTo(context));
    }

    /// This class is modeled after ListIterator. Provides read-only functions at the moment.
    /// All instances of ListIterator reflect changes to the underlying program (if possible).
    /// When ListIterator points at a certain instruction, it keeps pointing to it even though
    /// some instructions are removed or added at positions preceding that of the LogicIterator.
    /// This is true regardless of how the modification was done (e.g., even by modifications made though
    /// different LogicIterator instance, or by calling methods).
    public class LogicIterator implements ListIterator<LogicInstruction>, AutoCloseable {
        private final Predicate<LogicInstruction> matcher;
        private final boolean valid;
        private int cursor;
        private boolean closed = false;
        private int lastRet = -1;

        private LogicIterator() {
            this.cursor = 0;
            this.matcher = _ -> false;
            this.valid = false;
        }
        private LogicIterator(int cursor, Predicate<LogicInstruction> matcher) {
            this.cursor = cursor;
            this.matcher = matcher;
            this.valid = true;
            if (cursor < 0 || cursor > program.size() || failIndex(cursor)) {
                throw new NoSuchElementException();
            }
        }

        private boolean failIndex(int index) {
            return index >= 0 && index < program.size() && !matcher.test(program.get(index));
        }

        private LogicInstruction test(LogicInstruction instruction) {
            if (!matcher.test(instruction)) {
                throw new NoSuchElementException();
            }
            return instruction;
        }

        @Override
        public void close() {
            closed = true;
            closeIterator(this);
        }

        public void setNextIndex(int index) {
            checkClosed();
            if (cursor < 0 || cursor >= program.size() || failIndex(cursor)) {
                throw new NoSuchElementException();
            }
            cursor = index;
        }

        /// Creates an independent LogicIterator instance positioned at the same instruction as this instance.
        ///
        /// @return a new LogicIterator instance
        public LogicIterator copy() {
            return createIteratorAtIndex(cursor);
        }

        /// Returns the instruction at the given offset from the current position. Both positive and negative offsets
        /// are valid, offset 0 returns the instruction that would be returned by calling [#next()]. If the resulting
        /// instruction position lies outside the program range, null is returned and no exception is thrown.
        ///
        /// @param offset offset relative to the current position
        /// @return instruction at given offset relative to current position, or null
        public LogicInstruction peek(int offset) {
            checkClosed();
            return test(instructionAt(cursor + offset));
        }

        /// Returns the next non-empty instruction without advancing the cursor.
        /// @return the next non-empty instruction
        public @Nullable LogicInstruction peekValid() {
            checkClosed();
            for (int i = cursor; i < program.size(); i++) {
                LogicInstruction instruction = program.get(i);
                if (!matcher.test(instruction)) break;
                if (instruction.getOpcode() != Opcode.EMPTY) return instruction;
            }
            return null;
        }

        /// @return true if there's a next instruction.
        public boolean hasNext() {
            checkClosed();
            return cursor < program.size() && !failIndex(cursor);
        }

        /// @return the next instruction
        /// @throws NoSuchElementException when at the end of the program
        public LogicInstruction next() {
            checkClosed();
            int i = cursor;
            if (i >= program.size() || failIndex(i)) {
                throw new NoSuchElementException();
            }
            cursor = i + 1;
            return program.get(lastRet = i);
        }

        /// Returns the next instructions, skipping all empty instructions in the process
        /// @return the next non-empty instruction
        /// @throws NoSuchElementException when at the end of the program
        public LogicInstruction nextValid() {
            checkClosed();
            int i = cursor;

            while (i < program.size() && program.get(i).getOpcode() == Opcode.EMPTY) {
                i++;
            }
            if (i >= program.size() || failIndex(i)) {
                throw new NoSuchElementException();
            }
            cursor = i + 1;
            return program.get(lastRet = i);
        }

        /// @return the index of the next instruction
        @Override
        public int nextIndex() {
            checkClosed();
            return cursor;
        }

        public int currentIndex() {
            return lastRet;
        }

        /// @return true if there's a previous instruction.
        @Override
        public boolean hasPrevious() {
            checkClosed();
            return cursor > 0 && !failIndex(cursor - 1);
        }

        /// @return the previous instruction
        /// @throws NoSuchElementException when at the start of the program
        public LogicInstruction previous() {
            checkClosed();
            int i = cursor - 1;
            if (i < 0 || failIndex(i)) {
                throw new NoSuchElementException();
            }
            cursor = i;
            return program.get(lastRet = i);
        }

        /// @return the index of the previous instruction
        public int previousIndex() {
            checkClosed();
            return cursor - 1;
        }

        /// Removes the last returned instruction. If no instruction was returned, or it was already removed,
        /// an exception is thrown.
        public void remove() {
            checkClosed();
            if (lastRet < 0) {
                throw new IllegalStateException();
            }
            removeInstruction(lastRet);
            // Cursor will be updated in instructionRemoved
            lastRet = -1;
        }

        /// Replaces the last returned instruction with the given instruction. If no instruction was returned,
        /// or it was removed in the meantime, an exception is thrown.
        ///
        /// @param replacement instruction with which to replace the last returned instruction
        @Override
        public void set(LogicInstruction replacement) {
            checkClosed();
            if (lastRet < 0) {
                throw new IllegalStateException();
            }
            replaceInstruction(lastRet, replacement);
        }

        /// Sets the last returned instruction to empty. If no instruction was returned, or it was already removed,
        /// an exception is thrown.
        public void setEmpty() {
            checkClosed();
            if (lastRet < 0) {
                throw new IllegalStateException();
            }
            replaceInstruction(lastRet, instructionProcessor.createEmpty(program.get(lastRet).getAstContext()));
        }

        /// Inserts the specified instruction into the list.
        /// The instruction is inserted immediately before the element that
        /// would be returned by [#next], if any, and after the element
        /// that would be returned by [#previous], if any.
        /// The new element is inserted before the implicit
        /// cursor: a later call to `next` would be unaffected, and
        /// a later call to `previous` would return the new element.
        ///
        /// @param instruction the instruction to insert
        @Override
        public void add(LogicInstruction instruction) {
            checkClosed();
            if (!valid) {
                throw new IllegalStateException();
            }

            int index = cursor;
            insertInstruction(index, instruction);
            // Cursor will be updated in instructionAdded
            lastRet = -1;
        }

        /// Provides a stream of instructions between this and `upTo` (inclusive at this, exclusive at `upTo`)
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

    private void closeIterator(LogicIterator iterator) {
        if (iterator.valid && !iterators.remove(iterator)) {
            throw new IllegalStateException("Trying to close unknown iterator.");
        }
    }
    //</editor-fold>

    //<editor-fold desc="Finding contexts">
    public boolean hasSubcontexts(AstContext context, AstSubcontextType... types) {
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

    private <T> void forEachContext(AstContext astContext, Predicate<AstContext> matcher, Function<AstContext, @Nullable T> action,
            List<T> result) {
        if (matcher.test(astContext)) {
            T applied = action.apply(astContext);
            if (applied != null) {
                result.add(applied);
            }
        }
        astContext.children().forEach(c -> forEachContext(c, matcher, action, result));
    }


    private <T> List<T> forEachContext(AstContext astContext, Predicate<AstContext> matcher, Function<AstContext, @Nullable T> action) {
        List<T> result = new ArrayList<>();
        forEachContext(astContext, matcher, action, result);
        return List.copyOf(result);
    }

    public <T> List<T> forEachContext(Predicate<AstContext> matcher, Function<AstContext, @Nullable T> action) {
        return forEachContext(rootContext, matcher, action);
    }

    public <T> List<T> forEachContext(AstContextType contextType, AstSubcontextType subcontextType,
            Function<AstContext, @Nullable T> action) {
        return forEachContext(rootContext, c -> c.matches(contextType, subcontextType), action);
    }

    public List<AstContext> contexts(AstContext astContext, Predicate<AstContext> matcher) {
        List<AstContext> contexts = new ArrayList<>();
        forEachContext(astContext, matcher, contexts::add);
        return List.copyOf(contexts);
    }

    public List<AstContext> contexts(Predicate<AstContext> matcher) {
        List<AstContext> contexts = new ArrayList<>();
        forEachContext(rootContext, matcher, contexts::add);
        return List.copyOf(contexts);
    }

    public @Nullable AstContext context(Predicate<AstContext> matcher) {
        List<AstContext> contexts = contexts(matcher);
        return switch (contexts.size()) {
            case 0 -> null;
            case 1 -> contexts.getFirst();
            default -> throw new MindcodeInternalError("More than one context found.");
        };
    }

    public AstContext existingContext(Predicate<AstContext> matcher) {
        List<AstContext> contexts = contexts(matcher);
        if (contexts.size() == 1) {
            return contexts.getFirst();
        } else {
            throw new MindcodeInternalError("More than one context found.");
        }
    }

    /// Creates a list of AST contexts representing inline functions.
    ///
    /// @return list of inline function node contexts
    public List<AstContext> getInlineFunctions() {
        return contexts(c -> c.subcontextType() == AstSubcontextType.INLINE_CALL);
    }

    public List<AstContext> getOutOfLineFunctions() {
        return contexts(c -> c.contextType() == AstContextType.FUNCTION_DEF);
    }
    //</editor-fold>


    //<editor-fold desc="Finding instructions by context">
    public LogicList contextInstructions(@Nullable AstContext astContext) {
        return astContext == null ? EMPTY :
                new LogicList(astContext,
                        List.copyOf(instructionStream().filter(ix -> ix.belongsTo(astContext)).toList()),
                        Map.of());
    }

    public Stream<LogicInstruction> contextStream(@Nullable AstContext astContext) {
        return astContext == null ? Stream.empty() : instructionStream().filter(ix -> ix.belongsTo(astContext));
    }

    public @Nullable LogicInstruction firstInstruction(@Nullable AstContext astContext) {
        return firstInstruction(ix -> ix.belongsTo(astContext));
    }

    public int firstInstructionIndex(AstContext astContext) {
        return firstInstructionIndex(ix -> ix.belongsTo(astContext));
    }

    public @Nullable LogicInstruction lastInstruction(AstContext astContext) {
        return lastInstruction(ix -> ix.belongsTo(astContext));
    }

    public int lastInstructionIndex(AstContext astContext) {
        return lastInstructionIndex(ix -> ix.belongsTo(astContext));
    }

    public LogicInstruction instructionBefore(AstContext astContext) {
        return instructionAt(firstInstructionIndex(ix -> ix.belongsTo(astContext)) - 1);
    }

    public @Nullable LogicInstruction instructionAfter(AstContext astContext) {
        int index = lastInstructionIndex(ix -> ix.belongsTo(astContext)) + 1;
        while (index < program.size()
               && (instructionAt(index) instanceof LabelInstruction ix && !isActive(ix.getLabel())
               || instructionAt(index) instanceof EmptyInstruction)) {
            index++;
        }
        return index < program.size() ? instructionAt(index) : null;
    }
    //</editor-fold>

    //<editor-fold desc="Logic list">
    public LogicList buildLogicList(AstContext context, List<LogicInstruction> instructions) {
        return new LogicList(context, instructions, Map.of());
    }

    public LogicList createLogicList(AstContext context) {
        return new LogicList(context, List.of(), Map.of());
    }

    private final LogicList EMPTY = new LogicList(null, java.util.List.of(), Map.of());

    /// Class for accessing context instructions in an organized manner.
    /// Is always created from a specific AST context.
    public class LogicList implements Iterable<LogicInstruction>, ContextfulInstructionCreator, ContextlessInstructionCreator {
        private final @Nullable AstContext astContext;
        private final ArrayList<LogicInstruction> instructions;
        private final Map<LogicLabel, LogicLabel> labelMap;

        private LogicList(@Nullable AstContext astContext, List<LogicInstruction> instructions,
                Map<LogicLabel, LogicLabel> labelMap) {
            this.astContext = astContext;
            this.instructions = new ArrayList<>(instructions);
            this.labelMap = labelMap;
        }

        @Override
        public LogicList withSideEffects(SideEffects sideEffects) {
            instructionProcessor.withSideEffects(sideEffects);
            return this;
        }

        public Map<LogicLabel, LogicLabel> getLabelMap() {
            return labelMap;
        }

        @Override
        public InstructionProcessor getProcessor() {
            return instructionProcessor;
        }

        @Override
        public LogicInstruction createInstruction(Opcode opcode, List<LogicArgument> arguments) {
            return createInstruction(Objects.requireNonNull(astContext), opcode, arguments);
        }

        @Override
        public LogicInstruction createInstruction(AstContext astContext, Opcode opcode, List<LogicArgument> arguments) {
            LogicInstruction instruction = instructionProcessor.createInstruction(astContext, opcode, arguments);
            instructions.add(instruction);
            return instruction;
        }

        public void addToContext(LogicInstruction instruction) {
            instructions.add(instruction.withContext(Objects.requireNonNull(astContext)));
        }

        public void addToContext(List<? extends LogicInstruction> instructions) {
            AstContext context = Objects.requireNonNull(astContext);
            instructions.forEach(ix -> this.instructions.add(ix.withContext(context)));
        }

        public void addKeepingContext(LogicInstruction instruction) {
            instructions.add(instruction);
        }

        public void addKeepingContext(int index, LogicInstruction instruction) {
            instructions.add(index, instruction);
        }

        public void replaceInContext(int index, LogicInstruction instruction) {
            instructions.set(index, instruction.withContext(instructions.get(index).getAstContext()));
        }

        public void replaceKeepingContext(int index, LogicInstruction instruction) {
            instructions.set(index, instruction);
        }

        public LogicInstruction remove(int index) {
            return instructions.remove(index);
        }

        public boolean removeIf(Predicate<LogicInstruction> filter) {
            return instructions.removeIf(filter);
        }

        public LogicInstruction removeFirst() {
            return instructions.removeFirst();
        }

        public LogicInstruction removeLast() {
            return instructions.removeLast();
        }

        public @Nullable AstContext getAstContext() {
            return astContext;
        }

        public AstContext getExistingAstContext() {
            return Objects.requireNonNull(astContext);
        }

        public int size() {
            return instructions.size();
        }

        public int realSize() {
            return InstructionCounter.localSize(stream());
        }

        public boolean isEmpty() {
            return instructions.isEmpty();
        }

        public Iterator<LogicInstruction> iterator() {
            return instructions.iterator();
        }

        public @Nullable LogicInstruction get(int index) {
            return index >= 0 && index < size() ? instructions.get(index) : null;
        }

        public LogicInstruction getExisting(int index) {
            return instructions.get(index);
        }

        public @Nullable LogicInstruction getFirst() {
            return get(0);
        }

        public @Nullable LogicInstruction getFirstReal() {
            for (int i = 0; i < size(); i++) {
                LogicInstruction ix = instructions.get(i);
                if (ix.isReal()) {
                    return ix;
                }
            }

            return null;
        }

        public @Nullable LogicInstruction getLast() {
            return get(size() - 1);
        }

        public @Nullable LogicInstruction getLastReal() {
            for (int i = size() - 1; i >= 0; i--) {
                LogicInstruction ix = instructions.get(i);
                if (ix.isReal()) {
                    return ix;
                }
            }

            return null;
        }

        public @Nullable LogicInstruction getFromEnd(int index) {
            return index >= 0 && index < size() ? instructions.get(size() - index - 1) : null;
        }

        public @Nullable LogicInstruction getRealFromEnd(int index) {
            for (int i = size() - 1; i >= 0; i--) {
                LogicInstruction ix = instructions.get(i);
                if (ix.isReal() && index-- <= 0) {
                    return ix;
                }
            }

            return null;
        }

        public int indexOf(LogicInstruction o) {
            return instructions.indexOf(o);
        }

        public int indexOf(Predicate<LogicInstruction> matcher) {
            for (int i = 0; i < size(); i++) {
                LogicInstruction ix = get(i);
                assert ix != null;
                if (matcher.test(ix)) {
                    return i;
                }
            }

            return -1;
        }

        public @Nullable LogicInstruction last(Predicate<LogicInstruction> matcher) {
            for (int i = size() - 1; i >= 0; i--) {
                LogicInstruction ix = get(i);
                assert ix != null;
                if (matcher.test(ix)) {
                    return ix;
                }
            }

            return null;
        }

        public int lastIndexOf(Predicate<LogicInstruction> matcher) {
            for (int i = size() - 1; i >= 0; i--) {
                LogicInstruction ix = get(i);
                assert ix != null;
                if (matcher.test(ix)) {
                    return i;
                }
            }

            return -1;
        }

        public ListIterator<LogicInstruction> listIterator() {
            return instructions.listIterator();
        }

        public LogicList subList(int fromIndex, int toIndex) {
            return new LogicList(astContext, instructions.subList(fromIndex, toIndex), labelMap);
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

        /// Duplicates this logic list including the context structure. This **must** be used when duplicating or moving
        /// existing instructions, as reusing existing contexts might lead to context discontinuity. Even when placing
        /// the copy right before or after the original - in this case, encompassing context will be continuous, but
        /// child contexts might not be.
        ///
        /// @return LogicList containing duplicated code.
        public LogicList duplicate(boolean remapLabels) {
            if (astContext == null) {
                throw new MindcodeInternalError("No astContext");
            }

            // Duplicate labels
            Map<LogicLabel, LogicLabel> labelMap = remapLabels ? duplicateLabels() : Map.of();

            Map<AstContext, AstContext> contextMap = astContext.createDeepCopy();
            return new LogicList(contextMap.get(astContext), stream()
                    .map(ix -> remapContextAndLabels(labelMap, contextMap, ix))
                    .toList(), labelMap);
        }

        public LogicList duplicateToContext(AstContext newContext, boolean functionInlining) {
            return transformToContext(newContext, functionInlining, ix -> ix);
        }

        public LogicList duplicateToContext(AstContext newContext, boolean remapLabels, boolean functionInlining) {
            return transformToContext(newContext, remapLabels, functionInlining, ix -> ix);
        }

        public @Nullable LogicList duplicateToContext(AstContext newContext, boolean functionInlining,
                Predicate<LogicInstruction> matcher) {
            return transformToContext(newContext, functionInlining, ix -> matcher.test(ix) ? ix : null);
        }

        public LogicList transformToContext(AstContext newContext, boolean functionInlining,
                Function<LogicInstruction, @Nullable LogicInstruction> transformer) {
            return transformToContext(newContext, true, functionInlining, transformer);
        }

        public LogicList transformToContext(AstContext newContext, boolean remapLabels,
                boolean functionInlining, Function<LogicInstruction, @Nullable LogicInstruction> transformer) {
            if (astContext == null) {
                throw new MindcodeInternalError("No astContext");
            }

            // Duplicate labels?
            Map<LogicLabel, LogicLabel> labelMap = remapLabels ? duplicateLabels() : Map.of();

            Map<AstContext, AstContext> contextMap = astContext.copyChildrenTo(newContext, functionInlining);
            return new LogicList(contextMap.get(astContext), stream()
                    .map(transformer)
                    .filter(Objects::nonNull)
                    .map(ix -> remapContextAndLabels(labelMap, contextMap, ix))
                    .toList(), labelMap);
        }

        private Map<LogicLabel, LogicLabel> duplicateLabels() {
            return Stream.concat(
                            stream()
                                    .filter(LabeledInstruction.class::isInstance)
                                    .map(LabeledInstruction.class::cast)
                                    .map(LabeledInstruction::getLabel),
                            stream()
                                    .filter(MultiLabelInstruction.class::isInstance)
                                    .map(MultiLabelInstruction.class::cast)
                                    .map(MultiLabelInstruction::getMarker)
                                    .distinct()
                    )
                    .collect(Collectors.toMap(l -> l, l -> instructionProcessor.nextLabel()));
        }

        private LogicInstruction remapContextAndLabels(Map<LogicLabel, LogicLabel> labelMap, Map<AstContext, AstContext> contextMap, LogicInstruction ix) {
            return instructionProcessor.replaceLabels(ix.withContext(contextMap.get(ix.getAstContext())), labelMap);
        }
    }
    //</editor-fold>


    public boolean isTraceActive() {
        return traceActive;
    }

    public void setTraceActive(boolean traceActive) {
        this.traceActive = traceActive;
    }

    public void indentInc() {
        traceFile.indentInc();
    }

    public void indentDec() {
        traceFile.indentDec();
    }

    void trace(Stream<String> text) {
        if (traceActive) traceFile.trace(text);
    }

    void trace(Supplier<String> text) {
        if (traceActive) traceFile.trace(text);
    }

    void trace(String text) {
        if (traceActive) traceFile.trace(text);
    }
}
