package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.ast.AstNode;

import java.util.ArrayList;
import java.util.List;

public record AstContext(
        int level,
        AstNode node,
        AstContextType contextType,
        AstContextSubtype contextSubtype,
        AstContext parent,
        double weight,
        List<AstContext> children
) {

    public static AstContext createRootNode() {
        return new AstContext(0, null, AstContextType.ROOT, AstContextSubtype.BODY,
                null, 1.0, new ArrayList<>());
    }

    public AstContext childNode(AstNode node, AstContextType contextType) {
        AstContext child = new AstContext(level + 1, node, contextType, node.getContextSubype(),
                this, weight, new ArrayList<>());
        children.add(child);
        return child;
    }

    public AstContext subtype(AstContextSubtype contextSubtype, double multiplier) {
        AstContext child = new AstContext(level, node, contextType, contextSubtype,
                this, weight * multiplier, new ArrayList<>());
        children.add(child);
        return child;
    }

    /**
     * This context matches another context when they're the same instance, or when this context is a direct child of
     * the other context.
     *
     * @param other context to match
     * @return true if this context matches the other context
     */
    public boolean matches(AstContext other) {
        return this == other || (parent != null && parent.matches(other));
    }

    public AstContext findContextOfType(AstContextType contextType) {
        AstContext current = this;
        while (current != null) {
            if (current.contextType == contextType) {
                return current;
            }
            current = current.parent;
        }

        return null;
    }

    public AstContext findTopContextOfType(AstContextType contextType) {
        AstContext current = this;
        AstContext found = null;
        while (current != null) {
            if (current.contextType == contextType) {
                found = current;
            }
            current = current.parent;
        }

        return found;
    }

    @Override
    public String toString() {
        return "AstContext{" +
                "level=" + level +
                ", node=" + node +
                ", contextType=" + contextType +
                ", contextSubtype=" + contextSubtype +
                ", weight=" + weight +
                '}';
    }
}
