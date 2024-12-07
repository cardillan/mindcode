package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.logic.arguments.Operation;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode(printFlat = true)
public class AstOperatorUnary extends AstExpression {
    private final Operation operation;
    private final AstExpression operand;

    public AstOperatorUnary(SourcePosition sourcePosition, Operation operation,
            AstExpression operand) {
        super(sourcePosition, children(operand));
        this.operation = Objects.requireNonNull(operation);
        this.operand = Objects.requireNonNull(operand);
    }

    public Operation getOperation() {
        return operation;
    }

    public AstExpression getOperand() {
        return operand;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstOperatorUnary that = (AstOperatorUnary) o;
        return operation == that.operation && operand.equals(that.operand);
    }

    @Override
    public int hashCode() {
        int result = operation.hashCode();
        result = 31 * result + operand.hashCode();
        return result;
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.OPERATOR;
    }
}