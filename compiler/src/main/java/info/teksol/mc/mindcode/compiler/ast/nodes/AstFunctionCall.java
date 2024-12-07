package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
@AstNode
public class AstFunctionCall extends AstExpression {
    private final @Nullable AstExpression object;
    private final AstIdentifier identifier;
    private final List<AstFunctionArgument> arguments;

    public AstFunctionCall(SourcePosition sourcePosition, @Nullable AstExpression object, AstIdentifier identifier,
            List<AstFunctionArgument> arguments) {
        super(sourcePosition, children(list(object, identifier), arguments));
        this.object = object;
        this.identifier = Objects.requireNonNull(identifier);
        this.arguments = Objects.requireNonNull(arguments);
    }

    public @Nullable AstExpression getObject() {
        return object;
    }

    public boolean hasObject() {
        return object != null;
    }

    public AstIdentifier getIdentifier() {
        return identifier;
    }

    public String getFunctionName() {
        return identifier.getName();
    }

    public List<AstFunctionArgument> getArguments() {
        return arguments;
    }

    public AstFunctionArgument getArgument(int index) {
        return arguments.get(index);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstFunctionCall that = (AstFunctionCall) o;
        return Objects.equals(object, that.object) && identifier.equals(that.identifier) && arguments.equals(that.arguments);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(object);
        result = 31 * result + identifier.hashCode();
        result = 31 * result + arguments.hashCode();
        return result;
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.CALL;
    }
}
