package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.generated.ast.visitors.AstForEachLoopStatementVisitor;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.AbstractStandaloneBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.LoopStack.LoopLabels;
import info.teksol.mc.mindcode.compiler.generation.variables.*;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static info.teksol.mc.mindcode.logic.arguments.LogicVoid.VOID;

@NullMarked
public class ForEachLoopStatementsBuilder extends AbstractLoopBuilder implements AstForEachLoopStatementVisitor<ValueStore> {
    protected final Set<ValueStore> invalidOutputs = new HashSet<>();

    public ForEachLoopStatementsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public ValueStore visitForEachLoopStatement(AstForEachLoopStatement node) {
        invalidOutputs.clear();

        int iterations = computeRangedForLoopIterations(node);
        if (iterations == Integer.MIN_VALUE) {
            buildBuiltInArrayForLoop(node);
        } else if (iterations > 0) {
            buildRangedForLoop(node, iterations);
        } else {
            new ForEachLoopBuilder(node).build();
        }
        return VOID;
    }

    private int computeRangedForLoopIterations(AstForEachLoopStatement node) {
        int[] sizes = node.getIteratorGroups().stream().mapToInt(this::iteratorGroupFixedRange).distinct().toArray();
        return sizes.length == 1 ? sizes[0] : 0;
    }

    // If this iterator group should be replaced by a ranged loop, returns the number of iterations.
    // Returns 0 otherwise.
    private int iteratorGroupFixedRange(AstIteratorsValuesGroup group) {
        if (group.getValues().getExpressions().size() != 1) return 0;

        AstExpression firstNode = group.getValues().getExpressions().getFirst();
        int arraySize = 0;

        if (firstNode instanceof AstIdentifier id) {
            ValueStore expression = variables.findVariable(id.getName(), true);
            if (expression instanceof BuiltInArray) return Integer.MIN_VALUE;
            if (expression instanceof ArrayStore arrayStore &&
                    (!arrayStore.optimizeElementAccess() || arrayStore.size() >= group.getProfile().getArrayIterationThreshold())) {
                arraySize = arrayStore.size();
            }
        } else if (firstNode instanceof AstSubarray) {
            ValueStore expression = findValue(firstNode);
            if (expression instanceof ArrayStore arrayStore) {
                arraySize = !arrayStore.optimizeElementAccess() || arrayStore.size() >= group.getProfile().getArrayIterationThreshold()
                        ? arrayStore.size() : 0;
            } else if (expression != LogicVariable.INVALID) {
                throw new MindcodeInternalError("Error evaluating subarray");
            }
        }

        // If the array size isn't divisible by the number of iterators, the error gets reported by the
        // standard for each loop builder.
        return arraySize % group.getIterators().size() == 0 ? arraySize / group.getIterators().size() : 0;
    }

    List<LoopIterator> createArrayIterators(AstForEachLoopStatement node) {
        List<LoopIterator> list = new ArrayList<>();
        for (AstIteratorsValuesGroup valuesGroup : node.getIteratorGroups()) {
            int iteratorCount = valuesGroup.getIterators().size();
            int iteratorIndex = 0;
            for (AstIterator astIterator : valuesGroup.getIterators()) {
                LoopIterator iterator = processIterator(valuesGroup, astIterator, iteratorCount, iteratorIndex);
                ArrayStore store = (ArrayStore) findValue(valuesGroup.getValues().getExpressions().getFirst());
                iterator.setArray(store);
                list.add(iterator);
                iteratorIndex++;
            }
        }
        return list;
    }

