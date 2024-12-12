package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode(printFlat = true)
public class AstReturnStatement extends AstStatement {
    private final @Nullable AstExpression returnValue;

    public AstReturnStatement(InputPosition inputPosition, @Nullable AstExpression returnValue) {
        super(inputPosition, children(returnValue));
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

}
