package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.evaluator.LogicReadable;
import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraph;
import info.teksol.mc.mindcode.compiler.optimization.DataFlowVariableStates.VariableStates;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.JumpInstruction;
import info.teksol.mc.mindcode.logic.instructions.LabelInstruction;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.OpInstruction;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/// Base class for optimizers. Contains methods to access and modify instructions of the program being processed
/// by the optimizer.
///
/// **Note:** The methods for accessing instructions always return `null` when the instruction doesn't exist,
/// regardless of the way the instruction is being accessed (via index, searching for properties, etc.) It is assumed
/// that accessed instructions are practically always tested using instanceof, which handles `null` values
/// gracefully. Methods that won't handle invalid indexes gracefully are documented as such.
///
/// Method that modify a program do not fail silently. For example, when trying to insert instruction before
/// instruction not in a program, an error occurs.
@NullMarked
abstract class BaseOptimizer extends AbstractOptimizer {
    protected int modifications = 0;
    protected int insertions = 0;
    protected int deletions = 0;
    protected int passes = 0;
    protected int iterations = 0;

    public BaseOptimizer(Optimization optimization, OptimizationContext optimizationContext) {
        super(optimization, optimizationContext);
    }

    protected boolean isTraceActive() {
        return OptimizationCoordinator.TRACE_ALL;
    }

    // Optimization logic

    /// Performs one iteration of the optimization. Return true to run another iteration, false when done.
    /// @return true to re-run the optimization
    protected abstract boolean optimizeProgram(OptimizationPhase phase);

    protected int currentPass = 0;
    protected int currentRun = 0;

    @Override
    public boolean optimize(OptimizationPhase phase, int pass) {
        if (optimizationContext.getProgram().isEmpty()) {
            return false;
        }

        currentPass = pass;
        boolean repeat;
        boolean modified = false;
        int iteration = 1;
        do {
            currentRun = optimizationContext.prepare();

            boolean wasTraceActive = optimizationContext.isTraceActive();
            optimizationContext.setTraceActive(isTraceActive());


            String runNumber = OptimizationCoordinator.TRACE ? "#" + currentRun + ": " : "";
            String title = pass == 0
                    ? "%s%s phase, %s, iteration %d".formatted(runNumber, phase.name, getName(), iteration)
                    : "%s%s phase, %s, pass %d, iteration %d".formatted(runNumber, phase.name, getName(), pass, iteration);

            trace("*** " + title + " ***");
            optimizationContext.debugPrintProgram(title, false);

            repeat = optimizeProgram(phase);
            optimizationContext.finish();

            modifications += optimizationContext.getModifications();
            insertions += optimizationContext.getInsertions();
            deletions += optimizationContext.getDeletions();
            modified |= optimizationContext.isUpdated();

            debugPrinter.registerIteration(this, title, optimizationContext.getProgram());
            iteration++;
            iterations++;

            optimizationContext.setTraceActive(wasTraceActive);
        } while (repeat);

        if (modified) {
            passes++;
        }

        return modified;
    }

    boolean wasUpdated() {
        return optimizationContext.isUpdated();
    }

    public CallGraph getCallGraph() {
        return optimizationContext.getCallGraph();
    }

    protected OptimizationResult applyOptimization(Supplier<OptimizationResult> optimization, String title) {
        currentRun = optimizationContext.prepare();
        OptimizationResult result = optimization.get();
        optimizationContext.finish();

        if (result == OptimizationResult.REALIZED) {
            modifications += optimizationContext.getModifications();
            insertions += optimizationContext.getInsertions();
            deletions += optimizationContext.getDeletions();
            debugPrinter.registerIteration(this, title, optimizationContext.getProgram());
        }
        return result;
    }

    public void generateFinalMessages() {
        String verb = insertions == deletions ? "modified" : insertions < deletions ? "eliminated" : "added";
        int count = insertions == deletions ? modifications : Math.abs(insertions - deletions);
        if (count > 0) {
            String visits = passes > 1 ? " (" + passes + " passes, " + iterations + " iterations)"
                    : iterations > 1 ? " (" + iterations + " iterations)" : "";

            emitMessage(MessageLevel.INFO, "%6d instructions %s by %s%s.",
                    count, verb, getName(), visits);
        }
    }

