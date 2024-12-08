package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AstConstant extends AstDocumentedNode {
    private final @NotNull AstIdentifier name;
    private final @NotNull AstMindcodeNode value;

    public AstConstant(InputPosition inputPosition, @Nullable AstDocComment docComment,
            @NotNull AstIdentifier name, @NotNull AstMindcodeNode value) {
        super(inputPosition, docComment);
        this.name = name;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AstConstant that = (AstConstant) o;
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
        return "AstConstant{" +
               "name=" + name +
               ", value=" + value +
               '}';
    }
}
