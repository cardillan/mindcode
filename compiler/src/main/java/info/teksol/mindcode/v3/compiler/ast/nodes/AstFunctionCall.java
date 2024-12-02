package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AstFunctionCall extends AstBaseMindcodeNode {
    private final AstMindcodeNode object;
    private final @NotNull AstIdentifier functionName;
    private final @NotNull AstFunctionArgumentList arguments;

    public AstFunctionCall(InputPosition inputPosition, AstMindcodeNode object, @NotNull AstIdentifier functionName,
            @NotNull AstFunctionArgumentList arguments) {
        super(inputPosition);
        this.object = object;
        this.functionName = Objects.requireNonNull(functionName);
        this.arguments = Objects.requireNonNull(arguments);
    }

    public AstMindcodeNode getObject() {
        return object;
    }

    public boolean hasObject() {
        return object != null;
    }

    public @NotNull AstIdentifier getFunctionName() {
        return functionName;
    }

    public @NotNull AstFunctionArgumentList getArguments() {
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
