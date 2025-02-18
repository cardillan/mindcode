package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.generated.ast.visitors.AstForEachLoopStatementVisitor;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.*;
import info.teksol.mc.mindcode.compiler.generation.LoopStack.LoopLabels;
import info.teksol.mc.mindcode.compiler.generation.variables.ArrayStore;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.compiler.generation.variables.VariableScope;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

        public ForEachLoopBuilder(AbstractBuilder builder, AstForEachLoopStatement node) {
            super(builder, node);
            iterationGroups = node.getIteratorGroups().stream().map(this::processIteratorGroup).toList();
            loopLabels = enterLoop(node);
        }

        private IterationGroup processIteratorGroup(AstIteratorsValuesGroup group) {
            return new IterationGroup(
                    group.getIterators().stream().map(iterator -> processIterator(group, iterator)).toList(),
                    group.getValues().getExpressions().stream().mapMulti(this::processValue)
                            .collect(Collectors.toCollection(LinkedList::new)));
        }

        private Iterator processIterator(AstIteratorsValuesGroup group, AstIterator iterator) {
            if (group.hasDeclaration()) {
                if (iterator.getIterator() instanceof AstIdentifier identifier) {
                    variables.createVariable(isLocalContext(), identifier, VariableScope.NODE, Map.of());
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

            // For-each loops do not put done label into flow control subcontext
            // Not a bug.
            assembler.createLabel(loopLabels.breakLabel());

            if (iterations > 0) {
                assembler.clearSubcontextType();
            }
            exitLoop(node, loopLabels);

            iterationGroups.forEach(IterationGroup::generateMessage);
        }

        private void createIteration() {
            // Code which sets up variables for the next iteration
            // Multiplier is set to 1: all instructions execute exactly once
            assembler.setSubcontextType(AstSubcontextType.ITR_LEADING, 1.0);

            // Setting the iterator address first. It is possible to use `continue` in the value list
            // (why, oh why?), in which case the next iteration is performed.
            LogicLabel nextValueLabel = assembler.nextLabel();
            assembler.createSetAddress(nextAddress, nextValueLabel);

            LinkedList<IterationElement> outputs = new LinkedList<>();

            // Copy values from the list to iterators
            iterationGroups.forEach(group -> group.generateIterationSetup(outputs));

            if (hasMoreData()) {
                // This isn't a last iteration: jump to the loop body
                assembler.createJumpUnconditional(bodyLabel);
            } else {
                // The last iteration: continue to the loop body directly
                createBody();
            }

            // The trailing part of the iteration setup. For the last iteration the code is generated after
            // the loop body, so that the program flow naturally exits the loop after the last iteration.
            assembler.setSubcontextType(AstSubcontextType.ITR_TRAILING, 1.0);
            assembler.createMultiLabel(nextValueLabel, marker);

            // Copy iterator values back to the array - only for `out` iterators
            for (IterationElement output : outputs) {
                output.value.setValue(assembler, output.iterator.getValue());
            }
        }

        private void createBody() {
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
        public boolean isComplex() {
            return value().isComplex();
        }

        @Override
        public boolean isLvalue() {
            return verifyLValue().isLvalue();
        }

        @Override
        public LogicValue getValue(CodeAssembler assembler) {
            return value().getValue(assembler);
        }

        @Override
        public void readValue(CodeAssembler assembler, LogicVariable target) {
            value().readValue(assembler, target);
        }

        @Override
        public void setValue(CodeAssembler assembler, LogicValue value) {
            verifyLValue().setValue(assembler, value);
        }

        @Override
        public void writeValue(CodeAssembler assembler, Consumer<LogicVariable> valueSetter) {
            verifyLValue().writeValue(assembler, valueSetter);
        }

        @Override
        public LogicValue getWriteVariable(CodeAssembler assembler) {
            return verifyLValue().getWriteVariable(assembler);
        }

        @Override
        public void storeValue(CodeAssembler assembler) {
            verifyLValue().storeValue(assembler);
        }
    }

    private final class IterationGroup {
        private final List<Iterator> iterators;
        private final LinkedList<ValueStore> values;
        private int consumedValues = 0;
        private int missing = 0;

        private IterationGroup(List<Iterator> iterators, LinkedList<ValueStore> values) {
            this.iterators = iterators;
            this.values = values;
        }

        boolean hasMoreData() {
            return !values.isEmpty();
        }

        private SourcePosition sourcePosition() {
            return iterators.getFirst().var.sourcePosition().upTo(iterators.getLast().var.sourcePosition());
        }

        private ValueStore nextValue() {
            if (values.isEmpty()) {
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
                if (evaluated instanceof ArrayStore<?> arrayStore) {
                    values.addAll(0, arrayStore.getElements());
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

        List<IterationElement> next() {
            if (values.isEmpty()) {
                missing += iterators.size();
                return iterators.stream().map(it -> new IterationElement(it, INACTIVE_VALUE)).toList();
            } else {
                return iterators.stream().map(it -> new IterationElement(it, nextValue())).toList();
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
        public boolean isComplex() {
            return false;
        }

        @Override
        public boolean isLvalue() {
            return true;
        }

        @Override
        public LogicValue getValue(CodeAssembler assembler) {
            return LogicVariable.INVALID;
        }

        @Override
        public void readValue(CodeAssembler assembler, LogicVariable target) {
            // Do nothing
        }

        @Override
        public void setValue(CodeAssembler assembler, LogicValue value) {
            // Do nothing
        }

        @Override
        public void writeValue(CodeAssembler assembler, Consumer<LogicVariable> valueSetter) {
            // Do nothing
        }
    }
}
