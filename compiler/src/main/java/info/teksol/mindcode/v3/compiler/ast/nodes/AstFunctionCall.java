package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

@NullMarked
public class AstFunctionCall extends AstExpression {
    private final @Nullable AstExpression object;
    private final AstIdentifier functionName;
    private final List< AstFunctionArgument> arguments;

    public AstFunctionCall(InputPosition inputPosition, @Nullable AstExpression object, AstIdentifier functionName,
            List< AstFunctionArgument> arguments) {
        super(inputPosition);
        this.object = object;
        this.functionName = Objects.requireNonNull(functionName);
        this.arguments = Objects.requireNonNull(arguments);
    }

    public @Nullable AstExpression getObject() {
        return object;
    }

    public boolean hasObject() {
        return object != null;
    }

    public AstIdentifier getFunctionName() {
        return functionName;
    }

    public List< AstFunctionArgument> getArguments() {
        return arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstFunctionCall that = (AstFunctionCall) o;
        return Objects.equals(object, that.object) && functionName.equals(that.functionName) && arguments.equals(that.arguments);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(object);
        result = 31 * result + functionName.hashCode();
        result = 31 * result + arguments.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AstFunctionCall{" +
               "object=" + object +
               ", functionName=" + functionName +
               ", arguments=" + arguments +
               '}';
    }
}
