package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
@AstNode(printFlat = true)
public class AstMemberAccess extends AstExpression {
    private final AstExpression object;
    private final AstExpression property;

    public AstMemberAccess(InputPosition inputPosition, AstExpression object, AstExpression property) {
        super(inputPosition, children(object, property));
        this.object = Objects.requireNonNull(object);
        this.property = Objects.requireNonNull(property);
    }

    public AstExpression getObject() {
        return object;
    }

    public AstExpression getProperty() {
        return property;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstMemberAccess that = (AstMemberAccess) o;
        return object.equals(that.object) && property.equals(that.property);
    }

    @Override
    public int hashCode() {
        int result = object.hashCode();
        result = 31 * result + property.hashCode();
        return result;
    }

}
