package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.logic.arguments.Operation;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode(printFlat = true)
public class AstOperatorIncDec extends AstExpression {
    private final Type type;
    private final Operation operation;
    private final AstExpression operand;

    public AstOperatorIncDec(SourcePosition sourcePosition, Type type, Operation operation,
            AstExpression operand) {
        super(sourcePosition, children(operand));
        this.type = Objects.requireNonNull(type);
        this.operation = Objects.requireNonNull(operation);
        this.operand = Objects.requireNonNull(operand);
    }

    public Type getType() {
        return type;
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

        AstOperatorIncDec that = (AstOperatorIncDec) o;
        return type == that.type && operation == that.operation && operand.equals(that.operand);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + operation.hashCode();
        result = 31 * result + operand.hashCode();
        return result;
    }

    public enum Type { PREFIX, POSTFIX }
}
