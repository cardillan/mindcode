package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AstMemberAccess extends AstBaseMindcodeNode {
    private final @NotNull AstMindcodeNode object;
    private final @NotNull AstMindcodeNode property;

    public AstMemberAccess(InputPosition inputPosition, @NotNull AstMindcodeNode object, @NotNull AstMindcodeNode property) {
        super(inputPosition);
        this.object = Objects.requireNonNull(object);
        this.property = Objects.requireNonNull(property);
    }

    public @NotNull AstMindcodeNode getObject() {
        return object;
    }

    public @NotNull AstMindcodeNode getProperty() {
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

    @Override
    public String toString() {
        return "AstMemberAccess{" +
               "object=" + object +
               ", property=" + property +
               '}';
    }
}