    public void indentInc() {
        optimizationContext.indentInc();
    }

    public void indentDec() {
        optimizationContext.indentDec();
    }

    public void indent(Runnable action) {
        indentInc();
        action.run();
        indentDec();
    }

    protected void trace(Stream<String> text) {
        optimizationContext.trace(text);
    }

    protected void trace(Supplier<String> text) {
        optimizationContext.trace(text);
    }

    protected void trace(String text) {
        optimizationContext.trace(text);
    }

    //<editor-fold desc="Label & variable tracking">
    public LabelInstruction getLabelInstruction(LogicLabel label) {
        return optimizationContext.getLabelInstruction(label);
    }

    public void putVariableStates(LogicInstruction instruction, VariableStates variableStates) {
        optimizationContext.putVariableStates(instruction, variableStates);
    }

    public @Nullable VariableStates getVariableStates(LogicInstruction instruction) {
        return optimizationContext.getVariableStates(instruction);
    }

    public @Nullable VariableStates getLoopVariables(AstContext conditionContext) {
        return optimizationContext.getLoopVariables(conditionContext);
    }

    public void clearVariableStates() {
        optimizationContext.clearVariableStates();
    }
    //</editor-fold>

    //<editor-fold desc="Expression evaluation">
    public OptimizerExpressionEvaluator getExpressionEvaluator() {
        return optimizationContext.getExpressionEvaluator();
    }

    public @Nullable LogicLiteral evaluateOpInstruction(OpInstruction op) {
        return optimizationContext.evaluateOpInstruction(op);
    }

    public @Nullable OpInstruction extendedEvaluate(VariableStates variableStates, OpInstruction op1) {
        return optimizationContext.extendedEvaluate(variableStates, op1);
    }

    public OpInstruction normalize(OpInstruction op) {
        return optimizationContext.normalize(op);
    }

    public OpInstruction normalizeMul(OpInstruction op, LogicVariable variable, LogicNumber number) {
        return optimizationContext.normalizeMul(op, variable, number);
    }

    public @Nullable LogicLiteral evaluate(SourcePosition sourcePosition, Operation operation, LogicReadable a, LogicReadable b) {
        return optimizationContext.evaluate(sourcePosition, operation, a, b);
    }

    public @Nullable LogicBoolean evaluateJumpInstruction(JumpInstruction jump) {
        return optimizationContext.evaluateJumpInstruction(jump);
    }

    public @Nullable LogicBoolean evaluateLoopConditionJump(JumpInstruction jump, AstContext loopContext) {
        return optimizationContext.evaluateLoopConditionJump(jump, loopContext);
    }
    //</editor-fold>

    //<editor-fold desc="Finding instructions by position">

    /// Return the instruction at a given position in the program.
    ///
    /// @param index index of the instruction
    /// @return the instruction at given index
    protected LogicInstruction instructionAt(int index) {
        return optimizationContext.instructionAt(index);
    }

    /// Returns the instruction preceding the given instruction. If such an instruction doesn't exist
    /// (because the reference instruction is the first one), returns null. If the reference instruction
    /// isn't found in the program, an exception is thrown.
    ///
    /// @param instruction instruction to find in the program
    /// @return instruction before the reference instruction
    protected @Nullable LogicInstruction instructionBefore(LogicInstruction instruction) {
        return optimizationContext.instructionBefore(instruction);
    }

    /// Returns the instruction following the given instruction. If such an instruction doesn't exist
    /// (because the reference instruction is the last one), returns null. If the reference instruction
    /// isn't found in the program, an exception is thrown.
    ///
    /// @param instruction instruction to find in the program
    /// @return instruction after the reference instruction
    protected @Nullable LogicInstruction instructionAfter(LogicInstruction instruction) {
        return optimizationContext.instructionAfter(instruction);
    }

