package info.teksol.mc.mindcode.compiler.astcontext;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.GlobalCompilerProfile;
import info.teksol.mc.profile.LocalCompilerProfile;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

@NullMarked
public final class AstContext {
    private static final AtomicInteger counter = new AtomicInteger();
    public final int id;

    public static int getCurrentCounter() {
        return counter.get();
    }

    private final CompilerProfile profile;

    // Marks the function and the code setting up the call to the function
    private final @Nullable MindcodeFunction function;
    private final int level;
    private final @Nullable AstMindcodeNode node;
    private final AstContextType contextType;
    private final AstSubcontextType subcontextType;
    private final @Nullable AstContext parent;
    private final @Nullable AstContext safeContext;
    private double weight;
    private final List<AstContext> children;

    // For keeping track of function sizes
    private final @Nullable MindcodeFunction functionBody;
    private final int functionCopyNumber;

    private AstContext(CompilerProfile profile, @Nullable MindcodeFunction function, @Nullable AstMindcodeNode node,
            AstContextType contextType, AstSubcontextType subcontextType, @Nullable AstContext parent, double weight,
            @Nullable MindcodeFunction functionBody, int functionCopyNumber, List<AstContext> children) {
        this.id = counter.getAndIncrement();
        this.profile = profile;
        this.function = function;
        this.level = parent == null ? 0 : parent.level + 1;
        this.node = node;
        this.contextType = contextType;
        this.subcontextType = subcontextType;
        this.parent = parent;
        this.safeContext = parent != null && isSafe(contextType, subcontextType) ? parent.safeContext : this;
        this.weight = weight;
        this.functionBody = functionBody;
        this.functionCopyNumber = functionCopyNumber;
        this.children = children;
    }

    private AstContext(CompilerProfile profile, @Nullable MindcodeFunction function, @Nullable AstMindcodeNode node,
            AstContextType contextType, AstSubcontextType subcontextType, @Nullable AstContext parent, double weight,
            @Nullable MindcodeFunction functionBody, int functionCopyNumber) {
        this(profile, function, node, contextType, subcontextType, parent, weight, functionBody, functionCopyNumber, new ArrayList<>());
    }

    public static AstContext createRootNode(CompilerProfile profile) {
        return new AstContext(profile, null, null, AstContextType.ROOT,
                AstSubcontextType.BASIC, null, 1.0, null, 0);
    }

    public static AstContext createStaticRootNode() {
        return createRootNode(CompilerProfile.fullOptimizations(false));
    }

    public AstContext createChild(AstMindcodeNode node, AstContextType contextType) {
        AstContext child = new AstContext(node.getProfile(), function, node, contextType, node.getSubcontextType(),
                this, node.getProfile().getCodeWeight(), functionBody, functionCopyNumber);
        children.add(child);

        return child;
    }

    public AstContext createChild(AstMindcodeNode node, AstContextType contextType, AstSubcontextType subcontextType) {
        AstContext child = new AstContext(node.getProfile(), function, node, contextType, subcontextType,
                this, node.getProfile().getCodeWeight(), functionBody, functionCopyNumber);
        children.add(child);

        return child;
    }

    public AstContext createFunctionDeclaration(MindcodeFunction function, AstMindcodeNode node,
            AstContextType contextType, double weight) {
        AstContext child = new AstContext(profile, function, node, contextType, node.getSubcontextType(),
                this, weight, function, function.nextCopyNumber());
        children.add(child);
        return child;
    }

    public AstContext createFunctionBody(MindcodeFunction functionBody, AstMindcodeNode node, AstContextType contextType) {
        AstContext child = new AstContext(node.getProfile(), function, node, contextType, node.getSubcontextType(),
                this, node.getProfile().getCodeWeight(), functionBody, functionBody.nextCopyNumber());
        children.add(child);

        return child;
    }

    public AstContext createSubcontext(AstSubcontextType subcontextType, double weight) {
        // Subcontext always inherits compiler profile from parent context
        AstContext child = new AstContext(profile, function, node, contextType, subcontextType,
                this, weight, functionBody, functionCopyNumber);
        children.add(child);
        return child;
    }

    public AstContext createSubcontext(MindcodeFunction function, AstSubcontextType subcontextType, double weight) {
        // Subcontext always inherits compiler profile from parent context
        AstContext child = new AstContext(profile, function, node, contextType, subcontextType,
                this, weight, functionBody, functionCopyNumber);
        children.add(child);
        return child;
    }

    public AstContext createSubcontext(AstContextType contextType, AstSubcontextType subcontextType, double weight) {
        // Subcontext always inherits compiler profile from parent context
        AstContext child = new AstContext(profile, function, node, contextType, subcontextType,
                this, weight, functionBody, functionCopyNumber);
        children.add(child);
        return child;
    }

    public AstContext createRelocationContext(AstContext original) {
        AstContext child = new AstContext(original.profile, original.function, original.node, contextType, AstSubcontextType.RELOCATION,
                this, original.totalWeight() / weight, original.functionBody, original.functionCopyNumber);
        children.add(child);
        return child;
    }


