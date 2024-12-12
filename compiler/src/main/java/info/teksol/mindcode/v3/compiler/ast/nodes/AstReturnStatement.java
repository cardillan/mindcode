package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class AstReturnStatement extends AstStatement {
    private final @Nullable AstExpression returnValue;

    public AstReturnStatement(InputPosition inputPosition, @Nullable AstExpression returnValue) {
        super(inputPosition);
        this.returnValue = returnValue;
    }

    public @Nullable AstExpression getReturnValue() {
        return returnValue;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstReturnStatement that = (AstReturnStatement) o;
        return Objects.equals(returnValue, that.returnValue);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(returnValue);
    }

    @Override
    public String toString() {
        return "AstReturnStatement{" +
               "returnValue=" + returnValue +
               '}';
    }
}
