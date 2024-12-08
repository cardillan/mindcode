package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class AstFunctionCall extends AstExpression {
    private final @Nullable AstExpression object;
    private final @NotNull AstIdentifier functionName;
    private final @NotNull List<@NotNull AstFunctionArgument> arguments;

    public AstFunctionCall(@NotNull InputPosition inputPosition, @Nullable AstExpression object, @NotNull AstIdentifier functionName,
            @NotNull List<@NotNull AstFunctionArgument> arguments) {
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

    public @NotNull AstIdentifier getFunctionName() {
        return functionName;
    }

    public @NotNull List<@NotNull AstFunctionArgument> getArguments() {
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
