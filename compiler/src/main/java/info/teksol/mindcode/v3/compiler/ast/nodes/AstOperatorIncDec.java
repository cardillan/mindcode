package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class AstOperatorIncDec extends AstExpression {
    private final Type type;
    private final Operation operation;
    private final AstExpression operand;

    public AstOperatorIncDec(InputPosition inputPosition, Type type, Operation operation,
            AstExpression operand) {
        super(inputPosition);
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
    public boolean equals(Object o) {
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

    @Override
    public String toString() {
        return "AstOperatorIncDec{" +
               "type=" + type +
               ", operation=" + operation +
               ", operand=" + operand +
               '}';
    }

    public enum Type { PREFIX, POSTFIX }
    public enum Operation { INCREMENT, DECREMENT }
}
