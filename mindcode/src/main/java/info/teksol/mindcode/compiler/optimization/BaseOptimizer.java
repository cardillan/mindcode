package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.MessageLevel;
import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.mindcode.compiler.generator.CallGraph;
import info.teksol.mindcode.compiler.instructions.JumpInstruction;
import info.teksol.mindcode.compiler.instructions.LabelInstruction;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.OpInstruction;
import info.teksol.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mindcode.logic.*;
import info.teksol.mindcode.processor.MindustryValue;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Base class for optimizers. Contains methods to access and modify instructions of the program being processed
 * by the optimizer.
 * <p>
 * <b>Note:</b> The methods for accessing instructions always return {@code null} when the instruction doesn't exist,
 * regardless of the way the instruction is being accessed (via index, searching for properties etc.) It is assumed
 * that accessed instructions are practically always tested using instanceof, which handles {@code null} values
 * gracefully. Methods that won't handle invalid indexes gracefully are documented as such.
 * <p>
 * Method that modify a program do not fail silently. For example, when trying to insert instruction before
 * instruction not in a program, an error occurs.
 */
abstract class BaseOptimizer extends AbstractOptimizer {
    protected static final boolean TRACE = false;
    protected static final boolean DEBUG_PRINT = false;

    protected int modifications = 0;
    protected int insertions = 0;
    protected int deletions = 0;
    protected int passes = 0;
    protected int iterations = 0;

    public BaseOptimizer(Optimization optimization, OptimizationContext optimizationContext) {
        super(optimization, optimizationContext);
    }

    // Optimization logic

    /**
     * Performs one iteration of the optimization. Return true to run another iteration, false when done.
     * @return true to re-run the optimization
     */
    protected abstract boolean optimizeProgram(OptimizationPhase phase);

