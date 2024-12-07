package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@AstNode(printFlat = true)
public class AstConstant extends AstDeclaration {
    private final AstIdentifier name;
    private final AstExpression value;

    public AstConstant(SourcePosition sourcePosition, @Nullable AstDocComment docComment,
            AstIdentifier name, AstExpression value) {
        super(sourcePosition, children(name, value), docComment);
        this.name = name;
        this.value = value;
    }

    public AstIdentifier getName() {
        return name;
    }

    public AstExpression getValue() {
        return value;
    }

    public String getConstantName() {
        return name.getName();
    }

    @Override
    public boolean equals(@Nullable Object o) {
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