    private void buildBuiltInArrayForLoop(AstForEachLoopStatement node) {
        if (node.getIteratorGroups().size() > 1) {
            error(node.getIteratorGroups().get(1), ERR.FOR_EACH_INVALID_LINKS);
        }

        // We need to switch contexts
        assembler.exitAstNode(node);
        assembler.enterAstNode(node, AstContextType.LOOP);

        // Initialization
        assembler.setSubcontextType(AstSubcontextType.INIT, LOOP_REPETITIONS);

        // Creates all variables as needed
        List<LoopIterator> iterators = createArrayIterators(node);
        ArrayStore arr = iterators.getFirst().array();

        // Report out iterators
        iterators.stream().filter(it -> it.out).forEach(iterator -> error(iterator.var, ERR.LVALUE_CANNOT_ASSIGN_TO_ITERATOR));

        boolean descending = iterators.getFirst().descending;

        LogicValue start = descending
                ? assembler.createOp(Operation.SUB, assembler.nextTemp(), arr.limit(), LogicNumber.ONE).getResult()
                : LogicNumber.ZERO;
        LogicValue limit = descending ? LogicNumber.ZERO : arr.limit();
        LogicVariable loopControlVariable = assembler.nextTemp();
        loopControlVariable.setValue(assembler, start);

        final LogicLabel beginLabel = assembler.nextLabel();
        LoopLabels loopLabels = enterLoop(node, "for");

        // Condition
        assembler.setSubcontextType(AstSubcontextType.CONDITION, LOOP_REPETITIONS);
        assembler.createLabel(beginLabel);
        assembler.createJump(loopLabels.breakLabel(), descending ? Condition.LESS_THAN : Condition.GREATER_THAN_EQ, loopControlVariable, limit);

        // Loop body
        assembler.setSubcontextType(AstSubcontextType.BODY, LOOP_REPETITIONS);

        // Set up iterators
        iterators.forEach(iterator -> iterator.var.copyFrom(assembler,
                iterator.array().getElement(assembler, iterator.array().sourcePosition(), loopControlVariable, true)));

        visitBody(node.getBody());

        // Continue label
        // The label needs to be part of the loop body so that it gets copied on loop unrolling
        assembler.createLabel(loopLabels.continueLabel());

        // Update
        assembler.setSubcontextType(AstSubcontextType.UPDATE, LOOP_REPETITIONS);
        assembler.createOp(descending ? Operation.SUB : Operation.ADD, loopControlVariable, loopControlVariable, LogicNumber.ONE);

        // Flow control
        assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, LOOP_REPETITIONS);
        assembler.createJumpUnconditional(beginLabel);

