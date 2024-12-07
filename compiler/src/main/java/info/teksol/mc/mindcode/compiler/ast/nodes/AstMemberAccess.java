package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode(printFlat = true)
public class AstMemberAccess extends AstExpression {
    private final AstExpression object;
    private final AstIdentifier member;

    public AstMemberAccess(SourcePosition sourcePosition, AstExpression object, AstIdentifier member) {
        super(sourcePosition, children(object, member));
        this.object = Objects.requireNonNull(object);
        this.member = Objects.requireNonNull(member);
    }

    public AstExpression getObject() {
        return object;
    }

    public AstIdentifier getMember() {
        return member;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstMemberAccess that = (AstMemberAccess) o;
        return object.equals(that.object) && member.equals(that.member);
    }

    @Override
    public int hashCode() {
        int result = object.hashCode();
        result = 31 * result + member.hashCode();
        return result;
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.PROPERTY;
    }
}
