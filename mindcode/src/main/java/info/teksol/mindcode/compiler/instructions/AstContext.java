package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.ast.AstNode;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public final class AstContext {
    private static final AtomicInteger counter = new AtomicInteger();
    public final int id;

    private final int level;
    private final AstNode node;
    private final AstContextType contextType;
    private final AstSubcontextType subcontextType;
    private final AstContext parent;
    private final double weight;
    private final List<AstContext> children;

    private AstContext(int level, AstNode node, AstContextType contextType, AstSubcontextType subcontextType,
            AstContext parent, double weight, List<AstContext> children) {
        this.id = counter.getAndIncrement();
        this.level = level;
        this.node = node;
        this.contextType = contextType;
        this.subcontextType = subcontextType;
        this.parent = parent;
        this.weight = weight;
        this.children = children;
    }

    public AstContext(int level, AstNode node, AstContextType contextType, AstSubcontextType subcontextType,
            AstContext parent, double weight) {
        this(level, node, contextType, subcontextType, parent, weight, new ArrayList<>());
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

    public Map<AstContext, AstContext> createDeepCopy() {
        Map<AstContext, AstContext> map = new IdentityHashMap<>(16);
        createDeepCopy(map, parent);
        return map;
    }

    private AstContext createDeepCopy(Map<AstContext, AstContext> map, AstContext parent) {
        AstContext copy = new AstContext(level, node, contextType, subcontextType, parent, weight);
        children.stream()
                .map(c -> c.createDeepCopy(map, copy))
                .forEachOrdered(copy.children::add);
        map.put(this, copy);
        return copy;
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

    public boolean matches(AstContextType contextType, AstSubcontextType subcontextType) {
        return this.contextType == contextType && this.subcontextType == subcontextType;
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
            if (child.node == node && child.subcontextType == type) {
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
                "id=" + id +
                ", level=" + level +
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

    public AstContext child(int index) {
        return children.get(index);
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
