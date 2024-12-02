package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class AstFunctionArgumentList extends AstBaseMindcodeNode {
    private final @NotNull List<@NotNull AstFunctionArgument> arguments;

    public AstFunctionArgumentList(InputPosition inputPosition, @NotNull List<@NotNull AstFunctionArgument> arguments) {
        super(inputPosition);
        this.arguments = Objects.requireNonNull(arguments);
    }

    public @NotNull List<@NotNull AstFunctionArgument> getArguments() {
        return arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstFunctionArgumentList that = (AstFunctionArgumentList) o;
        return arguments.equals(that.arguments);
    }

    @Override
    public int hashCode() {
        return arguments.hashCode();
    }

    @Override
    public String toString() {
        return "AstFunctionArgumentList{" +
               "arguments=" + arguments +
               '}';
    }
}
