package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.generated.ast.visitors.AstForEachLoopStatementVisitor;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.AbstractCodeBuilder;
import info.teksol.mc.mindcode.compiler.generation.AbstractStandaloneBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.LoopStack.LoopLabels;
import info.teksol.mc.mindcode.compiler.generation.variables.ArrayStore;
import info.teksol.mc.mindcode.compiler.generation.variables.Modifiers;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.compiler.generation.variables.VariableScope;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static info.teksol.mc.mindcode.logic.arguments.LogicVoid.VOID;

@NullMarked
public class ForEachLoopStatementsBuilder extends AbstractLoopBuilder implements AstForEachLoopStatementVisitor<ValueStore> {

    public ForEachLoopStatementsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public ValueStore visitForEachLoopStatement(AstForEachLoopStatement node) {
        new ForEachLoopBuilder(this, node).build();
        return VOID;
    }

    private class ForEachLoopBuilder extends AbstractStandaloneBuilder<AstForEachLoopStatement> {
        private final List<IterationGroup> iterationGroups;
        private final LogicLabel bodyLabel = assembler.nextLabel();
        private final LogicLabel marker = assembler.nextMarker();
        private final LogicVariable nextAddress = assembler.nextTemp();
        private final LoopLabels loopLabels;
        private final boolean symbolicLabels;
        private final boolean nullCounterNoop;

        public ForEachLoopBuilder(AbstractCodeBuilder builder, AstForEachLoopStatement node) {
            super(builder, node);
            symbolicLabels = node.getProfile().isSymbolicLabels();
            nullCounterNoop = node.getProfile().isNullCounterIsNoop();
            iterationGroups = node.getIteratorGroups().stream().map(this::processIteratorGroup).toList();
            loopLabels = enterLoop(node, "for");
            allowContinue(false);
        }

        private IterationGroup processIteratorGroup(AstIteratorsValuesGroup group) {
            return new IterationGroup(
                    group.getIterators().stream().map(iterator -> processIterator(group, iterator))
                            .collect(Collectors.toCollection(ArrayList::new)),
                    group.getValues().getExpressions().stream().mapMulti(this::processValue)
                            .collect(Collectors.toCollection(ArrayList::new)),
                    group.isDescending()
            );
        }

        private Iterator processIterator(AstIteratorsValuesGroup group, AstIterator iterator) {
            if (group.hasDeclaration()) {
                if (iterator.getIterator() instanceof AstIdentifier identifier) {
                    variables.createVariable(isLocalContext(), identifier, VariableScope.NODE, Modifiers.EMPTY);
                } else {
                    // Probably can't happen due to grammar
                    error(iterator.getIterator(), ERR.IDENTIFIER_EXPECTED);
                }
            }
            return new Iterator(iterator.hasOutModifier(), resolveLValue(iterator.getIterator()));
        }

        private void processValue(AstExpression expression, Consumer<ValueStore> consumer) {
            if (variables.isVarargParameter(expression)) {
                variables.getVarargs().forEach(consumer);
            } else {
                consumer.accept(new DeferredValueStore(expression));
            }
        }

        private boolean hasMoreData() {
            return iterationGroups.stream().anyMatch(IterationGroup::hasMoreData);
        }

        private void build() {
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

            iterationGroups.forEach(IterationGroup::generateMessage);
        }

        private void createIteration() {
            // Code which sets up variables for the next iteration
            // Multiplier is set to 1: all instructions execute exactly once
            assembler.setSubcontextType(AstSubcontextType.ITR_LEADING, 1.0);

            LinkedList<IterationElement> outputs = new LinkedList<>();

            // Copy values from the list to iterators
            iterationGroups.forEach(group -> group.generateIterationSetup(outputs));

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
                createBody();
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
                output.value.setValue(assembler, output.iterator.getValue());
            }
        }

        private void createBody() {
            allowContinue(true);

            // This is the last iteration. We'll output the loop body directly here.
            assembler.setSubcontextType(AstSubcontextType.BODY, LOOP_REPETITIONS);
            assembler.createLabel(bodyLabel);

            visitBody(node.getBody());

            // Continue label
            // The label needs to be part of loop body so that it gets copied on loop unrolling
            assembler.createLabel(loopLabels.continueLabel());

            // Jumps to the iteration trailing block
            // On the last iteration, this jumps right to the next statement, but it can't be avoided
            assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, LOOP_REPETITIONS);
            assembler.createMultiJump(nextAddress, marker);

            allowContinue(false);
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
        private final List<Iterator> iterators;
        private final LinkedList<ValueStore> values;
        private final boolean descending;
        private int consumedValues = 0;
        private int missing = 0;
        private boolean omitErrors = false;

        private IterationGroup(ArrayList<Iterator> iterators, ArrayList<ValueStore> values, boolean descending) {
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
                if (evaluated instanceof LogicVariable var && var.isReference()) {
                    // Can't happen except inline function compiled without being called
                    if (assembler.isActive()) throw new MindcodeInternalError("Unresolved variable reference in active mode.");
                    omitErrors = true;
                    return INACTIVE_VALUE;
                } else if (evaluated instanceof ArrayStore arrayStore) {
                    if (descending) {
                        values.addAll(0, reverse(new ArrayList<>(arrayStore.getElements())));
                    } else {
                        values.addAll(0, arrayStore.getElements());
                    }
                    return values.removeFirst();
                }
            }

            return value;
        }

        void generateIterationSetup(List<IterationElement> outputs) {
            if (values.isEmpty()) {
                missing += iterators.size();
            } else {
                for (Iterator iterator : iterators) {
                    ValueStore value = nextValue();
                    iterator.setValue(value.getValue(assembler));
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

    private record IterationElement(Iterator iterator, ValueStore value) {
    }

    ///  Represents an iterator variable in the loop
    private final class Iterator {
        public final boolean out;
        private final ValueStore var;

        private Iterator(boolean out, ValueStore var) {
            this.out = out;
            this.var = var;
        }

        void setValue(LogicValue value) {
            var.setValue(assembler, value);
        }

        LogicValue getValue() {
            return var.getValue(assembler);
        }
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
