package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.LogicInstructionPrinter;
import info.teksol.mindcode.compiler.MessageLevel;
import info.teksol.mindcode.compiler.generator.CallGraph;
import info.teksol.mindcode.compiler.generator.CallGraph.Function;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.compiler.optimization.DataFlowOptimizer.VariableStates.VariableValue;
import info.teksol.mindcode.logic.*;
import info.teksol.mindcode.processor.ExpressionEvaluator;
import info.teksol.mindcode.processor.MindustryValue;

import java.util.*;
import java.util.stream.Collectors;

import static info.teksol.mindcode.compiler.instructions.AstSubcontextType.*;
import static info.teksol.mindcode.logic.Operation.*;

public class DataFlowOptimizer extends BaseOptimizer {
    public static final boolean DEBUG = false;
    public static final boolean DEBUG2 = false;

    /** Cached single instance for obtaining the results of evaluating expressions. */
    private final ExpressionValue expressionValue;

    /**
     * Instruction replacement map. All possible replacements are done after the data flow analysis is finished,
     * to avoid concurrent modification exceptions while updating the program during the analysis.
     */
    private final Map<LogicInstruction, LogicInstruction> replacements = new IdentityHashMap<>();

    /**
     * Set of instructions whose sole purpose is to set value to some variable. If the variable isn't subsequently
     * read, the instruction is useless and can be removed.
     */
    private final Set<LogicInstruction> defines = Collections.newSetFromMap(new IdentityHashMap<>());

    /**
     * Set of variable producing instructions that were actually used in the program and need to be kept.
     */
    private final Set<LogicInstruction> keep = Collections.newSetFromMap(new IdentityHashMap<>());

    /** Exceptions to the keep set */
    private final Set<LogicInstruction> keepNot = Collections.newSetFromMap(new IdentityHashMap<>());

    /**
     * List of potentially uninitialized variables. In at least one branch the variable can be read without
     * being assigned a value first.
     */
    private final Set<LogicVariable> uninitialized = new HashSet<>();

    /**
     * Maps labels to their respective instructions. Rebuilt at the beginning of each iteration. Allows quickly
     * determine the context of the label instruction and therefore jumps that lead outside their respective context
     * (non-localized jumps). Non-localized jumps are the product of break, continue or return statements.
     */
    private Map<LogicLabel, LogicInstruction> labels;

    /** Maps function prefix to a list of variables directly or indirectly read by the function */
    private Map<String, Set<LogicVariable>> functionReads;

    /** Maps function prefix to a list of variables directly or indirectly written by the function */
    private Map<String, Set<LogicVariable>> functionWrites;

    public DataFlowOptimizer(InstructionProcessor instructionProcessor) {
        super(instructionProcessor);
        expressionValue = new ExpressionValue(instructionProcessor);
    }

