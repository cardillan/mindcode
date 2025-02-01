package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.generated.ast.visitors.AstForEachLoopStatementVisitor;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstExpression;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstForEachLoopStatement;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstLoopIterator;
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static info.teksol.mc.mindcode.logic.arguments.LogicVoid.VOID;

@NullMarked
public class ForEachLoopStatementsBuilder extends AbstractLoopBuilder implements AstForEachLoopStatementVisitor<ValueStore> {

    public ForEachLoopStatementsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public ValueStore visitForEachLoopStatement(AstForEachLoopStatement node) {
        if (!node.getValues().isEmpty()) {
            new ForEachLoopBuilder(this, node).build();
        }
        return VOID;
    }

    private class ForEachLoopBuilder extends AbstractStandaloneBuilder<AstForEachLoopStatement> {
        private final List<ValueStore> values;
        private final List<Iterator> iterators;
        private final LogicLabel bodyLabel = assembler.nextLabel();
        private final LogicLabel marker = assembler.nextMarker();
        private final LogicVariable nextAddress = assembler.nextTemp();
        private final LoopLabels loopLabels;

        private int consumedValues = 0;

        public ForEachLoopBuilder(AbstractBuilder builder, AstForEachLoopStatement node) {
            super(builder, node);
            values = new ArrayList<>(node.getValues().size() + variables.getVarargs().size());
            iterators = node.getIterators().stream().map(this::processIterator).toList();

            // Prepare list of values for the loop, including varargs if any
            for (AstExpression expression : node.getValues()) {
                if (variables.isVarargParameter(expression)) {
                    values.addAll(variables.getVarargs());
                } else {
                    values.add(new DeferredValueStore(expression));
                }
            }

            loopLabels = enterLoop(node);
        }

        private void build() {
            while (!values.isEmpty()) {
                createIteration();
            }

            // For-each loops do not put done label into flow control subcontext
            // Not a bug.
            assembler.createLabel(loopLabels.breakLabel());

            if (consumedValues > 0) {
                assembler.clearSubcontextType();
            }
            exitLoop(node, loopLabels);

            // Compute total values to process in the loop (rectifies incorrect list size)
            if (consumedValues % iterators.size() != 0) {
                error(pos(node.getValues()), ERR.FOR_EACH_WRONG_NUMBER_OF_VALUES, consumedValues, iterators.size());
            }
        }

        private Iterator processIterator(AstLoopIterator it) {
            if (node.hasDeclaration()) {
                if (it.getIterator() instanceof AstIdentifier identifier) {
                    variables.createVariable(isLocalContext(), identifier, VariableScope.NODE, Set.of());
                } else {
                    // Probably can't happen due to grammar
                    error(it.getIterator(), ERR.IDENTIFIER_EXPECTED);
                }
            }
            return new Iterator(it.hasOutModifier(), resolveLValue(it.getIterator()));
        }

        private void createIteration() {
            // Code which sets up variables for the next iteration
            // Multiplier is set to 1: all instructions execute exactly once
            assembler.setSubcontextType(AstSubcontextType.ITR_LEADING, 1.0);

            // Setting the iterator address first. It is possible to use `continue` in the value list
            // (why, oh why?), in which case the next iteration is performed.
            LogicLabel nextValueLabel = assembler.nextLabel();
            assembler.createSetAddress(nextAddress, nextValueLabel);

            LinkedList<ValueStore> outputValues = new LinkedList<>();

            // Copy values from the list to iterators
            for (Iterator iterator : iterators) {
                ValueStore value = nextValue();
                iterator.setValue(value.getValue(assembler));
                if (iterator.out) {
                    outputValues.add(value);
                }
            }

            if (!values.isEmpty()) {
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
            for (Iterator iterator : iterators) {
                if (iterator.out) {
                    outputValues.removeFirst().setValue(assembler, iterator.getValue());
//                    ValueStore value = outputValues.removeFirst();
//                    if (value.isLvalue()) {
//                        value.setValue(assembler, iterator.getValue());
//                    } else if (value instanceof FunctionArgument functionArgument) {
//                        error(value.sourcePosition(), ERR.LVALUE_CANNOT_ASSIGN_TO_EXPRESSION);
//                    }
                }
            }
        }

        private ValueStore nextValue() {
            if (values.isEmpty()) {
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

    private static final InactiveValueStore INACTIVE_VALUE = new InactiveValueStore();

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
