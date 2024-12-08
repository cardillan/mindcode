package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AstOperatorIncDec extends AstBaseMindcodeNode {
    private final @NotNull Type type;
    private final @NotNull Operation operation;
    private final @NotNull AstMindcodeNode operand;

    public AstOperatorIncDec(@NotNull InputPosition inputPosition, @NotNull Type type, @NotNull Operation operation,
            @NotNull AstMindcodeNode operand) {
        super(inputPosition);
        this.type = Objects.requireNonNull(type);
        this.operation = Objects.requireNonNull(operation);
        this.operand = Objects.requireNonNull(operand);
    }

    public @NotNull Type getType() {
        return type;
    }

    public @NotNull Operation getOperation() {
        return operation;
    }

    public @NotNull AstMindcodeNode getOperand() {
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
