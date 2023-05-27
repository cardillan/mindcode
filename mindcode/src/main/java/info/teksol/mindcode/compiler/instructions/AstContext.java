package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.ast.AstNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class AstContext {
    private final int level;
    private final AstNode node;
    private final AstContextType contextType;
    private final AstSubcontextType subcontextType;
    private final AstContext parent;
    private final double weight;
    private final List<AstContext> children = new ArrayList<>();

    public AstContext(int level, AstNode node, AstContextType contextType, AstSubcontextType subcontextType,
            AstContext parent, double weight) {
        this.level = level;
        this.node = node;
        this.contextType = contextType;
        this.subcontextType = subcontextType;
        this.parent = parent;
        this.weight = weight;
    }

    public static AstContext createRootNode() {
        return new AstContext(0, null, AstContextType.ROOT, AstSubcontextType.BASIC,
                null, 1.0);
    }

    public AstContext createChild(AstNode node, AstContextType contextType) {
        AstContext child = new AstContext(level + 1, node, contextType, node.getContextSubype(),
                this, weight);
        children.add(child);
        return child;
    }

    public AstContext createSubcontext(AstSubcontextType subcontextType, double multiplier) {
        AstContext child = new AstContext(level, node, contextType, subcontextType,
                this, weight * multiplier);
        children.add(child);
        return child;
    }

    public AstContext createCopy() {
        return new AstContext(level, node, contextType, subcontextType, parent, weight);
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

    public AstContext findSubcontext(AstSubcontextType type) {
        for (AstContext child : children) {
            if (child.subcontextType == type) {
                return child;
            }
        }
        return null;
    }

    public List<AstContext> findSubcontexts(AstSubcontextType type) {
        return children.stream().filter(c -> c.subcontextType == type).toList();
    }

    @Override
    public String toString() {
        return "AstContext{" +
                "level=" + level +
                ", node=" + node +
                ", contextType=" + contextType +
                ", subcontextType=" + subcontextType +
                ", weight=" + weight +
                '}';
    }

    public int level() {
        return level;
    }

    public AstNode node() {
        return node;
    }

    public AstContextType contextType() {
        return contextType;
    }

    public AstSubcontextType subcontextType() {
        return subcontextType;
    }

    public AstContext parent() {
        return parent;
    }

    public double weight() {
        return weight;
    }

    public List<AstContext> children() {
        return children;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AstContext) obj;
        return this.level == that.level &&
                Objects.equals(this.node, that.node) &&
                Objects.equals(this.contextType, that.contextType) &&
                Objects.equals(this.subcontextType, that.subcontextType) &&
                Objects.equals(this.parent, that.parent) &&
                Double.doubleToLongBits(this.weight) == Double.doubleToLongBits(that.weight) &&
                Objects.equals(this.children, that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, node, contextType, subcontextType, parent, weight, children);
    }

}
