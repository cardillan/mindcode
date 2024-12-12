package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
@AstNode(printFlat = true)
public class AstDirectiveValue extends AstFragment {
    private final String text;

    public AstDirectiveValue(InputPosition inputPosition, String text) {
        super(inputPosition);
        this.text = Objects.requireNonNull(text);
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
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