    @Override
    protected boolean optimizeProgram() {
        defines.clear();
        keep.clear();
        keepNot.clear();
        uninitialized.clear();
        replacements.clear();

        if (DEBUG || DEBUG2) {
            System.out.println("\n\n\n*** NEW ITERATION ***\n");
        }

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
                    if (!keep.contains(instruction) || keepNot.contains(instruction)) {
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
                .sorted()
                .distinct()
                .collect(Collectors.joining(", "));

        if (!uninitializedList.isEmpty()) {
            emitMessage(MessageLevel.WARNING, "       List of uninitialized variables: %s.", uninitializedList);
        }
    }

    private void processTopContext(AstContext context) {
        VariableStates variableStates = new VariableStates();
        if (context.functionPrefix() != null) {
            Function function = getCallGraph().getFunctionByPrefix(context.functionPrefix());
            function.getDeclaration().getParams().stream()
                    .map(p -> LogicVariable.local(function.getName(), context.functionPrefix(), p.getName()))
                    .forEachOrdered(variableStates::markInitialized);
        }

        variableStates = processContext(context, variableStates, true);
        keepNot.addAll(variableStates.keepNot.values());

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

            for (int j = start; j < children.size(); j++) {
                // Do not propagate constants on first iteration...
                variableStates = processContext(children.get(j), variableStates, i > 0);
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

        p(instruction);

        // Altogether ignore push and pop instructions
        // TODO Enhance analysis by considering pushed/popped values are unchanged by a function call
        //      Allow variable substitution in push instructions (additional optimization)
        if (instruction instanceof PushOrPopInstruction) {
            return;
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
            case OpInstruction op && op.getOperation().isDeterministic() -> evaluateOpInstruction(op);
            default -> null;
        };

        if (DEBUG2) {
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
                OpInstruction newInstruction = extendedEvaluate(variableStates, op);
                if (newInstruction != null) {
                    replacements.put(instruction, normalize(newInstruction));
                }
            }
        }

        variableStates.print("  after processing instruction");
    }

    private LogicLiteral evaluateOpInstruction(OpInstruction op) {
        if (op.hasSecondOperand()) {
            return evaluateBinaryOpInstruction(op);
        } else {
            return evaluateUnaryOpInstruction(op);
        }
    }

    private LogicLiteral evaluateUnaryOpInstruction(OpInstruction op) {
        if (op.getX() instanceof LogicLiteral x && x.isNumericLiteral()) {
            return evaluate(op.getOperation(), x, LogicNull.NULL);
        }
        return null;
    }

    private LogicLiteral evaluateBinaryOpInstruction(OpInstruction op) {
        if (op.getX() instanceof LogicLiteral x && x.isNumericLiteral()
                && op.getY() instanceof LogicLiteral y && y.isNumericLiteral()) {
            return evaluate(op.getOperation(), x, y);
        }
        return null;
    }

    private OpInstruction extendedEvaluate(VariableStates variableStates, OpInstruction op1) {
        LogicLiteral x1 = op1.getX() instanceof LogicLiteral l && l.isNumericLiteral() ? l : null;
        LogicLiteral y1 = op1.getY() instanceof LogicLiteral l && l.isNumericLiteral() ? l : null;
        // We need just one literal
        if (x1 == null ^ y1 == null) {
            LogicLiteral literal1 = x1 == null ? y1 : x1;
            VariableValue variableValue = variableStates.findVariableValue(x1 == null ? op1.getX() : op1.getY());
            if (variableValue != null && variableValue.isExpression()
                    && variableValue.instruction instanceof OpInstruction op2 && op2.hasSecondOperand()) {
                LogicLiteral x2 = op2.getX() instanceof LogicLiteral l && l.isNumericLiteral() ? l : null;
                LogicLiteral y2 = op2.getY() instanceof LogicLiteral l && l.isNumericLiteral() ? l : null;

                if (x2 == null ^ y2 == null) {
                    LogicLiteral literal2 = x2 == null ? y2 : x2;
                    LogicValue value = x2 == null ? op2.getX() : op2.getY();
                    if (value instanceof LogicVariable variable) {
                        if (op1.getOperation() == op2.getOperation() && op1.getOperation().isAssociative()) {
                            // Perform the operation on the two literals
                            LogicLiteral literal = evaluate(op1.getOperation(), literal1, literal2);
                            if (literal != null) {
                                // Construct the instruction
                                return createOp(op1.getAstContext(), op1.getOperation(), op1.getResult(), variable, literal);
                            }
                        } else if (op1.getOperation() == ADD && op2.getOperation() == SUB) {
                            return evaluateAddAfterSub(op1, ADD, SUB, x2 != null, literal1, literal2, variable);
                        } else if (op1.getOperation() == SUB && op2.getOperation() == ADD) {
                            return evaluateSubAfterAdd(op1, ADD, SUB, x1 != null, literal1, literal2, variable);
                        } else if (op1.getOperation() == SUB && op2.getOperation() == SUB) {
                            return evaluateSubAfterSub(op1, ADD, SUB, x1 != null, x2 != null, literal1, literal2, variable);
                        } else if (op1.getOperation() == MUL && op2.getOperation() == DIV) {
                            return evaluateAddAfterSub(op1, MUL, DIV, x2 != null, literal1, literal2, variable);
                        } else if (op1.getOperation() == DIV && op2.getOperation() == MUL) {
                            return evaluateSubAfterAdd(op1, MUL, DIV, x1 != null, literal1, literal2, variable);
                        } else if (op1.getOperation() == DIV && op2.getOperation() == DIV) {
                            return evaluateSubAfterSub(op1, MUL, DIV, x1 != null, x2 != null, literal1, literal2, variable);
                        }
                    }
                }
            }
        }
        return null;
    }

    private OpInstruction evaluateAddAfterSub(OpInstruction op, Operation add, Operation sub,
            boolean literal2first, LogicLiteral literal1, LogicLiteral literal2, LogicVariable variable) {
        if (literal2first) {
            LogicLiteral literal = evaluate(add, literal2, literal1);
            return literal == null || literal.isNull() ? null
                    : createOp(op.getAstContext(), sub, op.getResult(), literal, variable);
        } else {
            LogicLiteral literal = evaluate(sub, literal2, literal1);
            return literal == null ? null
                    : createOp(op.getAstContext(), sub, op.getResult(), variable, literal);
        }
    }

    private OpInstruction evaluateSubAfterAdd(OpInstruction op, Operation add, Operation sub,
            boolean literal1first, LogicLiteral literal1, LogicLiteral literal2, LogicVariable variable) {
        LogicLiteral literal = evaluate(sub, literal1, literal2);
        if (literal != null && !literal.isNull()) {
            return literal1first
                    ? createOp(op.getAstContext(), sub, op.getResult(), literal, variable)
                    : createOp(op.getAstContext(), sub, op.getResult(), variable, literal);
        }
        return null;
    }

    private OpInstruction evaluateSubAfterSub(OpInstruction op, Operation add, Operation sub,
            boolean literal1first, boolean literal2first, LogicLiteral literal1, LogicLiteral literal2, LogicVariable variable) {
        LogicLiteral literal = literal2first
                ? evaluate(sub, literal2, literal1)
                : evaluate(add, literal2, literal1);

        if (literal != null && !literal.isNull()) {
            return literal1first == literal2first
                    ? createOp(op.getAstContext(), sub, op.getResult(), variable, literal)
                    : createOp(op.getAstContext(), sub, op.getResult(), literal, variable);
        }
        return null;
    }

    private OpInstruction normalize(OpInstruction op) {
        switch (op.getOperation()) {
            case SUB:
                if (op.getY() instanceof LogicNumber l && l.getDoubleValue() < 0.0) {
                    return createOp(op.getAstContext(), ADD, op.getResult(), op.getX(), l.negation());
                }
                break;

            case DIV:
                if (op.getY() instanceof LogicNumber l && l.getDoubleValue() != (long) l.getDoubleValue()) {
                    LogicNumber reciprocal = l.reciprocal(instructionProcessor);
                    if (reciprocal.toMlog().length() < l.toMlog().length()) {
                        return createOp(op.getAstContext(), MUL, op.getResult(), op.getX(), reciprocal);
                    }
                }
                break;

            case MUL:
                if (op.getX() instanceof LogicNumber number && op.getY() instanceof  LogicVariable variable) {
                    return normalizeMul(op, variable, number);
                } else if (op.getY() instanceof LogicNumber number && op.getX() instanceof  LogicVariable variable) {
                    return normalizeMul(op, variable, number);
                }
                break;
        }

        return op;
    }

    private OpInstruction normalizeMul(OpInstruction op, LogicVariable variable, LogicNumber number) {
        if (number.getDoubleValue() != (long) number.getDoubleValue()) {
            LogicNumber reciprocal = number.reciprocal(instructionProcessor);
            if (reciprocal.toMlog().length() < number.toMlog().length()) {
                return createOp(op.getAstContext(), DIV, op.getResult(), variable, reciprocal);
            }
        }
        return op;
    }

    private boolean canEliminate(LogicVariable variable) {
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

    private void p(LogicInstruction instruction) {
        if (DEBUG || DEBUG2) {
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

    /**
     * Describes known states of variables. Instances are primarily created by analysing code blocks, and then
     * merged when two or more code branches merge. Merging operations may produce variables with multiple definitions.
     * When merging two instances prescribing different values/expressions to the same variable, the values/expressions
     * are purged.
     */
    class VariableStates {
        /**
         *  Maps variables to their known values, which might be a constant represented by a literal,
         *  or an expression represented by an instruction.
         */
        private final Map<LogicVariable, VariableValue> values;

        /**
         * Maps variables to a list of instructions defining its current value (potentially more than one
         * due to branching).
         */
        private final Map<LogicVariable, List<LogicInstruction>> definitions;

        /** Maps variables to prior variables containing the same expression. */
        private final Map<LogicVariable, LogicVariable> equivalences;

        /** Set of initialized variables. */
        private final Set<LogicVariable> initialized;

        /** Identifies instructions that do not have to be kept, because they set identical value. */
        private final Map<LogicVariable, LogicInstruction> keepNot;


        public VariableStates() {
            values = new LinkedHashMap<>();
            definitions = new HashMap<>();
            equivalences = new HashMap<>();
            initialized = new HashSet<>();
            keepNot = new HashMap<>();
        }

        private VariableStates(VariableStates other) {
            values = new LinkedHashMap<>(other.values);
            definitions = new HashMap<>(other.definitions);
            equivalences = new HashMap<>(other.equivalences);
            initialized = new HashSet<>(other.initialized);
            keepNot = new HashMap<>(other.keepNot);
        }

        public VariableStates copy() {
            return new VariableStates(this);
        }

        private void p(LogicInstruction instruction) {
            if (DEBUG) {
                System.out.println("   " + instructionIndex(instruction) + ": " + LogicInstructionPrinter.toString(instructionProcessor, instruction));
            }
        }

        // Called when a variable value changes to purge all dependent expressions
        private void invalidateVariable(LogicVariable variable) {
            if (DEBUG2) {
                values.values().stream().filter(exp -> exp.dependsOn(variable))
                        .forEach(exp -> System.out.println("   Invalidating expression: " + exp.variable.toMlog() + ": " + exp.instruction));
                equivalences.entrySet().stream().filter(e -> e.getValue().equals(variable))
                        .forEach(e -> System.out.println("   Invalidating equivalence: " + e.getKey().toMlog() + ": " + e.getValue().toMlog()));
            }
            values.values().removeIf(exp -> exp.dependsOn(variable));
            equivalences.keySet().removeIf(var -> var.equals(variable));
            equivalences.values().removeIf(var -> var.equals(variable));
        }

        public void valueSet(LogicVariable variable, LogicInstruction instruction, LogicValue value) {
            if (DEBUG) {
                System.out.println("Value set: " + variable);
                p(instruction);
            }

            if (canEliminate(variable)) {
                // Only store the variable's value if it can be eliminated
                // Otherwise the value itself could be used - we do not want this for global variables.
                if (value == null) {
                    values.remove(variable);
                } else if (values.get(variable) != null && value.equals(values.get(variable).constantValue)) {
                    AstSubcontextType type = instruction.getAstContext().subcontextType();
                    if (type == OUT_OF_LINE_CALL || type == RECURSIVE_CALL) {
                        // A function argument is being set to the same value it already has. Skip it.
                        keepNot.put(variable, instruction);
                    }
                } else {
                    values.put(variable, new VariableValue(variable, value));
                }
            } else {
                keep.add(instruction);      // Ensure the instruction is kept
            }

            defines.add(instruction);
            initialized.add(variable);
            definitions.put(variable, List.of(instruction));

            // Update expressions
            invalidateVariable(variable);

            // Handle expressions only if the value is not exactly known
            if (value == null) {
                // Find the oldest equivalent expression
                for (VariableValue expression : values.values()) {
                    if (expression.isExpression() && expression.isEqual(instruction)) {
                        if (DEBUG2) {
                            System.out.println("    Adding inferred equivalence " + variable.getFullName() + " == " + expression.variable.getFullName());
                        }
                        equivalences.put(variable, expression.variable);
                        break;
                    }
                }

                if (instruction instanceof SetInstruction set) {
                    if (set.getResult() == variable && set.getValue() instanceof LogicVariable variable2) {
                        if (DEBUG2) {
                            System.out.println("    Adding direct equivalence " + variable.getFullName() + " == " + variable2.getFullName());
                        }
                        equivalences.put(variable, variable2);
                    }
                } else if (instruction.getOutputs() == 1) {
                    VariableValue expression = new VariableValue(variable, instruction);
                    // Do not reuse expressions with self-modifying variables (they effectively invalidate themselves)
                    if (!expression.dependsOn(variable)) {
                        values.put(variable, expression);
                    }
                }
            }
        }

        public void markInitialized(LogicVariable variable) {
            initialized.add(variable);
        }

        public void valueReset(LogicVariable variable) {
            if (DEBUG) {
                System.out.println("Value reset: " + variable);
            }
            values.remove(variable);
            invalidateVariable(variable);
        }

        public void updateAfterFunctionCall(String localPrefix) {
            functionReads.get(localPrefix).forEach(variable -> valueRead(variable, false));
            functionWrites.get(localPrefix).forEach(this::valueReset);
        }

        public VariableValue findVariableValue(LogicValue variable) {
            return variable instanceof LogicVariable v ? values.get(v) : null;
        }

        public LogicValue valueRead(LogicVariable variable) {
            return valueRead(variable, true);
        }

        public LogicValue valueRead(LogicVariable variable, boolean markUninitialized) {
            if (DEBUG) {
                System.out.println("Value read: " + variable);
            }
            if (markUninitialized && !initialized.contains(variable)) {
                uninitialized.add(variable);
            }

            if (definitions.containsKey(variable)) {
                if (DEBUG) {
                    definitions.get(variable).forEach(this::p);
                }
                keep.addAll(definitions.get(variable));     // We need to keep all defining instructions
            }

            VariableValue variableValue = values.get(variable);
            return variableValue == null ? null : variableValue.constantValue;
        }

        public LogicVariable findEquivalent(LogicVariable value) {
            return equivalences.get(value);
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

            // Definitions are merged together
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

                        invalidateVariable(variable);
                    }
                } else {
                    definitions.put(variable, other.definitions.get(variable));
                    invalidateVariable(variable);
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

            keepNot.keySet().retainAll(other.keepNot.keySet());
            for (LogicVariable variable : other.keepNot.keySet()) {
                if (values.get(variable) != other.values.get(variable)) {
                    values.remove(variable);
                }
            }

            print("  result:");

            return this;
        }

        private void print(String title) {
            if (DEBUG) {
                System.out.println(title);
                values.values().forEach(v -> System.out.println("    " + v));
                definitions.forEach((k, v) -> {
                    System.out.println("    Definitions of " + k.getFullName());
                    v.forEach(ix -> System.out.println("      " + instructionIndex(ix)
                            + ": " + LogicInstructionPrinter.toString(instructionProcessor, ix)));
                });
                equivalences.forEach((k, v) -> System.out.println("    Equivalence: " + k.getFullName() + " = " + v.getFullName()));
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

        class VariableValue {
            /** Variable containing the result of the expression. */
            private final LogicVariable variable;

            /** Constant value of the variable, if known */
            private final LogicValue constantValue;

            /** The instruction producing the expression. */
            private final LogicInstruction instruction;

            public VariableValue(LogicVariable variable, LogicValue constantValue) {
                if (!constantValue.isConstant()) {
                    throw new IllegalArgumentException("Non-constant value " + constantValue);
                }
                this.variable = Objects.requireNonNull(variable);
                this.constantValue = Objects.requireNonNull(constantValue);
                this.instruction = null;
            }

            public VariableValue(LogicVariable variable, LogicInstruction instruction) {
                this.variable = Objects.requireNonNull(variable);
                this.constantValue = null;
                this.instruction = Objects.requireNonNull(instruction);
            }

            public boolean isExpression() {
                return constantValue == null;
            }

            /**
             * Determines whether the expression depends on the given variable.
             *
             * @param variable variable to inspect
             * @return true if th expression reads the value of given variable
             */
            public boolean dependsOn(LogicVariable variable) {
                return isExpression() && instruction.inputArgumentsStream().anyMatch(arg -> arg.equals(variable));
            }

            /**
             * Determines whether this expression is equivalent to the one produced by the given instruction.
             *
             * @param instruction instruction defining the second expression
             * @return true if the two expressions are equal
             */
            public boolean isEqual(LogicInstruction instruction) {
                if (!isExpression() || this.instruction.getOpcode() != instruction.getOpcode()) {
                    return false;
                }

                // Equivalence for SENSOR instruction is not supported. Sensed values are considered volatile.
                return switch (instruction) {
                    case OpInstruction op && op.getOperation().isDeterministic()
                                                    -> isOpEqual((OpInstruction) this.instruction, op);
                    case PackColorInstruction ix    -> isInstructionEqual(this.instruction, ix);
                    //case ReadInstruction ix         -> isInstructionEqual(this.instruction, ix);
                    default                         -> false;
                };
            }

            private boolean isVolatile(LogicInstruction instruction) {
                return isExpression() && instruction.inputArgumentsStream().anyMatch(LogicArgument::isVolatile);
            }

            @SuppressWarnings("SuspiciousMethodCalls")
            public LogicArgument remap(LogicArgument value) {
                return equivalences.containsKey(value) ? equivalences.get(value) : value;
            }

            private boolean isInstructionEqual(LogicInstruction first, LogicInstruction second) {
                List<LogicArgument> args1 = first.getArgs();
                List<LogicArgument> args2 = second.getArgs();

                if (isVolatile(first) || args1.size() != args2.size()) {
                    return false;
                }

                for (int i = 0; i < args1.size(); i++) {
                    if (!first.getParam(i).isOutput() && !Objects.equals(remap(args1.get(i)), remap(args2.get(i)))) {
                        return false;
                    }
                }

                return true;
            }

            /**
             * Determines whether two OP instructions produce the same expression. It is already known the first
             * instruction operation is deterministic. THe obvious case is when the two instructions are completely
             * identical. Additionally, commutative and inverse operations are handled.
             *
             * @param first first instruction to compare
             * @param second second instruction to compare
             * @return true if the two instructions produce the same expression
             */
            private boolean isOpEqual(OpInstruction first, OpInstruction second) {
                if (isVolatile(first)) {
                    return false;
                }

                LogicArgument x1 = remap(first.getX());
                LogicArgument x2 = remap(second.getX());
                LogicArgument y1 = first.hasSecondOperand() ? remap(first.getY()) : null;
                LogicArgument y2 = second.hasSecondOperand() ? remap(second.getY()) : null;

                if (first.getOperation() == second.getOperation() && Objects.equals(x1, x2) && Objects.equals(y1, y2)) {
                    return true;
                }

                // For commutative operations, swapped arguments are equal
                // For inverted operations, swapped arguments are also equal
                if (first.getOperation() == second.getOperation() && first.getOperation().isCommutative()
                        || first.getOperation().hasInverse() && first.getOperation().inverse() == second.getOperation()) {
                    return Objects.equals(x1, y2) && Objects.equals(y1, x2);
                }

                return false;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                VariableValue that = (VariableValue) o;
                return this.variable.equals(that.variable) && this.instruction == that.instruction
                        && Objects.equals(this.constantValue, that.constantValue);
            }

            @Override
            public int hashCode() {
                return Objects.hash(variable, constantValue, instruction);
            }

            @Override
            public String toString() {
                return "variable " + variable.getFullName() + ": " + (isExpression()
                        ? " expression " + LogicInstructionPrinter.toString(instructionProcessor, instruction)
                        : " constant " + constantValue);
            }
        }
    }

    private LogicLiteral evaluate(Operation operation, MindustryValue a, MindustryValue b) {
        ExpressionEvaluator.getOperation(operation).execute(expressionValue, a, b);
        return expressionValue.getLiteral();
    }
}
