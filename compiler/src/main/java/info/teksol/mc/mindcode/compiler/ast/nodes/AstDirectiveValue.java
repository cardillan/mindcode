package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode(printFlat = true)
public class AstDirectiveValue extends AstFragment {
    private final String text;

    public AstDirectiveValue(SourcePosition sourcePosition, String text) {
        super(sourcePosition);
        this.text = Objects.requireNonNull(text);
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstDirectiveValue that = (AstDirectiveValue) o;
        return text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

}