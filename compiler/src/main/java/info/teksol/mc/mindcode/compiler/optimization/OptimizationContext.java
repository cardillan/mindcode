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
import info.teksol.mc.mindcode.compiler.optimization.DataFlowVariableStates.VariableStates;
import info.teksol.mc.mindcode.compiler.postprocess.LogicInstructionPrinter;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.util.CollectionUtils;
import info.teksol.mc.util.TraceFile;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
class OptimizationContext {
    private final CompilerProfile profile;
    private final MessageConsumer messageConsumer;
    private final OptimizerExpressionEvaluator expressionEvaluator;
    private final InstructionProcessor instructionProcessor;
    private final List<LogicInstruction> program;
    private final CallGraph callGraph;
    private final AstContext rootContext;
    private final boolean remoteLibrary;

    private @Nullable FunctionDataFlow functionDataFlow;
    private @Nullable BitSet unreachableInstructions;

    private final TraceFile traceFile;

    /// Maps instructions to input variable states (i.e. states before the instruction is executed)
    private final Map<LogicInstruction, VariableStates> variableStates = new IdentityHashMap<>();

    /// Holds evaluation of first loop condition variables for loop optimizer.
    private final Map<AstContext, VariableStates> loopVariables = new HashMap<>();

    private final Set<LogicVariable> staleVariables = new HashSet<>();

    /// List of variables that serve as a loop control variable in at least one unrolled loop.
    private final Set<LogicVariable> unrolledVariables = new HashSet<>();

    /// Set of variables which were detected as uninitialized.
    private final Set<LogicVariable> uninitializedVariables = new HashSet<>();

    /// Maps labels to their respective instructions. Built once and then updated after each modification
    /// of the program.
    private final Map<LogicLabel, LabelInstruction> labels = new HashMap<>();

    /// Tracks all instructions that target a label. Unused labels (labels whose list of instructions would be empty)
    /// are removed from the program after each iteration finishes.
    private final Map<LogicLabel, List<LogicInstruction>> labelReferences = new HashMap<>();

    private int modifications = 0;
    private int insertions = 0;
    private int deletions = 0;
    private boolean updated;

    OptimizationContext(TraceFile traceFile, MessageConsumer messageConsumer, CompilerProfile profile,
            InstructionProcessor instructionProcessor, List<LogicInstruction> program, CallGraph callGraph,
            AstContext rootAstContext, boolean remoteLibrary) {
        this.traceFile = traceFile;
        this.messageConsumer = messageConsumer;
        this.profile = profile;
        this.instructionProcessor = instructionProcessor;
        this.program = program;
        this.callGraph = callGraph;
        this.rootContext = rootAstContext;
        this.remoteLibrary = remoteLibrary;

        expressionEvaluator = new OptimizerExpressionEvaluator(instructionProcessor);

        rebuildLabelReferences();
        adjustWeights();
    }

    public void rebuildLabelReferences() {
        labels.clear();
        labelReferences.clear();

        instructionStream()
                .filter(LabelInstruction.class::isInstance)
                .map(LabelInstruction.class::cast)
                .forEach(this::addLabelInstruction);

        /* Create label references */
        instructionStream().forEachOrdered(this::addLabelReferences);
    }

    public MessageConsumer getMessageConsumer() {
        return messageConsumer;
    }

    public CompilerProfile getProfile() {
        return profile;
    }

