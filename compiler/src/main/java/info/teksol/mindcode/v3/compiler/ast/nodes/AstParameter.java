package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AstParameter extends AstDocumentedNode {
    private final @NotNull AstIdentifier name;
    private final @NotNull AstMindcodeNode value;

    public AstParameter(@NotNull InputPosition inputPosition, @Nullable AstDocComment docComment,
            @NotNull AstIdentifier name, @NotNull AstMindcodeNode value) {
        super(inputPosition, docComment);
        this.name = name;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AstParameter that = (AstParameter) o;
        return name.equals(that.name) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AstParameter{" +
               "name=" + name +
               ", value=" + value +
               '}';
    }
}
