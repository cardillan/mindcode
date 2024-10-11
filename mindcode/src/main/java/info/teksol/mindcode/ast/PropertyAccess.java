package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputFile;
import info.teksol.mindcode.compiler.generator.AstContextType;
import org.antlr.v4.runtime.Token;

import java.util.Objects;

public class PropertyAccess extends BaseAstNode {
    private final AstNode target;
    private final AstNode property;

    PropertyAccess(Token startToken, InputFile inputFile, AstNode target, AstNode property) {
        super(startToken, inputFile, target, property);
        this.target = target;
        this.property = property;
    }

    public AstNode getTarget() {
        return target;
    }

    public AstNode getProperty() {
        return property;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyAccess that = (PropertyAccess) o;
        return Objects.equals(target, that.target) &&
                Objects.equals(property, that.property);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, property);
    }

    @Override
    public String toString() {
        return "PropertyAccess{" +
                "target=" + target +
                ", property='" + property + '\'' +
                '}';
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.PROPERTY;
    }
}
