package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.generated.ast.visitors.AstAssignmentVisitor;
import info.teksol.generated.ast.visitors.AstOperatorIncDecVisitor;
import info.teksol.mindcode.logic.LogicBoolean;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Operation;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstAssignment;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstExpression;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstLiteralDecimal;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstOperatorIncDec;
import info.teksol.mindcode.v3.compiler.generation.CodeGenerator;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

import static info.teksol.mindcode.logic.LogicNull.NULL;
import static info.teksol.mindcode.logic.LogicVoid.VOID;

@NullMarked
public class AssignmentsBuilder extends AbstractBuilder implements AstAssignmentVisitor<NodeValue>, AstOperatorIncDecVisitor<NodeValue> {
    public AssignmentsBuilder(CodeGeneratorContext context, CodeGenerator.AstNodeVisitor mainNodeVisitor) {
        super(context, mainNodeVisitor);
    }

    @Override
    public NodeValue visitAssignment(AstAssignment node) {
        return applyOperation(node.getTarget(), node.getValue(), node.getOperation(), false);
    }

    @Override
    public NodeValue visitOperatorIncDec(AstOperatorIncDec node) {
        // The other operand for increment/decrement is literal one. We're creating it here so that it gets
        // a valid input position.
        return applyOperation(node.getOperand(),
                new AstLiteralDecimal(node.getOperand().inputPosition(), "1"),
                node.getOperation(),
                node.getType() == AstOperatorIncDec.Type.POSTFIX);
    }

    /// Handles all possible cases of assignments: simple assignments, compound assignments and prefix/postfix operators
    /// (prefix operator can be considered a special case of compound assignment).
    ///
    /// If the resulting value is passed up in a new temporary variable (may happen for volatile variables,
    /// for example), the variable is properly registered in the parent node context.
    ///
    /// @return an instance holding the prior and current value of the modified target (prior is used for
    ///         postfix operators only)
    private NodeValue applyOperation(AstExpression targetNode, AstExpression valueNode, @Nullable Operation operation,
            boolean returnPriorValue) {
        // We want to visit target first, so that heap variables are allocated left-to-right.
        NodeValue target = resolveLValue(targetNode);
        NodeValue eval = visit(valueNode);
        if (!target.isLvalue()) {
            return NULL;
        }

        // Obtains a LogicValue representing the assignment rvalue
        // For external variables this is the temporary variable holding the value read from memory
        LogicValue logicValue = eval.getValue(codeBuilder);
        LogicValue rvalue;

        if (eval == VOID) {
            warn(valueNode, "Expression doesn't have any value. Using value-less expressions in assignments is deprecated.");
            rvalue = NULL;
        } else if (logicValue.isVolatile()) {
            // The variable which keeps the value is volatile. Preserve the value.
            LogicVariable tmp = nextTemp();
            codeBuilder.createSet(tmp, logicValue);
            rvalue = tmp;
        } else {
            rvalue = logicValue;
        }

        LogicValue result;

        if (operation == null) {
            // In direct assignment, prior value of the target is unavailable
            target.setValue(codeBuilder, rvalue);

            // Use the variable we've just assigned to as the result, unless it is volatile - in that case,
            // use the r-value that was assigned
            result = target instanceof LogicVariable var && !var.isVolatile() ? var : rvalue;
        } else if (returnPriorValue) {
            // Evaluate the target and store it as a result
            // We're using a regular temp, not a node result temp, because the variable will be registered
            // in the parent context below
            // TODO Specific optimization for postfix operators - swap assignment and increment
            //      Also in loops
            LogicValue left = target.getValue(codeBuilder);
            LogicVariable tmp = nextTemp();
            codeBuilder.createSet(tmp, left);
            result = tmp;

            Consumer<LogicVariable> valueSetter = createValueSetter(operation, left, rvalue);
            target.writeValue(codeBuilder, valueSetter);
        } else {
            // Compound assignment modifies the target. Current value is the left operator.
            LogicValue left = target.getValue(codeBuilder);
            Consumer<LogicVariable> valueSetter = createValueSetter(operation, left, rvalue);

            if (target instanceof LogicVariable var && !var.isVolatile()) {
                // We're assigning to a non-volatile variable: do the operation in one step and use the variable as result
                target.writeValue(codeBuilder, valueSetter);

                // Evaluating a non-volatile variable is a no-op effort
                result = target.getValue(codeBuilder);
            } else {
                // Assigning to a volatile variable or a complex storage: compute the value first, then use it as a result
                // We're using a regular temp, not a node result temp, because the variable will be registered
                // in the parent context below
                LogicVariable tmp = nextTemp();
                valueSetter.accept(tmp);
                target.setValue(codeBuilder, tmp);
                result = tmp;
            }
        }

        if (result instanceof LogicVariable variable && !variable.isUserVariable()) {
            // result holds the value of this node -- when it is a variable, it needs to be registered
            // in parent node context: the variable was (possibly) created in a child context, but will
            // be accessed in parent context.
            variables.registerParentNodeVariable(variable);
        }

        return result;
    }

    private Consumer<LogicVariable> createValueSetter(Operation operation, LogicValue left, LogicValue rvalue) {
        if (operation == Operation.BOOLEAN_OR) {
            return variable -> {
                final LogicVariable tmp = nextTemp();
                codeBuilder.createOp(Operation.BOOLEAN_OR, tmp, left, rvalue);
                // Ensure the result is 0 or 1
                codeBuilder.createOp(Operation.NOT_EQUAL, variable, tmp, LogicBoolean.FALSE);
            };
        } else {
            return variable -> codeBuilder.createOp(operation, variable, left, rvalue);
        }
    }

}