        // Exit
        assembler.createLabel(loopLabels.breakLabel());
        assembler.clearSubcontextType();
        exitLoop(loopLabels);
    }

    private void buildRangedForLoop(AstForEachLoopStatement node, int iterations) {
        // Creates all variables as needed
        List<LoopIterator> iterators = createArrayIterators(node);

        // Handle large arrays separately
        if (iterators.stream().anyMatch(it -> it.array instanceof LargeArray)) {
            buildRangedForLoopLarge(node, iterations, iterators);
            return;
        }

        // We need to switch contexts
        assembler.exitAstNode(node);

        assembler.enterAstNode(node, AstContextType.LOOP);
        assembler.setSubcontextType(AstSubcontextType.INIT, 1.0);

        LoopIterator loopControl = iterators.getFirst();
        loopControl.setIteration(0);
        int limit = loopControl.descending
                ? loopControl.index - (iterations - 1) * loopControl.iteratorCount
                : loopControl.index + iterations * loopControl.iteratorCount;

        for (LoopIterator iterator : iterators) {
            iterator.setIteration(0);

            boolean found = false;

            for (LoopIterator previous : iterators) {
                if (previous == iterator) break;

                if (iterator.isCompatible(previous)) {
                    iterator.indexVariable = previous.indexVariable;
                    iterator.updateIndex = false;
                    found = true;
                    break;
                }

                if (iterator.derive(previous)) {
                    iterator.indexVariable = assembler.nextTemp();
                    iterator.updateIndex = false;
                    found = true;
                    break;
                }
            }

            if (!found) {
                iterator.indexVariable = assembler.nextTemp();
                assembler.createSet(iterator.indexVariable(), LogicNumber.create(iterator.index));
            }
        }

        for (LoopIterator iterator : iterators) {
            if (iterator.array().getStartOffset() != 0) {
                // Defer to the master array, as we're actually taking array offsets into an account here explicitly
                ArrayStore masterArray = iterator.array().getMasterArray();
                iterator.array = masterArray.offset(-masterArray.getStartOffset());
                iterator.setIteration(0);
            }
        }

        LoopLabels loopLabels = enterLoop(node, "for");
        buildInnerLoop(node, loopLabels, iterators, iterations, loopControl, LogicNumber.create(limit), true);
        exitLoop(loopLabels);
    }

    private void buildRangedForLoopLarge(AstForEachLoopStatement node, int iterations, List<LoopIterator> iterators) {
        LoopIterator loopControl = iterators.stream().filter(it -> it.array instanceof LargeArray).findFirst().orElseThrow();
        LogicVariable limit = assembler.nextTemp();

        int iteration = 0;
        List<LoopIterator> largeIterators = iterators.stream().filter(it -> it.array instanceof LargeArray).toList();

        while (iteration < iterations) {
            for (LoopIterator it : largeIterators) {
                it.setIteration(iteration);
                int index = it.indexValue();
                it.bodyIteratorValue = iteration == 0 || index == it.bodyIteratorValue ? index : Integer.MIN_VALUE;
                it.memoryValues.add(it.memoryValue());
            }

            iteration = iterators.stream().mapToInt(LoopIterator::nextIteration).min().orElseThrow();
        }

        iterators.forEach(it -> it.setIteration(0));

        // Allocate indexes
        for (LoopIterator iterator : iterators) {
            LoopIterator memoryCompatible = iterators.stream().filter(it -> it.memoryValues.equals(iterator.memoryValues)).findFirst().orElseThrow();
            iterator.memoryVariable = iterator == memoryCompatible ? assembler.nextTemp() : memoryCompatible.memoryVariable;
            iterator.updateMemory = iterator == memoryCompatible;

            LoopIterator indexCompatible = iterators.stream().filter(iterator::isCompatible).findFirst().orElseThrow();
            iterator.indexVariable = iterator == indexCompatible ? assembler.nextTemp() : indexCompatible.indexVariable;
            iterator.updateIndex = iterator == indexCompatible;
        }

        new ForEachLoopBuilderBase(node, false) {
            int iteration = 0;
            int lastLimit = Integer.MIN_VALUE;

            @Override
            protected boolean hasMoreData() {
                return iteration < iterations;
            }

            @Override
            protected void prepareIteration(List<IterationElement> outputs) {
                for (LoopIterator it : iterators) {
                    int next = it.nextIteration();
                    it.setIteration(iteration);

                    // Note: iteration always changes on the first pass
                    if (it.array instanceof LargeArray a && (iteration == 0 || next != it.nextIteration())) {
                        if (it.updateMemory) {
                            assembler.createSet(it.memoryVariable(), it.memoryValue());
                        }
                        if (it.updateIndex && it.bodyIteratorValue == Integer.MIN_VALUE) {
                            assembler.createSet(it.indexVariable(), LogicNumber.create(it.indexValue()));
                        }
                    }
                }

                int oldIteration = iteration;
                iteration = Math.min(iterations, iterators.stream().mapToInt(LoopIterator::nextIteration).min().orElseThrow());
                int newLimit = loopControl.descending
                        ? loopControl.indexValue() - (iteration - oldIteration - 1) * loopControl.iteratorCount
                        : loopControl.indexValue() + (iteration - oldIteration) * loopControl.iteratorCount;

                if (newLimit != lastLimit) {
                    assembler.createSet(limit, LogicNumber.create(newLimit));
                    lastLimit = newLimit;
                }
            }

            @Override
            protected void createBody() {
                assembler.enterAstNode(node, AstContextType.LOOP);
                assembler.setSubcontextType(AstSubcontextType.INIT, 1.0);
                buildInnerLoop(node, loopLabels, iterators, iterations, loopControl, limit, false);
                assembler.exitAstNode(node, AstContextType.LOOP);
            }
        }.build();
    }

    private void buildInnerLoop(AstForEachLoopStatement node, LoopLabels loopLabels, List<LoopIterator> iterators, int iterations,
            LoopIterator loopControl, LogicValue limit, boolean createBreakLabel) {

        for (LoopIterator it : iterators) {
            if (it.array instanceof LargeArray && it.updateIndex && it.bodyIteratorValue != Integer.MIN_VALUE) {
                assembler.createSet(it.indexVariable(), LogicNumber.create(it.bodyIteratorValue));
            }
        }

        // Loop body
        assembler.setSubcontextType(AstSubcontextType.BODY, iterations);

        final LogicLabel beginLabel = assembler.nextLabel();
        assembler.createLabel(beginLabel);

        // Set up variables
        for (LoopIterator iterator : iterators) {
            ArrayStore array = iterator.array();

            iterator.indexDerivation.accept(assembler);

            if (array instanceof LargeArray) {
                if (iterator.var instanceof LogicVariable var) {
                    assembler.createRead(var, iterator.memoryVariable(), iterator.indexVariable());
                } else {
                    LogicVariable tmp = assembler.nextTemp();
                    assembler.createRead(tmp, iterator.memoryVariable(), iterator.indexVariable());
                    iterator.var.copyFrom(assembler, tmp);
                }
            } else {
                iterator.var.copyFrom(assembler, array.getElement(assembler, array.sourcePosition(), iterator.indexVariable(), true));
            }
        }

        visitBody(node.getBody());

        // Continue label
        // The label needs to be part of the loop body so that it gets copied on loop unrolling
        assembler.createLabel(loopLabels.continueLabel());

        // Copy values back
        for (LoopIterator it : iterators) {
            if (!it.out) continue;

            if (it.var.isLvalue()) {
                ArrayStore array = it.array();

                if (array instanceof LargeArray a) {
                    assembler.createWrite(it.var.getValue(assembler), it.memoryVariable(), it.indexVariable());
                } else {
                    array.getElement(assembler, array.sourcePosition(), it.indexVariable(), true).copyFrom(assembler, it.var);
                }
            } else if (invalidOutputs.add(it.var)) {
                error(it.var, ERR.LVALUE_CANNOT_ASSIGN_TO_ITERATOR);
            }
        }

        // Advance indexes
        for (LoopIterator it : iterators) {
            if (it.updateIndex) {
                assembler.createOp(it.descending ? Operation.SUB : Operation.ADD,
                        it.indexVariable(), it.indexVariable(), LogicNumber.create(it.iteratorCount));
            }
        }

        // Condition
        assembler.setSubcontextType(AstSubcontextType.CONDITION, iterations);
        assembler.createJump(beginLabel, loopControl.descending ? Condition.GREATER_THAN_EQ : Condition.LESS_THAN,
                loopControl.indexVariable(), limit);

        // Exit
        assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, 1);
        // The Flow Control subcontext needs to be present for unrolling
        // Use an unused label if the break label is not generated
        assembler.createLabel(createBreakLabel ? loopLabels.breakLabel(): assembler.nextLabel());
        assembler.clearSubcontextType();
    }

    private abstract class ForEachLoopBuilderBase extends AbstractStandaloneBuilder<AstForEachLoopStatement> {
        protected final LogicLabel bodyLabel = assembler.nextLabel();
        protected final LogicLabel marker = assembler.nextMarker();
        protected final LogicVariable nextAddress = assembler.nextTemp();
        protected final LoopLabels loopLabels;
        protected final boolean symbolicLabels;
        protected final boolean nullCounterNoop;
        protected final boolean createContinueLabel;

        public ForEachLoopBuilderBase(AstForEachLoopStatement node, boolean createContinueLabel) {
            super(ForEachLoopStatementsBuilder.this, node);
            symbolicLabels = node.getProfile().isSymbolicLabels();
            nullCounterNoop = node.getProfile().isNullCounterIsNoop();
            loopLabels = enterLoop(node, "for");
            this.createContinueLabel = createContinueLabel;
            allowContinue(false);
        }

        protected abstract boolean hasMoreData();

        protected abstract void prepareIteration(List<IterationElement> outputs);

        protected abstract void createBody();

        public void build() {
            int iterations = 0;
            while (hasMoreData()) {
                createIteration();
                iterations++;
            }

            // For-each loops do not put the `done` label into flow control subcontext
            // Not a bug.
            assembler.createLabel(loopLabels.breakLabel());

            if (iterations > 0) {
                assembler.clearSubcontextType();
            }
            exitLoop(loopLabels);
        }

        protected void createIteration() {
            // Code which sets up variables for the next iteration
            // Multiplier is set to 1: all instructions execute exactly once
            assembler.setSubcontextType(AstSubcontextType.ITR_LEADING, 1.0);

            LinkedList<IterationElement> outputs = new LinkedList<>();

            // Copy values from the list to iterators
            // Consumes data - must update hasMoreData()
            prepareIteration(outputs);

            boolean lastIteration = !hasMoreData();

            // Setting the iterator address.
            // Continue is disallowed in a value list -> nextAddress can't be used before being set up.
            LogicLabel nextValueLabel = assembler.nextLabel();

            if (symbolicLabels) {
                if (lastIteration && nullCounterNoop) {
                    assembler.createSet(nextAddress, LogicNull.NULL);
                } else {
                    assembler.createOp(Operation.ADD, nextAddress, LogicBuiltIn.COUNTER, LogicNumber.ONE);
                }
            } else {
                assembler.createSetAddress(nextAddress, nextValueLabel);
            }

            LogicLabel lastValueLabel = null;
            if (lastIteration) {
                if (symbolicLabels && !nullCounterNoop) {
                    // In symbolic labels mode, the nextAddress leads here. We need to jump over the body.
                    assembler.createJumpUnconditional(bodyLabel);
                    assembler.createMultiLabel(assembler.nextLabel(), marker);
                    lastValueLabel = assembler.nextLabel();
                    assembler.createJumpUnconditional(lastValueLabel);
                }

                // The last iteration: continue to the loop body directly
                allowContinue(true);

                // This is the last iteration. We'll output the loop body directly here.
                assembler.setSubcontextType(AstSubcontextType.BODY, LOOP_REPETITIONS);
                assembler.createLabel(bodyLabel);

                createBody();

                // Continue label
                // The label needs to be part of the loop body so that it gets copied on loop unrolling
                // May get skipped when this loop contains an inner loop as well
                if (createContinueLabel) {
                    assembler.createLabel(loopLabels.continueLabel());
                }

                // Jumps to the iteration trailing block
                // On the last iteration, this jumps right to the next statement, but it can't be avoided
                assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, LOOP_REPETITIONS);
                assembler.createMultiJump(nextAddress, marker);

                allowContinue(false);
            } else {
                // This isn't a last iteration: jump to the loop body
                assembler.createJumpUnconditional(bodyLabel);
            }

            // The trailing part of the iteration setup. For the last iteration the code is generated after
            // the loop body, so that the program flow naturally exits the loop after the last iteration.
            assembler.setSubcontextType(AstSubcontextType.ITR_TRAILING, 1.0);
            assembler.createMultiLabel(nextValueLabel, marker);

            if (lastValueLabel != null) {
                assembler.createLabel(lastValueLabel);
            }

            // Copy iterator values back to the array - only for `out` iterators
            for (IterationElement output : outputs) {
                if (output.value.isLvalue()) {
                    output.value.setValue(assembler, output.iterator.getValue(assembler));
                } else if (invalidOutputs.add(output.iterator.var)) {
                    if (output.value instanceof InputFunctionArgument arg) {
                        error(arg, ERR.LVALUE_CANNOT_ASSIGN_TO_ARGUMENT);
                    } else {
                        error(output.iterator.var, ERR.LVALUE_CANNOT_ASSIGN_TO_ITERATOR);
                    }
                }
            }
        }
    }

    private class ForEachLoopBuilder extends ForEachLoopBuilderBase {
        private final List<IterationGroup> iterationGroups;

        public ForEachLoopBuilder(AstForEachLoopStatement node) {
            super(node, true);
            iterationGroups = node.getIteratorGroups().stream().map(this::processIteratorGroup).toList();
        }

        private IterationGroup processIteratorGroup(AstIteratorsValuesGroup group) {
            return new IterationGroup(
                    group.getIterators().stream().map(iterator -> processIterator(group, iterator, 0, 0))
                            .collect(Collectors.toCollection(ArrayList::new)),
                    group.getValues().getExpressions().stream().mapMulti(this::processValue)
                            .collect(Collectors.toCollection(ArrayList::new)),
                    group.isDescending()
            );
        }

        private void processValue(AstExpression expression, Consumer<ValueStore> consumer) {
            if (variables.isVarargParameter(expression)) {
                variables.getVarargs().forEach(consumer);
            } else {
                consumer.accept(new DeferredValueStore(expression));
            }
        }

        @Override
        public void build() {
            super.build();
            iterationGroups.forEach(IterationGroup::generateMessage);
        }

        protected  boolean hasMoreData() {
            return iterationGroups.stream().anyMatch(IterationGroup::hasMoreData);
        }

        protected void prepareIteration(List<IterationElement> outputs) {
            iterationGroups.forEach(group -> group.generateIterationSetup(outputs));
        }

        protected void createBody() {
            visitBody(node.getBody());
        }
    }

    /// A ValueStore based on AstExpression which is only evaluated when actually accessed.
    private class DeferredValueStore implements ValueStore {
        private final AstExpression expression;
        private @Nullable ValueStore valueStore;
        private @Nullable ValueStore resolvedLValue;

        public DeferredValueStore(AstExpression expression) {
            this.expression = expression;
        }

        @Override
        public SourcePosition sourcePosition() {
            return expression.sourcePosition();
        }

        private ValueStore value() {
            if (valueStore == null) {
                valueStore = evaluate(expression);
            }
            return valueStore;
        }

        private ValueStore verifyLValue() {
            if (resolvedLValue == null) {
                resolvedLValue = resolveLValue(expression, value());
            }
            return resolvedLValue;
        }

        @Override
        public LogicString getMlogVariableName() {
            return value().getMlogVariableName();
        }

        @Override
        public boolean isComplex() {
            return value().isComplex();
        }

        @Override
        public boolean isLvalue() {
            return verifyLValue().isLvalue();
        }

        @Override
        public LogicValue getValue(ContextfulInstructionCreator creator) {
            return value().getValue(creator);
        }

        @Override
        public void readValue(ContextfulInstructionCreator creator, LogicVariable target) {
            value().readValue(creator, target);
        }

        @Override
        public void setValue(ContextfulInstructionCreator creator, LogicValue value) {
            verifyLValue().setValue(creator, value);
        }

        @Override
        public void writeValue(ContextfulInstructionCreator creator, Consumer<LogicVariable> valueSetter) {
            verifyLValue().writeValue(creator, valueSetter);
        }

        @Override
        public LogicValue getWriteVariable(ContextfulInstructionCreator creator) {
            return verifyLValue().getWriteVariable(creator);
        }

        @Override
        public void storeValue(ContextfulInstructionCreator creator) {
            verifyLValue().storeValue(creator);
        }
    }

    private final class IterationGroup {
        private final List<LoopIterator> iterators;
        private final LinkedList<ValueStore> values;
        private final boolean descending;
        private int consumedValues = 0;
        private int missing = 0;
        private boolean omitErrors = false;

        private IterationGroup(ArrayList<LoopIterator> iterators, ArrayList<ValueStore> values, boolean descending) {
            this.iterators = descending ? reverse(iterators) : iterators;
            this.values = new LinkedList<>(descending ? reverse(values) : values);
            this.descending = descending;
        }

        private static <T> ArrayList<T> reverse(ArrayList<T> list) {
            Collections.reverse(list);
            return list;
        }

        boolean hasMoreData() {
            return !values.isEmpty();
        }

        private SourcePosition sourcePosition() {
            return iterators.getFirst().var.sourcePosition().upTo(iterators.getLast().var.sourcePosition());
        }

        private ValueStore nextValue() {
            if (values.isEmpty()) {
                if (omitErrors) return INACTIVE_VALUE;

                if (missing == 0) {
                    error(sourcePosition(), ERR.FOR_EACH_WRONG_NUMBER_OF_VALUES, consumedValues, iterators.size());
                }
                missing++;
                return INACTIVE_VALUE;
            }

            consumedValues++;
            ValueStore value = values.removeFirst();

            if (value instanceof DeferredValueStore deferredValueStore) {
                ValueStore evaluated = deferredValueStore.value();
                switch (evaluated) {
                    case LogicVariable var when var.isReference() -> {
                        // Can't happen except inline function compiled without being called
                        if (assembler.isActive()) throw new MindcodeInternalError("Unresolved variable reference in active mode.");
                        omitErrors = true;
                        return INACTIVE_VALUE;
                    }
                    case BuiltInArray _ -> {
                        error(value, ERR.FOR_EACH_INVALID_LINKS);
                        return LogicVariable.INVALID;
                    }
                    case ElementsArrayStore arrayStore -> {
                        if (arrayStore.hasArrayOffset()) {
                            int n = arrayStore.size();
                            values.addAll(0, Collections.nCopies(n, LogicVariable.INVALID));
                            for (int i = 0; i < n; i++) {
                                int index = descending ? n - 1 - i : i;
                                values.set(i, arrayStore.getElement(assembler, value.sourcePosition(), LogicNumber.create(index), true));
                            }
                        } else {
                            values.addAll(0, descending ? arrayStore.getElements().reversed() : arrayStore.getElements());
                        }
                        return values.removeFirst();
                    }
                    default -> { }
                }
            }

            return value;
        }

        void generateIterationSetup(List<IterationElement> outputs) {
            if (values.isEmpty()) {
                missing += iterators.size();
            } else {
                for (LoopIterator iterator : iterators) {
                    ValueStore value = nextValue();
                    iterator.setValue(assembler, value.getValue(assembler));
                    if (iterator.out) {
                        outputs.add(new IterationElement(iterator, value));
                    }
                }
            }
        }

        void generateMessage() {
            if (missing > 0) {
                error(sourcePosition(), ERR.FOR_EACH_UNBALANCED_GROUPS, consumedValues, consumedValues + missing);
            }
        }
    }

    private record IterationElement(LoopIterator iterator, ValueStore value) {
    }

    private LoopIterator processIterator(AstIteratorsValuesGroup group, AstIterator iterator, int iteratorCount, int iteratorIndex) {
        if (group.hasDeclaration()) {
            if (iterator.getIterator() instanceof AstIdentifier identifier) {
                variables.createVariable(isLocalContext(), identifier, VariableScope.LOOP_CONTROL, Modifiers.EMPTY);
            } else {
                // Probably can't happen due to grammar
                error(iterator.getIterator(), ERR.IDENTIFIER_EXPECTED);
            }
        } else if (globalProfile.isStrict()) {
            error(group, ERR.LOOP_CONTROL_VARIABLE_NOT_DECLARED);
        }

        if (iterator.getIterator() instanceof AstIdentifier identifier){
            variables.registerLoopControlVariable(identifier, group.hasDeclaration());
        }

        ValueStore loopControlVariable = resolveLValue(iterator.getIterator());
        return new LoopIterator(iterator.hasOutModifier(), loopControlVariable, group.isDescending(), iteratorCount, iteratorIndex);
    }

    private static final ValueStore INACTIVE_VALUE = new InactiveValueStore();

    private static class InactiveValueStore implements ValueStore {
        @Override
        public SourcePosition sourcePosition() {
            return SourcePosition.EMPTY;
        }

        @Override
        public LogicString getMlogVariableName() {
            return LogicVariable.INVALID.getMlogVariableName();
        }

        @Override
        public boolean isComplex() {
            return false;
        }

        @Override
        public boolean isLvalue() {
            return true;
        }

        @Override
        public LogicValue getValue(ContextfulInstructionCreator creator) {
            return LogicVariable.INVALID;
        }

        @Override
        public void readValue(ContextfulInstructionCreator creator, LogicVariable target) {
            // Do nothing
        }

        @Override
        public void setValue(ContextfulInstructionCreator creator, LogicValue value) {
            // Do nothing
        }

        @Override
        public void writeValue(ContextfulInstructionCreator creator, Consumer<LogicVariable> valueSetter) {
            // Do nothing
        }
    }
}