    /// Provides a sublist of the current program. Will fail when such a sublist cannot be created.
    /// The returned list won't reflect further changes to the program.
    ///
    /// @param fromIndex starting index
    /// @param toIndex ending index (exclusive)
    /// @return a List containing given instructions.
    protected List<LogicInstruction> instructionSubList(int fromIndex, int toIndex) {
        return optimizationContext.instructionSubList(fromIndex, toIndex);
    }

    /// Provides a sublist of the current program. Will fail when such a sublist cannot be created.
    /// The returned list won't reflect further changes to the program.
    ///
    /// @param fromInstruction first instruction in the list
    /// @param toInstruction last instruction in the list (inclusive)
    /// @return a List containing given instructions.
    protected List<LogicInstruction> instructionSubList(LogicInstruction fromInstruction, LogicInstruction toInstruction) {
        return optimizationContext.instructionSubList(fromInstruction, toInstruction);
    }

    /// Provides a stream of all instructions in the program.
    ///
    /// @return an instruction stream
    protected Stream<LogicInstruction> instructionStream() {
        return optimizationContext.instructionStream();
    }

    /// Locates an instruction based on instance identity and returns its index.
    /// Returns -1 when such instruction doesn't exist.
    ///
    /// @param instruction instruction to locate
    /// @return index of the instruction, or -1 if the instruction isn't found.
    protected int instructionIndex(LogicInstruction instruction) {
        return optimizationContext.instructionIndex(instruction);
    }

    /// Returns the index of a given instruction. When the instruction isn't found, an exception is thrown.
    protected int existingInstructionIndex(LogicInstruction instruction) {
        return optimizationContext.existingInstructionIndex(instruction);
    }
    //</editor-fold>

    //<editor-fold desc="Finding instructions by properties">

    /// Starting at the given index, finds the first instruction matching predicate.
    /// Returns the index or -1 if not found.
    ///
    /// @param startIndex index to start search from, inclusive
    /// @param matcher predicate matching sought instruction
    /// @return index of the first instruction matching the predicate
    protected int firstInstructionIndex(int startIndex, Predicate<LogicInstruction> matcher) {
        return optimizationContext.firstInstructionIndex(startIndex, matcher);
    }

    /// Finds the first instruction matching predicate in the entire program.
    /// Returns the index or -1 if not found.
    ///
    /// @param matcher predicate matching sought instruction
    /// @return index of the first instruction matching the predicate
    protected int firstInstructionIndex(Predicate<LogicInstruction> matcher) {
        return optimizationContext.firstInstructionIndex(matcher);
    }

    /// Finds the last instruction matching predicate up to the given instruction index.
    /// Returns the index or -1 if not found.
    ///
    /// @param startIndex index to end search at, inclusive
    /// @param matcher predicate matching sought instruction
    /// @return index of the last instruction matching the predicate, up to the specified index
    protected int lastInstructionIndex(int startIndex, Predicate<LogicInstruction> matcher) {
        return optimizationContext.lastInstructionIndex(startIndex, matcher);
    }

    /// Finds the last instruction matching predicate in the entire program.
    /// Returns the index or -1 if not found.
    ///
    /// @param matcher predicate matching sought instruction
    /// @return index of the last instruction matching the predicate
    protected int lastInstructionIndex(Predicate<LogicInstruction> matcher) {
        return optimizationContext.lastInstructionIndex(matcher);
    }

    /// Finds a first non-label instruction following a label
    /// Returns the index or -1 if not found.
    /// If the label doesn't exist in the program, an exception is thrown.
    ///
    /// @param label target label
    /// @return first non-label instruction following the label
    protected int labeledInstructionIndex(LogicLabel label) {
        return optimizationContext.labeledInstructionIndex(label);
    }

    /// Starting at given index, find the first instruction matching the predicate. Return null if not found.
    ///
    /// @param startIndex index to start search from, inclusive
    /// @param matcher predicate matching sought instruction
    /// @return the first instruction matching the predicate
    protected @Nullable LogicInstruction firstInstruction(int startIndex, Predicate<LogicInstruction> matcher) {
        return optimizationContext.firstInstruction(startIndex, matcher);
    }

