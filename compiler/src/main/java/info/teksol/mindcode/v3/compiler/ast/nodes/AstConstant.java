package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@AstNode(printFlat = true)
public class AstConstant extends AstDeclaration {
    private final AstIdentifier name;
    private final AstExpression value;

    public AstConstant(InputPosition inputPosition, @Nullable AstDocComment docComment,
            AstIdentifier name, AstExpression value) {
        super(inputPosition, children(name, value), docComment);
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

        AstConstant that = (AstConstant) o;
        return name.equals(that.name) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

}
