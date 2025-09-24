package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
@AstNode
public class AstDirectiveSet extends AstDeclaration {
    private final boolean local;
    private final AstDirectiveValue option;
    private final List<AstDirectiveValue> values;

    public AstDirectiveSet(SourcePosition sourcePosition, boolean local, AstDirectiveValue option, List<AstDirectiveValue> values) {
        super(sourcePosition, children(list(option), values));
        this.local = local;
        this.option = Objects.requireNonNull(option);
        this.values = Objects.requireNonNull(values);
    }

    public AstDirectiveSet(SourcePosition sourcePosition, @Nullable AstDocComment docComment, boolean local,
            AstDirectiveValue option, List<AstDirectiveValue> values) {
        super(sourcePosition, children(list(option), values), docComment);
        this.local = local;
        this.option = Objects.requireNonNull(option);
        this.values = Objects.requireNonNull(values);
    }

    @Override
    public AstNodeScope getScope() {
        return AstNodeScope.NONE;
    }

    @Override
    public AstNodeScope getScopeRestriction() {
        return local ? AstNodeScope.NONE : AstNodeScope.GLOBAL;
    }

    public boolean isLocal() {
        return local;
    }

    public AstDirectiveValue getOption() {
        return option;
    }

    public List<AstDirectiveValue> getValues() {
        return values;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstDirectiveSet that = (AstDirectiveSet) o;
        return local == that.local && option.equals(that.option) && values.equals(that.values);
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(local);
        result = 31 * result + option.hashCode();
        result = 31 * result + values.hashCode();
        return result;
    }
}