    /// Finds the first instruction matching predicate. Returns null if not found.
    ///
    /// @param matcher predicate matching sought instruction
    /// @return the first instruction in the entire program matching the predicate
    protected @Nullable LogicInstruction firstInstruction(Predicate<LogicInstruction> matcher) {
        return optimizationContext.firstInstruction(matcher);
    }

    /// Finds the last instruction matching predicate up to the given instruction index.
    /// Returns null if not found.
    ///
    /// @param startIndex index to end search at, inclusive
    /// @param matcher predicate matching sought instruction
    /// @return index of the last instruction matching the predicate, up to the specified index
    protected @Nullable LogicInstruction lastInstruction(int startIndex, Predicate<LogicInstruction> matcher) {
        return optimizationContext.lastInstruction(startIndex, matcher);
    }

    /// Finds the last instruction matching predicate. Returns null if not found.
    ///
    /// @param matcher predicate matching sought instruction
    /// @return the last instruction matching the predicate
    protected @Nullable LogicInstruction lastInstruction(Predicate<LogicInstruction> matcher) {
        return optimizationContext.lastInstruction(matcher);
    }

    /// Finds a first non-label instruction following a label.
    /// Return null when not found
    protected @Nullable LogicInstruction labeledInstruction(LogicLabel label) {
        return optimizationContext.labeledInstruction(label);
    }

    /// Return an independent list of instructions matching predicate.
    ///
    /// @param matcher predicate matching sought instructions
    /// @return list of all instructions matching the predicate.
    protected List<LogicInstruction> instructions(Predicate<LogicInstruction> matcher) {
        return optimizationContext.instructions(matcher);
    }

    /// Return a list of indexes corresponding to instructions matching predicate.
    ///
    /// @param matcher predicate matching sought instructions
    /// @return list of indexes of the predicate matching instructions.
    protected List<Integer> instructionIndexes(Predicate<LogicInstruction> matcher) {
        return optimizationContext.instructionIndexes(matcher);
    }

    /// Return a number of instructions matching predicate.
    ///
    /// @param matcher predicate matching sought instructions
    /// @return number of matching instructions;
    protected int instructionCount(Predicate<LogicInstruction> matcher) {
        return optimizationContext.instructionCount(matcher);
    }
    //</editor-fold>

    //<editor-fold desc="General code structure methods">

    /// Determines whether the given code block is contained, meaning it doesn't contain jumps outside.
    /// Outside jumps are generated as a result of break, continue or return statements.
    ///
    /// @param codeBlock code block to inspect
    /// @return true if the code block always exits though its last instruction.
    protected boolean isContained(List<LogicInstruction> codeBlock) {
        return optimizationContext.isContained(codeBlock);
    }

    /// Determines whether the given code block is contained, meaning it doesn't contain jumps outside.
    /// Outside jumps are generated as a result of break, continue or return statements.
    ///
    /// @param logicList code block to inspect
    /// @return true if the code block always exits though its last instruction.
    protected boolean isContained(LogicList logicList) {
        return optimizationContext.isContained(logicList);
    }
    //</editor-fold>

    //<editor-fold desc="Program modification">

    /// Inserts a new instruction at the given index. The instruction must be assigned an AST context suitable for its
    /// position in the program. An instruction must not be placed into the program twice; when an instruction
    /// truly needs to be duplicated, an independent copy with proper AST context needs to be created.
    ///
    /// @param index where to place the instruction
    /// @param instruction instruction to add
    /// @throws MindcodeInternalError when the new instruction is already present elsewhere in the program
    protected void insertInstruction(int index, LogicInstruction instruction) {
        optimizationContext.insertInstruction(index, instruction);
    }

    /// Inserts all instructions in the list to the program, starting at the given index.
    /// See [#insertInstruction(int,LogicInstruction)].
    ///
    /// @param index where to place the instructions
    /// @param instructions instructions to add
    /// @throws MindcodeInternalError when any of the new instructions is already present elsewhere in the program
    protected void insertInstructions(int index, LogicList instructions) {
        optimizationContext.insertInstructions(index, instructions);
    }

