package io.github.cardillan.mindcode.compiler.ast.nodes;

import io.github.cardillan.annotations.AstNode;
import io.github.cardillan.mindcode.common.SourcePosition;
import io.github.cardillan.mindcode.compiler.Modifier;
import io.github.cardillan.mindcode.compiler.astcontext.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
@AstNode
public class AstVariablesDeclaration extends AstExpression {
    private final List<AstVariableModifier> modifiers;
    private final List<AstVariableSpecification> variables;

    public AstVariablesDeclaration(SourcePosition sourcePosition, @Nullable AstDocComment docComment,
            List<AstVariableModifier> modifiers, List<AstVariableSpecification> variables) {
        super(sourcePosition, children(modifiers, variables), docComment);
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

    public boolean isConstantDeclaration() {
        return modifiers.stream().anyMatch(m -> m.getModifier() == Modifier.CONST);
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
        return AstContextType.DECLARATION;
    }
}