    public Map<AstContext, AstContext> createDeepCopy() {
        Map<AstContext, AstContext> map = new IdentityHashMap<>(16);
        Map<MindcodeFunction, Map<Integer, Integer>> functionCopyMap = new HashMap<>(16);
        createDeepCopy(map, functionCopyMap, parent, functionBody);
        return map;
    }

    private AstContext createDeepCopy(Map<AstContext, AstContext> map, Map<MindcodeFunction, Map<Integer, Integer>> functionCopyMap,
            @Nullable AstContext parent, @Nullable MindcodeFunction originalFunction) {
        int newFunctionCopyNumber = functionBody == null || functionBody == originalFunction ? 0
                : functionCopyMap.computeIfAbsent(functionBody, _ -> new HashMap<>()).computeIfAbsent(functionCopyNumber, _ -> functionBody.nextCopyNumber());
        AstContext copy = new AstContext(profile, function, node, contextType, subcontextType, parent, weight, functionBody, newFunctionCopyNumber);
        children.stream()
                .map(c -> c.createDeepCopy(map, functionCopyMap, copy, originalFunction))
                .forEachOrdered(copy.children::add);
        map.put(this, copy);
        return copy;
    }

    public Map<AstContext, AstContext> copyChildrenTo(AstContext newParent, boolean functionInlining) {
        Map<AstContext, AstContext> map = new IdentityHashMap<>(16);
        Map<MindcodeFunction, Map<Integer, Integer>> functionCopyMap = new HashMap<>(16);
        children.stream()
                .map(c -> c.createDeepCopy(map, functionCopyMap, newParent, functionInlining ? null : functionBody))
                .forEachOrdered(newParent.children::add);
        map.put(this, newParent);
        return map;
    }

    public LocalCompilerProfile getLocalProfile() {
        return profile;
    }

    public GlobalCompilerProfile getGlobalProfile() {
        return profile;
    }

    public CompilerProfile getCompilerProfile() {
        return profile;
    }

    private static boolean isSafe(AstContextType contextType, AstSubcontextType subcontextType) {
        return contextType.safe && subcontextType.safe;
    }

    public boolean isSafe() {
        return isSafe(contextType, subcontextType);
    }

    public boolean isTopSafeContext() {
        return this == safeContext && isSafe(contextType, subcontextType);
    }

    public @Nullable AstContext getSafeContext() {
        return safeContext;
    }

    /**
     * This context belongs to another context when they're the same instance, or when this context is
     * a descendant (direct or indirect child) of the other context.
     *
     * @param other context to match
     * @return true if this context belongs to the other context
     */
    public boolean belongsTo(@Nullable AstContext other) {
        return other != null && belongsToExisting(other);
    }

    private boolean belongsToExisting(AstContext other) {
        return this == other || level >= other.level && parent != null && parent.belongsToExisting(other);
    }

    public boolean matches(AstContextType contextType) {
        return this.contextType == contextType;
    }

    public boolean matches(AstContextType... contextTypes) {
        for (AstContextType contextType : contextTypes) {
            if (this.contextType == contextType) {
                return true;
            }
        }
        return false;
    }

    public boolean matches(AstContextType contextType, AstSubcontextType subcontextType) {
        return this.contextType == contextType && this.subcontextType == subcontextType;
    }

    public boolean matches(AstSubcontextType subcontextType) {
        return this.subcontextType == subcontextType;
    }

    public boolean matches(AstSubcontextType... subcontextTypes) {
        for (AstSubcontextType subcontextType : subcontextTypes) {
            if (this.subcontextType == subcontextType) {
                return true;
            }
        }
        return false;
    }

    public boolean matchesRecursively(AstSubcontextType... subcontextTypes) {
        if (matches(subcontextTypes)) {
            return true;
        }
        return parent != null && parent.matchesRecursively(subcontextTypes);
    }

    public boolean matchesRecursively(AstContextType... contextTypes) {
        for (AstContextType contextType : contextTypes) {
            if (this.contextType == contextType) {
                return true;
            }
        }
        return parent != null && parent.matchesRecursively(contextTypes);
    }

    public Optional<AstContext> findSuperContextOfType(Predicate<AstContext> matcher) {
        AstContext current = this;
        while (current != null) {
            if (matcher.test(current)) {
                return Optional.of(current);
            }
            current = current.parent;
        }

        return Optional.empty();
    }

    public @Nullable AstContext findSuperContextOfType(AstContextType contextType) {
        return findSuperContextOfType(ctx -> ctx.matches(contextType)).orElse(null);
    }

