package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.mindcode.compiler.Modifier;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mc.util.CollectionUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;

@NullMarked
public class Modifiers {
    public static final Modifiers EMPTY = new Modifiers(Modifier.NONE, Map.of());

    private final Modifier main;
    private final Map<Modifier, ModifierParametrization<?>> modifiers;

    public Modifiers(Modifier main, Map<Modifier, ModifierParametrization<?>> modifiers) {
        this.main = main;
        this.modifiers = modifiers;
    }

    public Modifier getMain() {
        return main;
    }

    public boolean isEmpty() {
        return modifiers.isEmpty();
    }

    public boolean contains(Modifier modifier) {
        return modifiers.containsKey(modifier);
    }

    public boolean containsAny(Modifier... modifiers) {
        return CollectionUtils.containsAnyKey(this.modifiers, modifiers);
    }

    public AstMindcodeNode getNode(Modifier modifier) {
        return modifiers.get(modifier).node();
    }

    @SuppressWarnings("unchecked")
    public <T> @Nullable T getParameters(Modifier modifier) {
        ModifierParametrization<?> m = modifiers.get(modifier);
        return m == null ? null : (T) m.parametrization();
    }
}