    /// Replaces an instruction at a given index. The replaced instruction should either reuse the AST context
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
    protected LogicInstruction replaceInstruction(int index, LogicInstruction replacement) {
        return optimizationContext.replaceInstruction(index, replacement);
    }

    /// Removes an instruction at a given index.
    ///
    /// @param index index of an instruction to be removed
    protected void removeInstruction(int index) {
        optimizationContext.removeInstruction(index);
    }

    protected void invalidateInstruction(int index) {
        LogicInstruction instruction = instructionAt(index);
        if (instruction.getOpcode() != Opcode.EMPTY) {
            replaceInstruction(index, createEmpty(instruction.getAstContext()));
        }
    }

    protected void invalidateInstruction(LogicInstruction instruction) {
        replaceInstruction(instruction, createEmpty(instruction.getAstContext()));
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
        optimizationContext.insertBefore(anchor, inserted);
    }


    /// Inserts a list of instructions before given, existing instruction.
    ///
    /// If the reference instruction isn't found in the program, an exception is thrown.
    ///
    /// @param anchor instruction before which to place the new instruction
    /// @param instructions instructions to add
    protected void insertBefore(LogicInstruction anchor, LogicList instructions) {
        optimizationContext.insertBefore(anchor, instructions);
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
        optimizationContext.insertAfter(anchor, inserted);
    }

    /// Replaces a given instruction with a new one. The new instruction should either reuse the AST context
    /// of the original instruction at this position or use a new one specifically created for the replacement.
    ///
    /// If the original instruction isn't found in the program, an exception is thrown.
    ///
    /// @param original index of an instruction to be replaced
    /// @param replacement new instruction for given index
    /// @return the new instruction
    protected LogicInstruction replaceInstruction(LogicInstruction original, LogicInstruction replacement) {
        return optimizationContext.replaceInstruction(original, replacement);
    }

    protected void replaceInstructionArguments(LogicInstruction instruction, List<LogicArgument> newArgs) {
        optimizationContext.replaceInstructionArguments(instruction, newArgs);
    }


    /// Removes an existing instruction from the program. If the instruction isn't found, an exception is thrown.
    ///
    /// @param instruction instruction to be removed
    protected void removeInstruction(LogicInstruction instruction) {
        optimizationContext.removeInstruction(instruction);
    }

    /// Removes an instruction immediately preceding the given instruction.
    /// If the given instruction isn't found, an exception is thrown.
    ///
    /// @param anchor the reference instruction
    protected void removePrevious(LogicInstruction anchor) {
        optimizationContext.removePrevious(anchor);
    }

    /// Removes an instruction immediately following the given instruction.
    /// If the given instruction isn't found, an exception is thrown.
    ///
    /// @param anchor the reference instruction
    protected void removeFollowing(LogicInstruction anchor) {
        optimizationContext.removeFollowing(anchor);
    }

    /// Removes all instructions matching a given predicate.
    ///
    /// @param matcher predicate to match instructions to be removed
    protected void removeMatchingInstructions(Predicate<LogicInstruction> matcher) {
        optimizationContext.removeMatchingInstructions(matcher);
    }
    //</editor-fold>

    //<editor-fold desc="Program modification using contexts">

    /// Returns a label at the very beginning of the given AST context. If such a label doesn't exist, creates it.
    /// Note that the label must belong directly to the given context to be reused; labels belonging to
    /// child contexts aren't considered. Care needs to be taken to provide the correct context; for nodes modeled
    /// with subcontexts a subcontext should always be provided.
    protected LogicLabel obtainContextLabel(AstContext astContext) {
        return optimizationContext.obtainContextLabel(astContext);
    }
    //</editor-fold>

    //<editor-fold desc="Logic iterator">

    /// Creates a new LogicIterator positioned at the start of the program.
    ///
    /// @return a new LogicIterator instance
    protected LogicIterator createIterator() {
        return optimizationContext.createIterator();
    }