    @Override
    public boolean optimize(OptimizationPhase phase, int pass) {
        boolean repeat;
        boolean modified = false;
        int iteration = 1;
        do {
            optimizationContext.prepare();

            if (TRACE) {
                System.out.printf("%n*** %s: PASS %d, ITERATION %d ***%n%n", getName().toUpperCase(), pass, iteration);
            }
            if (DEBUG_PRINT) {
                System.out.println("Program at the beginning of the optimization phase:");
                optimizationContext.debugPrintProgram();
                System.out.println();
            }

            repeat = optimizeProgram(phase);
            optimizationContext.finish();

            modifications += optimizationContext.getModifications();
            insertions += optimizationContext.getInsertions();
            deletions += optimizationContext.getDeletions();
            modified |= optimizationContext.isUpdated();

            String title = pass == 0
                    ? "%s phase, %s, iteration %d".formatted(phase.name, getName(), iteration)
                    : "%s phase, %s, pass %d, iteration %d".formatted(phase.name, getName(), pass, iteration);

            debugPrinter.registerIteration(this, title, optimizationContext.getProgram());
            iteration++;
            iterations++;
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
        optimizationContext.prepare();
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

    //<editor-fold desc="Label & variable tracking">
    public LabelInstruction getLabelInstruction(LogicLabel label) {
        return optimizationContext.getLabelInstruction(label);
    }

    public void putVariableStates(LogicInstruction instruction, DataFlowVariableStates.VariableStates variableStates) {
        optimizationContext.putVariableStates(instruction, variableStates);
    }

    public DataFlowVariableStates.VariableStates getVariableStates(LogicInstruction instruction) {
        return optimizationContext.getVariableStates(instruction);
    }

    public DataFlowVariableStates.VariableStates getLoopVariables(AstContext conditionContext) {
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

    public LogicLiteral evaluateOpInstruction(OpInstruction op) {
        return optimizationContext.evaluateOpInstruction(op);
    }

    public OpInstruction extendedEvaluate(DataFlowVariableStates.VariableStates variableStates, OpInstruction op1) {
        return optimizationContext.extendedEvaluate(variableStates, op1);
    }

    public OpInstruction normalize(OpInstruction op) {
        return optimizationContext.normalize(op);
    }

    public OpInstruction normalizeMul(OpInstruction op, LogicVariable variable, LogicNumber number) {
        return optimizationContext.normalizeMul(op, variable, number);
    }

    public LogicLiteral evaluate(Operation operation, MindustryValue a, MindustryValue b) {
        return optimizationContext.evaluate(operation, a, b);
    }

    public LogicBoolean evaluateJumpInstruction(JumpInstruction jump) {
        return optimizationContext.evaluateJumpInstruction(jump);
    }

    public LogicBoolean evaluateLoopConditionJump(JumpInstruction jump, AstContext loopContext) {
        return optimizationContext.evaluateLoopConditionJump(jump, loopContext);
    }
    //</editor-fold>

    //<editor-fold desc="Finding instructions by position">
    /**
     * Return the instruction at given position in the program.
     *
     * @param index index of the instruction
     * @return the instruction at given index
     */
    protected LogicInstruction instructionAt(int index) {
        return optimizationContext.instructionAt(index);
    }

    /**
     * Returns the instruction preceding the given instruction. If such instruction doesn't exist
     * (because the reference instruction is the first one), returns null. If the reference instruction
     * isn't found the program, an exception is thrown.
     *
     * @param instruction instruction to find in the program
     * @return instruction before the reference instruction
     */
    protected LogicInstruction instructionBefore(LogicInstruction instruction) {
        return optimizationContext.instructionBefore(instruction);
    }

    /**
     * Returns the instruction following the given instruction. If such instruction doesn't exist
     * (because the reference instruction is the last one), returns null. If the reference instruction
     * isn't found the program, an exception is thrown.
     *
     * @param instruction instruction to find in the program
     * @return instruction after the reference instruction
     */
    protected LogicInstruction instructionAfter(LogicInstruction instruction) {
        return optimizationContext.instructionAfter(instruction);
    }

    /**
     * Provides a sublist of the current program. Will fail when such sublist cannot be created.
     * Returned list won't reflect further changed to the program.
     *
     * @param fromIndex starting index
     * @param toIndex ending index (exclusive)
     * @return a List containing given instructions.
     */
    protected List<LogicInstruction> instructionSubList(int fromIndex, int toIndex) {
        return optimizationContext.instructionSubList(fromIndex, toIndex);
    }

    /**
     * Provides a sublist of the current program. Will fail when such sublist cannot be created.
     * Returned list won't reflect further changed to the program.
     *
     * @param fromInstruction first instruction in the list
     * @param toInstruction last instruction in the list (inclusive)
     * @return a List containing given instructions.
     */
    protected List<LogicInstruction> instructionSubList(LogicInstruction fromInstruction, LogicInstruction toInstruction) {
        return optimizationContext.instructionSubList(fromInstruction, toInstruction);
    }

    /**
     * Provides stream of all instructions in the program.
     *
     * @return an instruction stream
     */
    protected Stream<LogicInstruction> instructionStream() {
        return optimizationContext.instructionStream();
    }

    /**
     * Locates an instruction based on instance identity and returns its index.
     * Returns -1 when such instruction doesn't exist.
     *
     * @param instruction instruction to locate
     * @return index of the instruction, or -1 if the instruction isn't found.
     */
    protected int instructionIndex(LogicInstruction instruction) {
        return optimizationContext.instructionIndex(instruction);
    }

    /** Returns the index of given instruction. When the instruction isn't found, an exception is thrown. */
    protected int existingInstructionIndex(LogicInstruction instruction) {
        return optimizationContext.existingInstructionIndex(instruction);
    }
    //</editor-fold>

    //<editor-fold desc="Finding instructions by properties">

    /**
     * Starting at given index, finds the first instruction matching predicate.
     * Returns the index or -1 if not found.
     *
     * @param startIndex index to start search from, inclusive
     * @param matcher predicate matching sought instruction
     * @return index of the first instruction matching the predicate
     */
    protected int firstInstructionIndex(int startIndex, Predicate<LogicInstruction> matcher) {
        return optimizationContext.firstInstructionIndex(startIndex, matcher);
    }

    /**
     * Finds the first instruction matching predicate in the entire program.
     * Returns the index or -1 if not found.
     *
     * @param matcher predicate matching sought instruction
     * @return index of the first instruction matching the predicate
     */
    protected int firstInstructionIndex(Predicate<LogicInstruction> matcher) {
        return optimizationContext.firstInstructionIndex(matcher);
    }

    /**
     * Finds the last instruction matching predicate up to the given instruction index.
     * Returns the index or -1 if not found.
     *
     * @param startIndex index to end search at, inclusive
     * @param matcher predicate matching sought instruction
     * @return index of the last instruction matching the predicate, up to the specified index
     */
    protected int lastInstructionIndex(int startIndex, Predicate<LogicInstruction> matcher) {
        return optimizationContext.lastInstructionIndex(startIndex, matcher);
    }

    /**
     * Finds the last instruction matching predicate in the entire program.
     * Returns the index or -1 if not found.
     *
     * @param matcher predicate matching sought instruction
     * @return index of the last instruction matching the predicate
     */
    protected int lastInstructionIndex(Predicate<LogicInstruction> matcher) {
        return optimizationContext.lastInstructionIndex(matcher);
    }

    /**
     * Finds a first non-label instruction following a label
     * Returns the index or -1 if not found.
     * If the label doesn't exist in the program, an exception is thrown.
     *
     * @param label target label
     * @return first non-label instruction following the label
     */
    protected int labeledInstructionIndex(LogicLabel label) {
        return optimizationContext.labeledInstructionIndex(label);
    }

    /**
     * Starting at given index, find first instruction matching predicate. Return null if not found.
     *
     * @param startIndex index to start search from, inclusive
     * @param matcher predicate matching sought instruction
     * @return the first instruction matching the predicate
     */
    protected LogicInstruction firstInstruction(int startIndex, Predicate<LogicInstruction> matcher) {
        return optimizationContext.firstInstruction(startIndex, matcher);
    }

    /**
     * Finds the first instruction matching predicate. Returns null if not found.
     *
     * @param matcher predicate matching sought instruction
     * @return the first instruction in the entire program matching the predicate
     */
    protected LogicInstruction firstInstruction(Predicate<LogicInstruction> matcher) {
        return optimizationContext.firstInstruction(matcher);
    }

    /**
     * Finds the last instruction matching predicate up to the given instruction index.
     * Returns null if not found.
     *
     * @param startIndex index to end search at, inclusive
     * @param matcher predicate matching sought instruction
     * @return index of the last instruction matching the predicate, up to the specified index
     */
    protected LogicInstruction lastInstruction(int startIndex, Predicate<LogicInstruction> matcher) {
        return optimizationContext.lastInstruction(startIndex, matcher);
    }

    /**
     * Finds the last instruction matching predicate. Returns null if not found.
     *
     * @param matcher predicate matching sought instruction
     * @return the last instruction matching the predicate
     */
    protected LogicInstruction lastInstruction(Predicate<LogicInstruction> matcher) {
        return optimizationContext.lastInstruction(matcher);
    }

    /**
     * Finds a first non-label instruction following a label.
     * Return null when not found
     */
    protected LogicInstruction labeledInstruction(LogicLabel label) {
        return optimizationContext.labeledInstruction(label);
    }

    /**
     * Return an independent list of instructions matching predicate.
     *
     * @param matcher predicate matching sought instructions
     * @return list of all instructions matching the predicate.
     */
    protected List<LogicInstruction> instructions(Predicate<LogicInstruction> matcher) {
        return optimizationContext.instructions(matcher);
    }
    /**
     * Return a number of instructions matching predicate.
     *
     * @param matcher predicate matching sought instructions
     * @return number of matching instructions;
     */
    protected int instructionCount(Predicate<LogicInstruction> matcher) {
        return optimizationContext.instructionCount(matcher);
    }
    //</editor-fold>

    //<editor-fold desc="General code structure methods">

    /**
     * Determines whether the given code block is contained, meaning it doesn't contain jumps outside.
     * Outside jumps are generated as a result of break, continue or return statements.
     *
     * @param codeBlock code block to inspect
     * @return true if the code block always exits though its last instruction.
     */
    protected boolean isContained(List<LogicInstruction> codeBlock) {
        return optimizationContext.isContained(codeBlock);
    }

    /**
     * Determines whether the given code block is contained, meaning it doesn't contain jumps outside.
     * Outside jumps are generated as a result of break, continue or return statements.
     *
     * @param logicList code block to inspect
     * @return true if the code block always exits though its last instruction.
     */
    protected boolean isContained(LogicList logicList) {
        return optimizationContext.isContained(logicList);
    }
    //</editor-fold>

    //<editor-fold desc="Program modification">
    /**
     * Inserts a new instruction at given index. The instruction must be assigned an AST context suitable for its
     * position in the program. An instruction must not be placed into the program twice; when an instruction
     * truly needs to be duplicated, an independent copy with proper AST context needs to be created.
     *
     * @param index where to place the instruction
     * @param instruction instruction to add
     * @throws MindcodeInternalError when the new instruction is already present elsewhere in the program
     */
    protected void insertInstruction(int index, LogicInstruction instruction) {
        optimizationContext.insertInstruction(index,instruction);
    }

    /**
     * Inserts all instruction in the list to the program, starting at given index.
     * See {@link #insertInstruction(int, LogicInstruction)}.
     *
     * @param index where to place the instructions
     * @param instructions instructions to add
     * @throws MindcodeInternalError when any of the new instructions is already present elsewhere in the program
     */
    protected void insertInstructions(int index, LogicList instructions) {
        optimizationContext.insertInstructions(index, instructions);
    }

    /**
     * Replaces an instruction at given index. The replaced instruction should either reuse the AST context
     * of the original instruction at this position, or use a new one specifically created for the purpose
     * of the replacement.
     * <p>
     * Replacing an instruction with the same instruction isn't supported and causes an OptimizationException
     * to be thrown.
     *
     * @param index index of an instruction to be replaced
     * @param instruction new instruction for given index
     * @throws MindcodeInternalError when trying to replace an instruction with itself, or when the replaced
     * instruction is already present elsewhere in the program
     */
    protected void replaceInstruction(int index, LogicInstruction instruction) {
        optimizationContext.replaceInstruction(index, instruction);
    }

    /**
     * Removes an instruction at given index.
     *
     * @param index index of an instruction to be removed
     */
    protected void removeInstruction(int index) {
        optimizationContext.removeInstruction(index);
    }

    protected void invalidateInstruction(int index) {
        LogicInstruction instruction = instructionAt(index);
        if (instruction.getOpcode() != Opcode.NOOP) {
            replaceInstruction(index, createNoOp(instruction.getAstContext()));
        }
    }

    protected void invalidateInstruction(LogicInstruction instruction) {
        replaceInstruction(instruction, createNoOp(instruction.getAstContext()));
    }

    /**
     * Inserts a new instruction before given, existing instruction. The new instruction must be assigned
     * an AST context suitable for its position in the program. An instruction must not be placed into the
     * program twice; when an instruction truly needs to be duplicated, an independent copy with proper
     * AST context needs to be created.
     * <p>
     * If the reference instruction isn't found in the program, an exception is thrown.
     *
     * @param anchor instruction before which to place the new instruction
     * @param inserted instruction to add
     */
    protected void insertBefore(LogicInstruction anchor, LogicInstruction inserted) {
        optimizationContext.insertBefore(anchor, inserted);
    }


    /**
     * Inserts a list of instructions before given, existing instruction.
     * <p>
     * If the reference instruction isn't found in the program, an exception is thrown.
     *
     * @param anchor instruction before which to place the new instruction
     * @param instructions instructions to add
     */
    protected void insertBefore(LogicInstruction anchor, LogicList instructions) {
        optimizationContext.insertBefore(anchor, instructions);
    }

    /**
     * Inserts a new instruction after given, existing instruction. The new instruction must be assigned
     * an AST context suitable for its position in the program. An instruction must not be placed into the
     * program twice; when an instruction truly needs to be duplicated, an independent copy with proper
     * AST context needs to be created.
     * <p>
     * If the reference instruction isn't found in the program, an exception is thrown.
     *
     * @param anchor instruction after which to place the new instruction
     * @param inserted instruction to add
     */
    protected void insertAfter(LogicInstruction anchor, LogicInstruction inserted) {
        optimizationContext.insertAfter(anchor, inserted);
    }

    /**
     * Replaces a given instruction with a new one. The new instruction should either reuse the AST context
     * of the original instruction at this position, or use a new one specifically created for the purpose
     * of the replacement.
     * <p>
     * If the original instruction isn't found in the program, an exception is thrown.
     *
     * @param original index of an instruction to be replaced
     * @param replaced new instruction for given index
     */
    protected void replaceInstruction(LogicInstruction original, LogicInstruction replaced) {
        optimizationContext.replaceInstruction(original, replaced);
    }

    protected void replaceInstructionArguments(LogicInstruction instruction, List<LogicArgument> newArgs) {
        optimizationContext.replaceInstructionArguments(instruction, newArgs);
    }


    /**
     * Removes an existing instruction from the program. If the instruction isn't found, an exception is thrown.
     *
     * @param instruction instruction to be removed
     */
    protected void removeInstruction(LogicInstruction instruction) {
        optimizationContext.removeInstruction(instruction);
    }

    /**
     * Removes an instruction immediately preceding given instruction.
     * If the given instruction isn't found, an exception is thrown.
     *
     * @param anchor the reference instruction
     */
    protected void removePrevious(LogicInstruction anchor) {
        optimizationContext.removePrevious(anchor);
    }

    /**
     * Removes an instruction immediately following given instruction.
     * If the given instruction isn't found, an exception is thrown.
     *
     * @param anchor the reference instruction
     */
    protected void removeFollowing(LogicInstruction anchor) {
        optimizationContext.removeFollowing(anchor);
    }

    /**
     * Removes all instructions matching given predicate.
     *
     * @param matcher predicate to match instructions to be removed
     */
    protected void removeMatchingInstructions(Predicate<LogicInstruction> matcher) {
        optimizationContext.removeMatchingInstructions(matcher);
    }
    //</editor-fold>

    //<editor-fold desc="Program modification using contexts">
    /**
     * Returns a label at the very beginning of the given AST context. If such label doesn't exist, creates it.
     * Note that the label must belong directly to the given context to be reused, labels belonging to
     * child contexts aren't considered. Care needs to be taken to provide correct context; for nodes modelled
     * with subcontexts a subcontext should be always provided.
     */
    protected LogicLabel obtainContextLabel(AstContext astContext) {
        return optimizationContext.obtainContextLabel(astContext);
    }
    //</editor-fold>

    //<editor-fold desc="Logic iterator">
    /**
     * Creates a new LogicIterator positioned at the start of the program.
     *
     * @return a new LogicIterator instance
     */
    protected LogicIterator createIterator() {
        return optimizationContext.createIterator();
    }

    /**
     * Creates a new LogicIterator positioned at given index.
     *
     * @param index initial position of the iterator
     * @return a new LogicIterator instance
     */
    public LogicIterator createIteratorAtIndex(int index) {
        return optimizationContext.createIteratorAtIndex(index);
    }

    /**
     * Creates a new LogicIterator positioned at given instruction.
     *
     * @param instruction target instruction
     * @return LogicIterator positioned at given instruction
     */
    protected LogicIterator createIteratorAtInstruction(LogicInstruction instruction) {
        return optimizationContext.createIteratorAtInstruction(instruction);
    }

    /**
     * Creates a new LogicIterator positioned at the beginning of given context.
     *
     * @param context target context
     * @return LogicIterator positioned at the beginning of given context
     */
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

    protected Function<AstContext, Void> returningNull(Consumer<AstContext> action) {
        return context -> {
            action.accept(context);
            return null;
        };
    }

    protected <T> List<T> forEachContext(Predicate<AstContext> matcher, Function<AstContext, T> action) {
        return optimizationContext.forEachContext(matcher, action);
    }

    protected <T> List<T> forEachContext(AstContextType contextType, AstSubcontextType subcontextType,
            Function<AstContext, T> action) {
        return optimizationContext.forEachContext(contextType, subcontextType, action);
    }

    protected List<AstContext> contexts(Predicate<AstContext> matcher) {
        return optimizationContext.contexts(matcher);
    }

    protected AstContext context(Predicate<AstContext> matcher) {
        return optimizationContext.context(matcher);
    }

    /**
     * Creates a list of AST contexts representing inline functions.
     *
     * @return list of inline function node contexts
     */
    protected List<AstContext> getInlineFunctions() {
        return optimizationContext.getInlineFunctions();
    }

    protected List<AstContext> getOutOfLineFunctions() {
        return optimizationContext.getOutOfLineFunctions();
    }
    //</editor-fold>


    //<editor-fold desc="Finding instructions by context">
    protected LogicList contextInstructions(AstContext astContext) {
        return optimizationContext.contextInstructions(astContext);
    }

    protected Stream<LogicInstruction> contextStream(AstContext astContext) {
        return optimizationContext.contextStream(astContext);
    }

    protected LogicInstruction firstInstruction(AstContext astContext) {
        return optimizationContext.firstInstruction(astContext);
    }

    protected int firstInstructionIndex(AstContext astContext) {
        return optimizationContext.firstInstructionIndex(astContext);
    }

    protected LogicInstruction lastInstruction(AstContext astContext) {
        return optimizationContext.lastInstruction(astContext);
    }

    protected int lastInstructionIndex(AstContext astContext) {
        return optimizationContext.lastInstructionIndex(astContext);
    }

    protected LogicInstruction instructionBefore(AstContext astContext) {
        return optimizationContext.instructionBefore(astContext);
    }

    protected LogicInstruction instructionAfter(AstContext astContext) {
        return optimizationContext.instructionAfter(astContext);
    }
    //</editor-fold>

    protected LogicList buildLogicList(AstContext context, List<LogicInstruction> instructions) {
        return optimizationContext.buildLogicList(context, List.copyOf(instructions));
    }

    protected final void trace(Supplier<String> text) {
        if (TRACE) {
            System.out.println(text.get());
        }
    }
}
