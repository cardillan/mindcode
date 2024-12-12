package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class AstParameter extends AstFragment {
    private final AstIdentifier name;
    private final AstExpression value;

    public AstParameter(InputPosition inputPosition, @Nullable AstDocComment docComment,
            AstIdentifier name, AstExpression value) {
        super(inputPosition, docComment);
        this.name = name;
        this.value = value;
    }

    public AstIdentifier getName() {
        return name;
    }

    public AstExpression getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstParameter that = (AstParameter) o;
        return name.equals(that.name) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AstParameter{" +
               "name=" + name +
               ", value=" + value +
               ", docComment=" + docComment +
               '}';
    }
}
