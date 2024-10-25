package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.compiler.generator.AstContextType;

import java.util.List;
import java.util.Objects;

public class Control extends BaseAstNode {
    private final AstNode target;
    private final String property;
    private final List<FunctionArgument> arguments;

    Control(InputPosition inputPosition, AstNode target, String property, List<FunctionArgument> arguments) {
        super(inputPosition, arguments, target);
        this.target = target;
        this.property = property;
        this.arguments = arguments;
    }

    public AstNode getTarget() {
        return target;
    }

    public String getProperty() {
        return property;
    }

    public List<FunctionArgument> getArguments() {
        return arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Control control = (Control) o;
        return Objects.equals(target, control.target) &&
                Objects.equals(property, control.property) &&
                Objects.equals(arguments, control.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, property, arguments);
    }

    @Override
    public String toString() {
        return "Control{" +
                "target=" + target +
                ", property='" + property + '\'' +
                ", arguments=" + arguments +
                '}';
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.PROPERTY;
    }
}