    public @Nullable AstContext findTopContextOfType(AstContextType contextType) {
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

    public @Nullable AstContext findTopContextOfTypes(AstContextType... contextTypes) {
        EnumSet<AstContextType> types = EnumSet.copyOf(Arrays.asList(contextTypes));
        AstContext current = this;
        AstContext found = null;
        while (current != null) {
            if (types.contains(current.contextType)) {
                found = current;
            }
            current = current.parent;
        }

        return found;
    }

    /**
     * For a given descendant, finds the direct child of this context that contains the descendant.
     *
     * @param descendant descendant to this context
     * @return direct child containing the descendant, or null if it doesn't exist
     */
    public @Nullable AstContext findDirectChild(AstContext descendant) {
        AstContext current = descendant;
        while (current != null) {
            if (current.parent == this) {
                return current;
            }
            current = current.parent;
        }

        return null;
    }

    public @Nullable AstContext findSubcontext(AstSubcontextType type) {
        for (AstContext child : children) {
            if (child.subcontextType == type) {
                return child;
            }
        }
        return null;
    }

    public @Nullable AstContext findLastSubcontext(AstSubcontextType type) {
        for (int i = children.size() - 1; i >= 0; i--) {
            if (child(i).subcontextType == type) {
                return child(i);
            }
        }
        return null;
    }

    public List<AstContext> findSubcontexts(AstSubcontextType type) {
        return children.stream().filter(c -> c.subcontextType == type).toList();
    }

    public boolean containsChildContext(Predicate<AstContext> matcher) {
        for (AstContext child : children) {
            if (matcher.test(child) || child.containsChildContext(matcher)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines whether the instruction is executed exactly once within a context - i.e. isn't part o any embedded
     * control flow structure, such as if, loop or case statements/expressions.
     *
     * @param instruction instruction to evaluate
     * @return true if the instruction is simply contained in this context
     */
    public boolean executesOnce(LogicInstruction instruction) {
        for (AstContext ctx = instruction.getAstContext(); ctx != null; ctx = ctx.parent()) {
            if (!ctx.isLinear()) {
                return ctx == this || ctx.parent() == this;
            }
        }
        return false;
    }

    public boolean isLinear() {
        if (contextType == AstContextType.CALL) {
            return subcontextType == AstSubcontextType.SYSTEM_CALL
                    || subcontextType == AstSubcontextType.ARGUMENTS
                    || subcontextType == AstSubcontextType.PARAMETERS
                    || subcontextType == AstSubcontextType.BASIC;
        } else {
            return subcontextType == AstSubcontextType.INIT || subcontextType == AstSubcontextType.BASIC || !contextType.flowControl;
        }
    }

    public String hierarchy() {
        String text = "#" + id + ": " +
                (subcontextType == AstSubcontextType.BASIC ? contextType.text : contextType.text + "[" +  subcontextType.text + "]");

        return parent == null ? text : parent.hierarchy() + "/" + text;
    }

    @Override
    public String toString() {
        return "AstContext{" +
                "id=" + id +
                ", parent id=" + (parent == null ? null : parent.id) +
                ", level=" + level +
                ", contextType=" + contextType +
                ", subcontextType=" + subcontextType +
                ", totalWeight=" + totalWeight() +
                ", node=" + node +
                '}';
    }

    public int level() {
        return level;
    }

    public @Nullable AstMindcodeNode node() {
        return node;
    }

    public AstMindcodeNode existingNode() {
        return Objects.requireNonNull(node);
    }

    public SourcePosition sourcePosition() {
        return node != null ? node.sourcePosition() : SourcePosition.EMPTY;
    }

    public @Nullable String functionPrefix() {
        return function == null ? null : function.getPrefix();
    }

    public @Nullable MindcodeFunction function() {
        return function;
    }

    public MindcodeFunction existingFunction() {
        return Objects.requireNonNull(function);
    }

    public @Nullable MindcodeFunction getFunctionBody() {
        return functionBody;
    }

    public int getFunctionCopyNumber() {
        return functionCopyNumber;
    }

    public boolean isFunction() {
        return function != null;
    }

    public AstContextType contextType() {
        return contextType;
    }

    public AstSubcontextType subcontextType() {
        return subcontextType;
    }

    public @Nullable AstContext parent() {
        return parent;
    }

    public AstContext existingParent() {
        return Objects.requireNonNull(parent);
    }

    public void updateWeight(double weight) {
        this.weight = weight;
    }

    public double weight() {
        return weight;
    }

    public double totalWeight() {
        return parent == null ? weight : weight * parent.totalWeight();
    }

    public List<AstContext> children() {
        return children;
    }

    public AstContext child(int index) {
        return children.get(index);
    }

    public @Nullable AstContext firstChild() {
        return children.isEmpty() ? null : children.getFirst();
    }

    public @Nullable AstContext lastChild() {
        return children.isEmpty() ? null : children.getLast();
    }

    public @Nullable AstContext nextChild(AstContext child) {
        for (int i = 0; i < children.size() - 1; i++) {
            if (children.get(i) == child) {
                return children.get(i + 1);
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AstContext) obj;
        return this.id == that.id &&
                this.level == that.level &&
                Objects.equals(this.node, that.node) &&
                Objects.equals(this.contextType, that.contextType) &&
                Objects.equals(this.subcontextType, that.subcontextType) &&
                this.parent == that.parent &&
                Double.doubleToLongBits(this.weight) == Double.doubleToLongBits(that.weight) &&
                Objects.equals(this.children, that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, level, node, contextType, subcontextType, weight);
    }

}
