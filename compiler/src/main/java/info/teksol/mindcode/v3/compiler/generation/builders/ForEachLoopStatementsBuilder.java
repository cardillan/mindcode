package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.generated.ast.visitors.AstForEachLoopStatementVisitor;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstExpression;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstForEachLoopStatement;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstLoopIterator;
import info.teksol.mindcode.v3.compiler.generation.CodeBuilder;
import info.teksol.mindcode.v3.compiler.generation.CodeGenerator;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static info.teksol.mindcode.logic.LogicVoid.VOID;

@NullMarked
public class ForEachLoopStatementsBuilder extends AbstractLoopBuilder implements AstForEachLoopStatementVisitor<NodeValue> {

    public ForEachLoopStatementsBuilder(CodeGeneratorContext context, CodeGenerator.AstNodeVisitor mainNodeVisitor) {
        super(context, mainNodeVisitor);
    }

    @Override
    public NodeValue visitForEachLoopStatement(AstForEachLoopStatement node) {
        if (!node.getValues().isEmpty()) {
            new ForEachLoopBuilder(node).build();
        }
        return VOID;
    }

    private class ForEachLoopBuilder extends StructureBuilder<AstForEachLoopStatement> {
        private final List<NodeValue> values;
        private final List<Iterator> iterators;
        private final LogicLabel bodyLabel = nextLabel();
        private final LogicLabel marker = nextMarker();
        private final LogicVariable nextAddress = nextTemp();
        private final LoopLabels loopLabels;

        // Used to traverse the value list during leading/trailing code creation.
        private int leadingIndex = 0;
        private int trailingIndex = 0;

        public ForEachLoopBuilder(AstForEachLoopStatement node) {
            super(node);
            values = new ArrayList<>(node.getValues().size() + variables.getVarargs().size());
            iterators = node.getIterators().stream().map(this::processIterator).toList();

            // Prepare list of values for the loop, including varargs if any
            for (AstExpression expression : node.getValues()) {
                if (variables.isVarargParameter(expression)) {
                    values.addAll(variables.getVarargs());
                } else {
                    values.add(new DeferredNodeValue(expression));
                }
            }

            // Compute total values to process in the loop (rectifies incorrect list size)
            if (values.size() % iterators.size() != 0) {
                error(node, "The number of values in the list (%d) must be an integer multiple of the number of iterators (%d).",
                        values.size(), iterators.size());

                // Fill it up using inactive values so that the loop can get compiled
                while (values.size() % iterators.size() != 0) {
                    values.add(INACTIVE_VALUE);
                }
            }

            loopLabels = enterLoop(node);
        }

        private Iterator processIterator(AstLoopIterator it) {
            return new Iterator(it.hasOutModifier(), resolveLValue(it.getIterator()));
        }

        private void build() {
            while (leadingIndex < values.size()) {
                createIteration();
            }

            // Finalizing code
            // TODO This should probably be marked as AstSubcontextType.FLOW_CONTROL
            codeBuilder.createLabel(loopLabels.doneLabel());
            codeBuilder.clearSubcontextType();
            exitLoop(node);
        }

        private void createIteration() {
            // Code which sets up variables for the next iteration
            // Multiplier is set to 1: all instructions execute exactly once
            codeBuilder.setSubcontextType(AstSubcontextType.ITR_LEADING, 1.0);

            // Setting the iterator address first. It is possible to use `continue` in a value expression
            // (why, oh why?), in which case the next iteration is performed.
            LogicLabel nextValueLabel = nextLabel();
            codeBuilder.createSetAddress(nextAddress, nextValueLabel);

            // Copy values from the list to iterators
            for (Iterator iterator : iterators) {
                NodeValue value = values.get(leadingIndex++);
                iterator.setValue(value.getValue(codeBuilder));
            }

            if (leadingIndex < values.size()) {
                // This isn't a last iteration: jump to the loop body
                codeBuilder.createJumpUnconditional(bodyLabel);
            } else {
                // The last iteration: continue to the loop body directly
                createBody();
            }

            // The trailing part of the iteration setup. For the last iteration the code is generated after
            // the loop body, so that the program flow naturally exits the loop after the last iteration.
            codeBuilder.setSubcontextType(AstSubcontextType.ITR_TRAILING, 1.0);
            codeBuilder.createGotoLabel(nextValueLabel, marker);

            // Copy iterator values back to the array - only for `out` iterators
            for (Iterator iterator : iterators) {
                if (iterator.out) {
                    values.get(trailingIndex).setValue(codeBuilder, iterator.getValue());
                }
                trailingIndex++;
            }
        }

        private void createBody() {
            // This is the last iteration. We'll output the loop body directly here.
            codeBuilder.setSubcontextType(AstSubcontextType.BODY, LOOP_REPETITIONS);
            codeBuilder.createLabel(bodyLabel);

            visitStatements(node.getBody());

            // Label for continue statements
            codeBuilder.createLabel(loopLabels.continueLabel());

            // Jumps to the iteration trailing block
            // On the last iteration, this jumps right to the next statement, but it can't be avoided
            codeBuilder.setSubcontextType(AstSubcontextType.FLOW_CONTROL, LOOP_REPETITIONS);
            codeBuilder.createGoto(nextAddress, marker);
        }
    }

    /// A NodeValue based on AstExpression which is only evaluated when actually accessed.
    private class DeferredNodeValue implements NodeValue {
        private final AstExpression expression;
        private @Nullable NodeValue nodeValue;
        private @Nullable NodeValue resolvedLValue;

        public DeferredNodeValue(AstExpression expression) {
            this.expression = expression;
        }

        private NodeValue value() {
            if (nodeValue == null) {
                nodeValue = visit(expression);
            }
            return nodeValue;
        }

        private NodeValue verifyLValue() {
            if (resolvedLValue == null) {
                resolvedLValue = resolveLValue(expression, value());
            }
            return resolvedLValue;
        }

        @Override
        public boolean isLvalue() {
            return value().isLvalue();
        }

        @Override
        public LogicValue getValue(CodeBuilder codeBuilder) {
            return value().getValue(codeBuilder);
        }

        @Override
        public void setValue(CodeBuilder codeBuilder, LogicValue value) {
            verifyLValue().setValue(codeBuilder, value);
        }

        @Override
        public void writeValue(CodeBuilder codeBuilder, Consumer<LogicVariable> valueSetter) {
            verifyLValue().writeValue(codeBuilder, valueSetter);
        }
    }

    ///  Represents an iterator variable in the loop
    private final class Iterator {
        public final boolean out;
        private final NodeValue var;

        private Iterator(boolean out, NodeValue var) {
            this.out = out;
            this.var = var;
        }

        void setValue(LogicValue value) {
            var.setValue(codeBuilder, value);
        }

        LogicValue getValue() {
            return var.getValue(codeBuilder);
        }
    }

    private static final InactiveValue INACTIVE_VALUE = new InactiveValue();
    private static class InactiveValue implements NodeValue {
        @Override
        public boolean isLvalue() {
            return true;
        }

        @Override
        public LogicValue getValue(CodeBuilder codeBuilder) {
            return LogicVariable.INVALID;
        }

        @Override
        public void setValue(CodeBuilder codeBuilder, LogicValue value) {
            // Do nothing
        }

        @Override
        public void writeValue(CodeBuilder codeBuilder, Consumer<LogicVariable> valueSetter) {
            // Do nothing
        }
    }
}
