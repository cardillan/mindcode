package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode(printFlat = true)
public class AstReturnStatement extends AstStatement {
    private final @Nullable AstExpression returnValue;

    public AstReturnStatement(SourcePosition sourcePosition, @Nullable AstExpression returnValue) {
        super(sourcePosition, children(returnValue));
        this.returnValue = returnValue;
    }

    public @Nullable AstExpression getReturnValue() {
        return returnValue;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstReturnStatement that = (AstReturnStatement) o;
        return Objects.equals(returnValue, that.returnValue);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(returnValue);
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.RETURN;
    }
}
