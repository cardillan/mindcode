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
    private final AstDirectiveValue option;
    private final List<AstDirectiveValue> values;

    public AstDirectiveSet(SourcePosition sourcePosition, AstDirectiveValue option,
            List<AstDirectiveValue> values) {
        super(sourcePosition, children(list(option), values));
        this.option = Objects.requireNonNull(option);
        this.values = Objects.requireNonNull(values);
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
        return option.equals(that.option) && values.equals(that.values);
    }

    @Override
    public int hashCode() {
        int result = option.hashCode();
        result = 31 * result + values.hashCode();
        return result;
    }

}