    /// Creates a new LogicIterator positioned at the given index.
    ///
    /// @param index initial position of the iterator
    /// @return a new LogicIterator instance
    public LogicIterator createIteratorAtIndex(int index) {
        return optimizationContext.createIteratorAtIndex(index);
    }

    /// Creates a new LogicIterator positioned at the given instruction.
    ///
    /// @param instruction target instruction
    /// @return LogicIterator positioned at the given instruction
    protected LogicIterator createIteratorAtInstruction(LogicInstruction instruction) {
        return optimizationContext.createIteratorAtInstruction(instruction);
    }

    /// Creates a new LogicIterator positioned at the beginning of the given context.
    ///
    /// @param context target context
    /// @return LogicIterator positioned at the beginning of the given context
    protected LogicIterator createIteratorAtContext(AstContext context) {
        return optimizationContext.createIteratorAtContext(context);
    }
    //</editor-fold>

    //<editor-fold desc="Finding contexts">
    protected AstContext getRootContext() {
        return optimizationContext.getRootContext();
    }

    protected boolean hasSubcontexts(AstContext context, AstSubcontextType... types) {
        return optimizationContext.hasSubcontexts(context, types);
    }

    protected Function<AstContext, @Nullable Void> returningNull(Consumer<AstContext> action) {
        return context -> {
            action.accept(context);
            return null;
        };
    }

    protected <T> List<T> forEachContext(Predicate<AstContext> matcher, Function<AstContext, @Nullable T> action) {
        return optimizationContext.forEachContext(matcher, action);
    }

    protected <T> List<T> forEachContext(AstContextType contextType, AstSubcontextType subcontextType,
            Function<AstContext, @Nullable T> action) {
        return optimizationContext.forEachContext(contextType, subcontextType, action);
    }

    protected List<AstContext> contexts(Predicate<AstContext> matcher) {
        return optimizationContext.contexts(matcher);
    }

    protected @Nullable AstContext context(Predicate<AstContext> matcher) {
        return optimizationContext.context(matcher);
    }

    protected AstContext existingContext(Predicate<AstContext> matcher) {
        return optimizationContext.existingContext(matcher);
    }

    /// Creates a list of AST contexts representing inline functions.
    ///
    /// @return list of inline function node contexts
    protected List<AstContext> getInlineFunctions() {
        return optimizationContext.getInlineFunctions();
    }

    protected List<AstContext> getOutOfLineFunctions() {
        return optimizationContext.getOutOfLineFunctions();
    }
    //</editor-fold>


    //<editor-fold desc="Finding instructions by context">
    protected LogicList contextInstructions(@Nullable AstContext astContext) {
        return optimizationContext.contextInstructions(astContext);
    }

    protected Stream<LogicInstruction> contextStream(@Nullable AstContext astContext) {
        return optimizationContext.contextStream(astContext);
    }

    protected @Nullable LogicInstruction firstInstruction(AstContext astContext) {
        return optimizationContext.firstInstruction(astContext);
    }

    protected int firstInstructionIndex(AstContext astContext) {
        return optimizationContext.firstInstructionIndex(astContext);
    }

    protected @Nullable LogicInstruction lastInstruction(AstContext astContext) {
        return optimizationContext.lastInstruction(astContext);
    }

    protected int lastInstructionIndex(AstContext astContext) {
        return optimizationContext.lastInstructionIndex(astContext);
    }

    protected LogicInstruction instructionBefore(AstContext astContext) {
        return optimizationContext.instructionBefore(astContext);
    }

    protected @Nullable LogicInstruction instructionAfter(AstContext astContext) {
        return optimizationContext.instructionAfter(astContext);
    }
    //</editor-fold>

    protected LogicList buildLogicList(AstContext context, List<LogicInstruction> instructions) {
        return optimizationContext.buildLogicList(context, instructions);
    }

    protected LogicList createLogicList(AstContext context) {
        return optimizationContext.createLogicList(context);
    }
}
