package info.teksol.mindcode.ast;

import org.antlr.v4.runtime.Token;

import java.util.List;
import java.util.Objects;

public class Control extends ControlBlockAstNode {
    private final AstNode target;
    private final String property;
    private final List<AstNode> params;

    Control(Token startToken, AstNode target, String property, List<AstNode> params) {
        super(startToken, params, target);
        this.target = target;
        this.property = property;
        this.params = params;
    }

    public AstNode getTarget() {
        return target;
    }

    public String getProperty() {
        return property;
    }

    public List<AstNode> getParams() {
        return params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Control control = (Control) o;
        return Objects.equals(target, control.target) &&
                Objects.equals(property, control.property) &&
                Objects.equals(params, control.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, property, params);
    }

    @Override
    public String toString() {
        return "Control{" +
                "target=" + target +
                ", property='" + property + '\'' +
                ", params=" + params +
                '}';
    }
}
