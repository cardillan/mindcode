package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
@AstNode
public class AstVariablesDeclaration extends AstExpression {
    private final List<AstVariableModifier> modifiers;
    private final List<AstVariableSpecification> variables;

    public AstVariablesDeclaration(SourcePosition sourcePosition, List<AstVariableModifier> modifiers,
            List<AstVariableSpecification> variables) {
        super(sourcePosition, children(modifiers, variables));
        this.modifiers = modifiers;
        this.variables = variables;
    }

    @Override
    public AstNodeScope getScope() {
        return AstNodeScope.NONE;
    }

    @Override
    public AstNodeScope getScopeRestriction() {
        return AstNodeScope.NONE;
    }

    public List<AstVariableModifier> getModifiers() {
        return modifiers;
    }

    public List<AstVariableSpecification> getVariables() {
        return variables;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstVariablesDeclaration that = (AstVariablesDeclaration) o;
        return modifiers.equals(that.modifiers) && variables.equals(that.variables);
    }

    @Override
    public int hashCode() {
        int result = modifiers.hashCode();
        result = 31 * result + variables.hashCode();
        return result;
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.INIT;
    }
}