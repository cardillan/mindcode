package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.generated.ast.visitors.AstAssignmentVisitor;
import info.teksol.mc.generated.ast.visitors.AstOperatorIncDecVisitor;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.WARN;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstAssignment;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstExpression;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstLiteralDecimal;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstOperatorIncDec;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

import static info.teksol.mc.mindcode.logic.arguments.LogicNull.NULL;
import static info.teksol.mc.mindcode.logic.arguments.LogicVoid.VOID;

@NullMarked
public class AssignmentsBuilder extends AbstractBuilder implements AstAssignmentVisitor<ValueStore>, AstOperatorIncDecVisitor<ValueStore> {

    public AssignmentsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public ValueStore visitAssignment(AstAssignment node) {
        return applyOperation(node.getTarget(), node.getValue(), node.getOperation(), false);
    }

    @Override
    public ValueStore visitOperatorIncDec(AstOperatorIncDec node) {
        // The other operand for increment/decrement is literal one. We're creating it here so that it gets
        // a valid input position.
        return applyOperation(node.getOperand(),
                new AstLiteralDecimal(node.getOperand().sourcePosition(), "1"),
                node.getOperation(),
                node.getType() == AstOperatorIncDec.Type.POSTFIX);
    }

    /// Handles all possible cases of assignments: simple assignments, compound assignments and prefix/postfix operators
    /// (prefix operator can be considered a special case of compound assignment).
    ///
    /// If the resulting value is passed up in a new temporary variable (may happen for volatile variables,
    /// for example), the variable is properly registered in the parent node context.
    ///
    /// @param returnPriorValue indicates the prior value of the target is the result of this node (basically only
    ///                         true for postfix operators)
    /// @return a ValueStore instance holding the result of this node
    private ValueStore applyOperation(AstExpression targetNode, AstExpression valueNode, @Nullable Operation operation,
            boolean returnPriorValue) {
        // We want to visit target first, so that heap variables are allocated left-to-right.
        ValueStore target = resolveLValue(targetNode);
        ValueStore eval = evaluate(valueNode);
        if (!target.isLvalue()) {
            return NULL;
        }

        // Obtains a LogicValue representing the assignment rvalue
        // For external variables this is the temporary variable holding the value read from memory
        LogicValue logicValue = eval.getValue(assembler);
        LogicValue rvalue;

        if (eval == VOID) {
            warn(valueNode, WARN.VOID_EXPRESSION_DEPRECATED);
            rvalue = NULL;
        } else if (logicValue.isVolatile()) {
            // The variable which keeps the value is volatile. Preserve the value.
            LogicVariable tmp = nextTemp();
            assembler.createSet(tmp, logicValue);
            rvalue = tmp;
        } else {
            rvalue = logicValue;
        }

        LogicValue result;

        if (operation == null) {
            // In direct assignment, prior value of the target is unavailable
            target.setValue(assembler, rvalue);

            // Use the variable we've just assigned to as the result, unless it is volatile - in that case,
            // use the r-value that was assigned
            result = target instanceof LogicVariable var && !var.isVolatile() ? var : rvalue;
        } else if (returnPriorValue) {
            // No need to check for string operands
            // The grammar won't allow expressions like `"a"++`.

            // Evaluate the target and store it as a result
            // We're using a regular temp, not a node result temp, because the variable will be registered
            // in the parent context below
            // TODO Specific optimization for postfix operators - swap assignment and increment
            //      Also in loops
            LogicValue left = target.getValue(assembler);
            LogicVariable tmp = nextTemp();
            assembler.createSet(tmp, left);
            result = tmp;

            Consumer<LogicVariable> valueSetter = createValueSetter(operation, left, rvalue);
            target.writeValue(assembler, valueSetter);
        } else if (rvalue instanceof LogicString) {
            error(pos(targetNode, valueNode), ERR.UNSUPPORTED_STRING_EXPRESSION);
            result = LogicVariable.INVALID;
        } else {
            // Compound assignment modifies the target. Current value is the left operator.
            LogicValue left = target.getValue(assembler);
            Consumer<LogicVariable> valueSetter = createValueSetter(operation, left, rvalue);

            if (target instanceof LogicVariable var && !var.isVolatile()) {
                // We're assigning to a non-volatile variable: do the operation in one step and use the variable as result
                target.writeValue(assembler, valueSetter);

                // Evaluating a non-volatile variable is a no-op effort
                result = target.getValue(assembler);
            } else {
                // Assigning to a volatile variable or a complex storage: compute the value first, then use it as a result
                // We're using a regular temp, not a node result temp, because the variable will be registered
                // in the parent context below
                LogicVariable tmp = nextTemp();
                valueSetter.accept(tmp);
                target.setValue(assembler, tmp);
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
                assembler.createOp(Operation.BOOLEAN_OR, tmp, left, rvalue);
                // Ensure the result is 0 or 1
                assembler.createOp(Operation.NOT_EQUAL, variable, tmp, LogicBoolean.FALSE);
            };
        } else {
            return variable -> assembler.createOp(operation, variable, left, rvalue);
        }
    }

}