    InstructionProcessor getInstructionProcessor() {
        return instructionProcessor;
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

    void debugPrintProgram(String title, boolean outputTitle) {
        if (OptimizationCoordinator.DEBUG_PRINT) {
            if (!OptimizationCoordinator.TRACE || outputTitle) {
                traceFile.outputProgram(title);
            }
            traceFile.outputProgram("Program before optimization:");
            BitSet unreachables = computeUnreachableInstructions();
            String text = LogicInstructionPrinter.toStringWithSourceCode(instructionProcessor, program,
                    index -> String.format(" [%c] cx#%-4d", unreachables.get(index) ? ' ' : 'x', program.get(index).getAstContext().id));
            traceFile.outputProgram(text);
        }
    }

    /// Prepares the instance for the next round of program modification/optimization.
    public void prepare() {
        if (updated) {
            unreachableInstructions = null;
            functionDataFlow = null;
            rebuildAstContextTree();
        }
        modifications = 0;
        insertions = 0;
        deletions = 0;
        updated = false;
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

    /// This method analyses the control flow of the program. It starts at the first instruction and visits
    /// every instruction reachable from there, either by advancing to the next instruction, or by a jump/goto/call.
    /// Bits of visited instruction are cleared and aren't inspected again. When all code paths have reached an
    /// end or an already visited instruction, the analysis stops and what is left are all unreachable instructions.
    ///
    /// @return a BitSet containing positions of unreachable instructions
    public BitSet getUnreachableInstructions() {
        if (unreachableInstructions == null) {
            if (profile.getOptimizationLevel(Optimization.UNREACHABLE_CODE_ELIMINATION) == OptimizationLevel.NONE) {
                unreachableInstructions = new BitSet(program.size());
            } else {
                unreachableInstructions = computeUnreachableInstructions();
            }
        }
        return unreachableInstructions;
    }

    private BitSet computeUnreachableInstructions() {
        BitSet unreachableInstructions = new BitSet(program.size());
        // Also serves as a data stop for reaching the end of instruction list naturally.
        unreachableInstructions.set(0, program.size());
        Queue<Integer> heads = new ArrayDeque<>();
        heads.offer(0);
        callGraph.getFunctions().stream()
                .filter(f -> f.isEntryPoint() && f.getLabel() != null)
                .map(MindcodeFunction::getLabel)
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
                    case END, RETURN, RETURNREC -> {
                        continue MainLoop;
                    }
                    case JUMP -> {
                        JumpInstruction jump = (JumpInstruction) ix;
                        heads.offer(findLabelIndex(jump.getTarget()));
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

                index++;
            }
        }

        return unreachableInstructions;
    }

    private int findLabelIndex(LogicLabel label) {
        int index = firstInstructionIndex(ix -> ix instanceof LabelInstruction l && l.getLabel().equals(label));
        if (index < 0) {
            throw new IllegalArgumentException("Illegal label: " + label);
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
    /// @return true if the propagation lead to some modifications
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
                        .filter(f -> !f.isInline() && !f.isMain())
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

    public void storeLoopVariables(AstContext conditionContext, VariableStates variableStates) {
        loopVariables.put(conditionContext, variableStates);
    }

    public @Nullable VariableStates getVariableStates(LogicInstruction instruction) {
        return variableStates.get(instruction);
    }

    public @Nullable VariableStates getLoopVariables(AstContext conditionContext) {
        return loopVariables.get(conditionContext);
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

    public void clearVariableStates() {
        variableStates.clear();
        loopVariables.clear();
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

    private void addLabelReferences(LogicInstruction instruction) {
        final Function<LogicLabel, List<LogicInstruction>> mappingFunction = v -> new ArrayList<>();
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
    }

    private void removeLabelReferences(LogicInstruction instruction) {
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
                    case NoOpInstruction noop -> it.remove();
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

    public boolean isUnrolledVariable(LogicVariable variable) {
        return unrolledVariables.contains(variable);
    }

    public void addUnrolledVariable(LogicVariable variable) {
        unrolledVariables.add(variable);
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

    public @Nullable LogicBoolean evaluateJumpInstruction(JumpInstruction jump) {
        return evaluateJumpInstruction(jump, variableStates.get(jump));
    }

    public @Nullable LogicBoolean evaluateLoopConditionJump(JumpInstruction jump, AstContext loopContext) {
        return evaluateJumpInstruction(jump, loopVariables.get(loopContext));
    }


    private @Nullable LogicBoolean evaluateJumpInstruction(JumpInstruction jump, VariableStates vs) {
        if (jump.isUnconditional()) {
            return LogicBoolean.TRUE;
        }

        // Avoid creating new instruction just for the evaluation
        LogicValue x = resolveValue(vs, jump.getX());
        LogicValue y = resolveValue(vs, jump.getY());

        if (x.isConstant() && y.isConstant()) {
            LogicLiteral result = expressionEvaluator.evaluate(jump.sourcePosition(), jump.getCondition().toOperation(), x, y);
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
    /// Return the instruction at given position in the program.
    ///
    /// @param index index of the instruction
    /// @return the instruction at given index
    LogicInstruction instructionAt(int index) {
        return program.get(index);
    }

    /// Returns the instruction preceding the given instruction. If such instruction doesn't exist
    /// (because the reference instruction is the first one), returns null. If the reference instruction
    /// isn't found the program, an exception is thrown.
    ///
    /// @param instruction instruction to find in the program
    /// @return instruction before the reference instruction
    @Nullable LogicInstruction instructionBefore(LogicInstruction instruction) {
        int index = existingInstructionIndex(instruction);
        return index == 0 ? null : instructionAt(index - 1);
    }

    /// Returns the instruction following the given instruction. If such instruction doesn't exist
    /// (because the reference instruction is the last one), returns null. If the reference instruction
    /// isn't found the program, an exception is thrown.
    ///
    /// @param instruction instruction to find in the program
    /// @return instruction after the reference instruction
    @Nullable LogicInstruction instructionAfter(LogicInstruction instruction) {
        int index = existingInstructionIndex(instruction) + 1;
        return index == program.size() ? null : instructionAt(index);
    }

    /// Provides a sublist of the current program. Will fail when such sublist cannot be created.
    /// Returned list won't reflect further changed to the program.
    ///
    /// @param fromIndex starting index
    /// @param toIndex ending index (exclusive)
    /// @return a List containing given instructions.
    List<LogicInstruction> instructionSubList(int fromIndex, int toIndex) {
        return List.copyOf(program.subList(fromIndex, toIndex));
    }

    /// Provides a sublist of the current program. Will fail when such sublist cannot be created.
    /// Returned list won't reflect further changed to the program.
    ///
    /// @param fromInstruction first instruction in the list
    /// @param toInstruction last instruction in the list (inclusive)
    /// @return a List containing given instructions.
    protected List<LogicInstruction> instructionSubList(LogicInstruction fromInstruction, LogicInstruction toInstruction) {
        return instructionSubList(instructionIndex(fromInstruction), instructionIndex(toInstruction) + 1);
    }

    /// Provides stream of all instructions in the program.
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

    /// Returns the index of given instruction. When the instruction isn't found, an exception is thrown.
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

    /// Starting at given index, finds the first instruction matching predicate.
    /// Returns the index or -1 if not found.
    ///
    /// @param startIndex index to start search from, inclusive
    /// @param matcher predicate matching sought instruction
    /// @return index of the first instruction matching the predicate
    protected int firstInstructionIndex(int startIndex, Predicate<LogicInstruction> matcher) {
        return CollectionUtils.indexOf(program, startIndex, matcher);
    }

    /// Finds the first instruction matching predicate in the entire program.
    /// Returns the index or -1 if not found.
    ///
    /// @param matcher predicate matching sought instruction
    /// @return index of the first instruction matching the predicate
    protected int firstInstructionIndex(Predicate<LogicInstruction> matcher) {
        return CollectionUtils.indexOf(program, 0, matcher);
    }

    /// Finds the last instruction matching predicate up to the given instruction index.
    /// Returns the index or -1 if not found.
    ///
    /// @param startIndex index to end search at, inclusive
    /// @param matcher predicate matching sought instruction
    /// @return index of the last instruction matching the predicate, up to the specified index
    protected int lastInstructionIndex(int startIndex, Predicate<LogicInstruction> matcher) {
        return CollectionUtils.lastIndexOf(program, startIndex, matcher);
    }

    /// Finds the last instruction matching predicate in the entire program.
    /// Returns the index or -1 if not found.
    ///
    /// @param matcher predicate matching sought instruction
    /// @return index of the last instruction matching the predicate
    protected int lastInstructionIndex(Predicate<LogicInstruction> matcher) {
        return CollectionUtils.lastIndexOf(program, program.size() - 1, matcher);
    }

    /// Finds a first non-label instruction following a label
    /// Returns the index or -1 if not found.
    /// If the label doesn't exist in the program, an exception is thrown.
    ///
    /// @param label target label
    /// @return first non-label instruction following the label
    protected int labeledInstructionIndex(LogicLabel label) {
        LabelInstruction labelInstruction = getLabelInstruction(label);
        int labelIndex = firstInstructionIndex(ix -> ix == labelInstruction);
        if (labelIndex < 0) {
            throw new MindcodeInternalError("Label not found in program.\nLabel: " + label);
        }
        return firstInstructionIndex(labelIndex + 1,
                ix -> !(ix instanceof LabeledInstruction) && !(ix instanceof NoOpInstruction));
    }

    /// Starting at given index, find first instruction matching predicate. Return null if not found.
    ///
    /// @param startIndex index to start search from, inclusive
    /// @param matcher predicate matching sought instruction
    /// @return the first instruction matching the predicate
    protected @Nullable LogicInstruction firstInstruction(int startIndex, Predicate<LogicInstruction> matcher) {
        int result = firstInstructionIndex(startIndex, matcher);
        return result < 0 ? null : program.get(result);
    }

    /// Finds the first instruction matching predicate. Returns null if not found.
    ///
    /// @param matcher predicate matching sought instruction
    /// @return the first instruction in the entire program matching the predicate
    protected @Nullable LogicInstruction firstInstruction(Predicate<LogicInstruction> matcher) {
        int result = firstInstructionIndex(matcher);
        return result < 0 ? null : program.get(result);
    }

    /// Finds the last instruction matching predicate up to the given instruction index.
    /// Returns null if not found.
    ///
    /// @param startIndex index to end search at, inclusive
    /// @param matcher predicate matching sought instruction
    /// @return index of the last instruction matching the predicate, up to the specified index
    protected @Nullable LogicInstruction lastInstruction(int startIndex, Predicate<LogicInstruction> matcher) {
        int result = lastInstructionIndex(startIndex, matcher);
        return result < 0 ? null : program.get(result);
    }

    /// Finds the last instruction matching predicate. Returns null if not found.
    ///
    /// @param matcher predicate matching sought instruction
    /// @return the last instruction matching the predicate
    protected @Nullable LogicInstruction lastInstruction(Predicate<LogicInstruction> matcher) {
        int result = lastInstructionIndex(matcher);
        return result < 0 ? null : program.get(result);
    }

    /// Finds a first non-label instruction following a label.
    /// Return null when not found
    protected @Nullable LogicInstruction labeledInstruction(LogicLabel label) {
        int index = labeledInstructionIndex(label);
        return index < 0 ? null : instructionAt(index);
    }

    /// Return an independent list of instructions matching predicate.
    ///
    /// @param matcher predicate matching sought instructions
    /// @return list of all instructions matching the predicate.
    protected List<LogicInstruction> instructions(Predicate<LogicInstruction> matcher) {
        return program.stream().filter(matcher).toList();
    }
    /// Return a number of instructions matching predicate.
    ///
    /// @param matcher predicate matching sought instructions
    /// @return number of matching instructions;
    protected int instructionCount(Predicate<LogicInstruction> matcher) {
        return (int) program.stream().filter(matcher).count();
    }
    //</editor-fold>

    //<editor-fold desc="General code structure methods">
    /// Determines whether the given code block is contained, meaning it doesn't contain jumps outside.
    /// Outside jumps are generated as a result of break, continue or return statements.
    ///
    /// @param codeBlock code block to inspect
    /// @return true if the code block always exits though its last instruction.
    protected boolean isContained(List<LogicInstruction> codeBlock) {
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
    protected boolean isContained(LogicList logicList) {
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
    private void instructionAdded(LogicInstruction instruction) {
        if (instruction instanceof LabelInstruction label) {
            addLabelInstruction(label);
        }
        addLabelReferences(instruction);

        instruction.outputArgumentsStream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .forEachOrdered(staleVariables::add);
    }

    private void instructionRemoved(LogicInstruction instruction) {
        variableStates.remove(instruction);
        if (instruction instanceof LabelInstruction label) {
            removeLabelInstruction(label);
        }
        removeLabelReferences(instruction);

        instruction.outputArgumentsStream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .forEachOrdered(staleVariables::add);
    }

    /// Inserts a new instruction at given index. The instruction must be assigned an AST context suitable for its
    /// position in the program. An instruction must not be placed into the program twice; when an instruction
    /// truly needs to be duplicated, an independent copy with proper AST context needs to be created.
    ///
    /// @param index where to place the instruction
    /// @param instruction instruction to add
    /// @throws MindcodeInternalError when the new instruction is already present elsewhere in the program
    protected void insertInstruction(int index, LogicInstruction instruction) {
        for (LogicInstruction logicInstruction : program) {
            if (logicInstruction == instruction) {
                throw new MindcodeInternalError("Trying to insert the same instruction twice.\n" + instruction);
            }
        }

        insertInstructionUnchecked(index, instruction);
    }

    /// Inserts a new instruction at given index. The instruction must be assigned an AST context suitable for its
    /// position in the program. An instruction must not be placed into the program twice; when an instruction
    /// truly needs to be duplicated, an independent copy with proper AST context needs to be created.
    ///
    /// @param index where to place the instruction
    /// @param instruction instruction to add
    /// @throws MindcodeInternalError when the new instruction is already present elsewhere in the program
    protected void insertInstructionUnchecked(int index, LogicInstruction instruction) {
        iterators.forEach(iterator -> iterator.instructionAdded(index));
        program.add(index, instruction);
        instructionAdded(instruction);
        updated = true;
        insertions += instruction.getRealSize(null);
    }

    /// Inserts all instruction in the list to the program, starting at given index.
    /// See [#insertInstruction(int,LogicInstruction)].
    ///
    /// @param index where to place the instructions
    /// @param instructions instructions to add
    /// @throws MindcodeInternalError when any of the new instructions is already present elsewhere in the program
    protected void insertInstructions(int index, LogicList instructions) {
        for (LogicInstruction instruction : instructions) {
            insertInstruction(index++, instruction);
        }
    }

    /// Replaces an instruction at given index. The replacement instruction should either reuse the AST context
    /// of the original instruction at this position, or use a new one specifically created for the purpose
    /// of the replacement.
    ///
    /// Replacing an instruction with the same instruction isn't supported and causes an OptimizationException
    /// to be thrown.
    ///
    /// @param index index of an instruction to be replaced
    /// @param replacement new instruction for given index
    /// @return the new instruction
    /// @throws MindcodeInternalError when trying to replace an instruction with itself, or when the replaced
    /// instruction is already present elsewhere in the program
    protected LogicInstruction replaceInstruction(int index, LogicInstruction replacement) {
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

    /// Removes an instruction at given index.
    ///
    /// @param index index of an instruction to be removed
    protected void removeInstruction(int index) {
        iterators.forEach(iterator -> iterator.instructionRemoved(index));
        LogicInstruction instruction = program.remove(index);
        instructionRemoved(instruction);
        updated = true;
        deletions += instruction.getRealSize(null);
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
    protected void insertBefore(LogicInstruction anchor, LogicInstruction inserted) {
        insertInstruction(existingInstructionIndex(anchor), inserted);
    }

    /// Inserts a list of instructions before given, existing instruction.
    ///
    /// If the reference instruction isn't found in the program, an exception is thrown.
    ///
    /// @param anchor instruction before which to place the new instruction
    /// @param instructions instructions to add
    protected void insertBefore(LogicInstruction anchor, LogicList instructions) {
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
    protected void insertAfter(LogicInstruction anchor, LogicInstruction inserted) {
        insertInstruction(existingInstructionIndex(anchor) + 1, inserted);
    }

    /// Replaces a given instruction with a new one. The new instruction should either reuse the AST context
    /// of the original instruction at this position, or use a new one specifically created for the purpose
    /// of the replacement.
    ///
    /// If the original instruction isn't found in the program, an exception is thrown.
    ///
    /// @param original index of an instruction to be replaced
    /// @param replacement new instruction to replace the old one
    /// @return the new instruction
    protected LogicInstruction replaceInstruction(LogicInstruction original, LogicInstruction replacement) {
        return replaceInstruction(existingInstructionIndex(original), replacement);
    }

    protected LogicInstruction replaceInstructionArguments(LogicInstruction instruction, List<LogicArgument> newArgs) {
        return replaceInstruction(instruction, instructionProcessor.replaceArgs(instruction, newArgs));
    }


    /// Removes an existing instruction from the program. If the instruction isn't found, an exception is thrown.
    ///
    /// @param instruction instruction to be removed
    protected void removeInstruction(LogicInstruction instruction) {
        removeInstruction(existingInstructionIndex(instruction));
    }

    /// Removes an instruction immediately preceding given instruction.
    /// If the given instruction isn't found, an exception is thrown.
    ///
    /// @param anchor the reference instruction
    protected void removePrevious(LogicInstruction anchor) {
        removeInstruction(existingInstructionIndex(anchor) - 1);
    }

    /// Removes an instruction immediately following given instruction.
    /// If the given instruction isn't found, an exception is thrown.
    ///
    /// @param anchor the reference instruction
    protected void removeFollowing(LogicInstruction anchor) {
        removeInstruction(existingInstructionIndex(anchor) + 1);
    }

    /// Removes all instructions matching given predicate.
    ///
    /// @param matcher predicate to match instructions to be removed
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

    /// Returns a label at the very beginning of the given AST context. If such label doesn't exist, creates it.
    /// Note that the label must belong directly to the given context to be reused, labels belonging to
    /// child contexts aren't considered. Care needs to be taken to provide correct context; for nodes modelled
    /// with subcontexts a subcontext should be always provided.
    protected LogicLabel obtainContextLabel(AstContext astContext) {
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
    protected LogicIterator createIterator() {
        return createIteratorAtIndex(0);
    }

    /// Creates a new LogicIterator positioned at given index.
    ///
    /// @param index initial position of the iterator
    /// @return a new LogicIterator instance
    public LogicIterator createIteratorAtIndex(int index) {
        LogicIterator iterator = new LogicIterator(index);
        iterators.add(iterator);
        return iterator;
    }

    /// Creates a new LogicIterator positioned at given instruction.
    ///
    /// @param instruction target instruction
    /// @return LogicIterator positioned at given instruction
    protected LogicIterator createIteratorAtInstruction(LogicInstruction instruction) {
        return createIteratorAtIndex(existingInstructionIndex(instruction));
    }

    /// Creates a new LogicIterator positioned at the beginning of given context.
    ///
    /// @param context target context
    /// @return LogicIterator positioned at the beginning of given context
    protected LogicIterator createIteratorAtContext(AstContext context) {
        return createIteratorAtIndex(firstInstructionIndex(context));
    }

    /// This class is modelled after ListIterator. Provides read-only functions at the moment.
    /// All instances of ListIterator reflect changes to the underlying program (if possible).
    /// When ListIterator points at certain instruction, if keeps pointing to it even though
    /// some instructions are removed or added at positions preceding that of the LogicIterator,
    /// regardless of how the modification was done (i.e. even modifications made though
    /// different LogicIterator instance, or by calling methods).
    protected class LogicIterator implements ListIterator<LogicInstruction>, AutoCloseable {
        private int cursor;
        private boolean closed = false;
        private int lastRet = -1;

        private LogicIterator(int cursor) {
            this.cursor = cursor;
            if (cursor < 0 || cursor > program.size()) {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void close() {
            closed = true;
            closeIterator(this);
        }

        public void setNextIndex(int index) {
            checkClosed();
            if (cursor < 0 || cursor >= program.size()) {
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

        /// Returns the instruction at given offset from current position. Both positive and negative offsets are valid,
        /// offset 0 returns the instruction that would be returned by calling [#next()]. If the resulting
        /// instruction position lies outside the program range, null is returned and no exception is thrown.
        ///
        /// @param offset offset relative to current position
        /// @return instruction at given offset relative to current position, or null
        public LogicInstruction peek(int offset) {
            return instructionAt(cursor + offset);
        }

        /// @return true if there's a next instruction.
        public boolean hasNext() {
            checkClosed();
            return cursor < program.size();
        }

        /// @return the next instruction
        /// @throws NoSuchElementException when at the end of the program
        public LogicInstruction next() {
            checkClosed();
            int i = cursor;
            if (i >= program.size()) {
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
            return cursor != 0;
        }

        /// @return the previous instruction
        /// @throws NoSuchElementException when at the start of the program
        public LogicInstruction previous() {
            checkClosed();
            int i = cursor - 1;
            if (i < 0) {
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

        /// Replaces the last returned instruction with given instruction. If no instruction was returned,
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

        /// Inserts the specified instruction into the list.
        /// The instruction is inserted immediately before the element that
        /// would be returned by [#next], if any, and after the element
        /// that would be returned by [#previous], if any.
        /// The new element is inserted before the implicit
        /// cursor: a subsequent call to `next` would be unaffected, and a
        /// subsequent call to `previous` would return the new element.
        ///
        /// @param instruction the instruction to insert
        @Override
        public void add(LogicInstruction instruction) {
            checkClosed();
            int index = cursor;
            insertInstruction(index, instruction);
            // Cursor will be updated in instructionAdded
            lastRet = -1;
        }

        /// Provides a stream of instructions between this and upTo (inclusive at this, exclusive at end)
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

    protected <T> List<T> forEachContext(Predicate<AstContext> matcher, Function<AstContext, T> action) {
        return forEachContext(rootContext, matcher, action);
    }

    protected <T> List<T> forEachContext(AstContextType contextType, AstSubcontextType subcontextType,
            Function<AstContext, @Nullable T> action) {
        return forEachContext(rootContext, c -> c.matches(contextType, subcontextType), action);
    }

    protected List<AstContext> contexts(AstContext astContext, Predicate<AstContext> matcher) {
        List<AstContext> contexts = new ArrayList<>();
        forEachContext(astContext, matcher, contexts::add);
        return List.copyOf(contexts);
    }

    protected List<AstContext> contexts(Predicate<AstContext> matcher) {
        List<AstContext> contexts = new ArrayList<>();
        forEachContext(rootContext, matcher, contexts::add);
        return List.copyOf(contexts);
    }

    protected @Nullable AstContext context(Predicate<AstContext> matcher) {
        List<AstContext> contexts = contexts(matcher);
        return switch (contexts.size()) {
            case 0 -> null;
            case 1 -> contexts.getFirst();
            default -> throw new MindcodeInternalError("More than one context found.");
        };
    }

    protected AstContext existingContext(Predicate<AstContext> matcher) {
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
    protected List<AstContext> getInlineFunctions() {
        return contexts(c -> c.subcontextType() == AstSubcontextType.INLINE_CALL);
    }

    protected List<AstContext> getOutOfLineFunctions() {
        return contexts(c -> c.contextType() == AstContextType.FUNCTION);
    }
    //</editor-fold>


    //<editor-fold desc="Finding instructions by context">
    protected LogicList contextInstructions(@Nullable AstContext astContext) {
        return astContext == null ? EMPTY :
                new LogicList(astContext, List.copyOf(instructionStream().filter(ix -> ix.belongsTo(astContext)).toList()));
    }

    protected Stream<LogicInstruction> contextStream(@Nullable AstContext astContext) {
        return astContext == null ? Stream.empty() : instructionStream().filter(ix -> ix.belongsTo(astContext));
    }

    protected @Nullable LogicInstruction firstInstruction(@Nullable AstContext astContext) {
        return firstInstruction(ix -> ix.belongsTo(astContext));
    }

    protected int firstInstructionIndex(AstContext astContext) {
        return firstInstructionIndex(ix -> ix.belongsTo(astContext));
    }

    protected @Nullable LogicInstruction lastInstruction(AstContext astContext) {
        return lastInstruction(ix -> ix.belongsTo(astContext));
    }

    protected int lastInstructionIndex(AstContext astContext) {
        return lastInstructionIndex(ix -> ix.belongsTo(astContext));
    }

    protected LogicInstruction instructionBefore(AstContext astContext) {
        return instructionAt(firstInstructionIndex(ix -> ix.belongsTo(astContext)) - 1);
    }

    protected @Nullable LogicInstruction instructionAfter(AstContext astContext) {
        int index = lastInstructionIndex(ix -> ix.belongsTo(astContext)) + 1;
        while (index < program.size()
               && (instructionAt(index) instanceof LabelInstruction ix && !isActive(ix.getLabel())
               || instructionAt(index) instanceof NoOpInstruction)) {
            index++;
        }
        return index < program.size() ? instructionAt(index) : null;
    }
    //</editor-fold>

    //<editor-fold desc="Logic list">
    protected LogicList buildLogicList(AstContext context, List<LogicInstruction> instructions) {
        return new LogicList(context, instructions);
    }

    private final LogicList EMPTY = new LogicList(null, java.util.List.of());

    /// Class for accessing context instructions in an organized manner.
    /// Is always created from a specific AST context.
    ///
    /// TODO allow modifications to be done to the LogicList - a block of code could be created in LogicList
    ///      and then inserted into the program.
    protected class LogicList implements Iterable<LogicInstruction> {
        private final @Nullable AstContext astContext;
        private final List<LogicInstruction> instructions;

        private LogicList(@Nullable AstContext astContext, List<LogicInstruction> instructions) {
            this.astContext = astContext;
            this.instructions = instructions;
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

        public @NonNull Iterator<LogicInstruction> iterator() {
            return instructions.iterator();
        }

        public @Nullable LogicInstruction get(int index) {
            return index >= 0 && index < size() ? instructions.get(index) : null;
        }

        public @Nullable LogicInstruction getFirst() {
            return get(0);
        }

        public @Nullable LogicInstruction getLast() {
            return get(size() - 1);
        }

        public @Nullable LogicInstruction getFromEnd(int index) {
            return index >= 0 && index < size() ? instructions.get(size() - index - 1) : null;
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

        /// Duplicates this logic list including the context structure. This **must** be used when duplicating
        /// existing instructions, as reusing existing contexts might lead to context discontinuity (even when placing
        /// the copy right before or after the original - in this case, encompassing context will be continuous, but
        /// child contexts might not be).
        ///
        /// @return LogicList containing duplicated code.
        public LogicList duplicate() {
            if (astContext == null) {
                throw new MindcodeInternalError("No astContext");
            }

            // Duplicate labels
            Map<LogicLabel, LogicLabel> labelMap = duplicateLabels();

            Map<AstContext, AstContext> contextMap = astContext.createDeepCopy();
            return new LogicList(contextMap.get(astContext), stream()
                    .map(ix -> remapContextAndLabels(labelMap, contextMap, ix))
                    .toList());
        }

        public LogicList duplicateToContext(AstContext newContext) {
            return transformToContext(newContext, ix -> ix);
        }

        public @Nullable LogicList duplicateToContext(AstContext newContext, Predicate<LogicInstruction> matcher) {
            return transformToContext(newContext, ix -> matcher.test(ix) ? ix : null);
        }

        public LogicList transformToContext(AstContext newContext, Function<LogicInstruction, @Nullable LogicInstruction> transformer) {
            if (astContext == null) {
                throw new MindcodeInternalError("No astContext");
            }

            // Duplicate labels
            Map<LogicLabel, LogicLabel> labelMap = duplicateLabels();

            Map<AstContext, AstContext> contextMap = astContext.copyChildrenTo(newContext);
            return new LogicList(contextMap.get(astContext), stream()
                    .map(transformer)
                    .filter(Objects::nonNull)
                    .map(ix -> remapContextAndLabels(labelMap, contextMap, ix))
                    .toList());
        }

        private @NonNull Map<LogicLabel, LogicLabel> duplicateLabels() {
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

    public void indentInc() {
        traceFile.indentInc();
    }

    public void indentDec() {
        traceFile.indentDec();
    }

    void trace(Stream<String> text) {
        traceFile.trace(text);
    }

    void trace(Supplier<String> text) {
        traceFile.trace(text);
    }

    void trace(String text) {
        traceFile.trace(text);
    }
}
