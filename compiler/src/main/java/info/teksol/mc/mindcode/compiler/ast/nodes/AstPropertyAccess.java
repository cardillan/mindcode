package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode(printFlat = true)
public class AstPropertyAccess extends AstExpression {
    private final AstExpression object;
    private final AstBuiltInIdentifier property;

    public AstPropertyAccess(SourcePosition sourcePosition, AstExpression object, AstBuiltInIdentifier property) {
        super(sourcePosition, children(object, property));
        this.object = Objects.requireNonNull(object);
        this.property = Objects.requireNonNull(property);
    }

    public AstExpression getObject() {
        return object;
    }

    public AstBuiltInIdentifier getProperty() {
        return property;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstPropertyAccess that = (AstPropertyAccess) o;
        return object.equals(that.object) && property.equals(that.property);
    }

    @Override
    public int hashCode() {
        int result = object.hashCode();
        result = 31 * result + property.hashCode();
        return result;
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.PROPERTY;
    }
}
