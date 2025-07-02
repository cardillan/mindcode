package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode
public class AstSubarray extends AstExpression implements AstArray {
    private final @Nullable AstIdentifier processor;
    private final AstIdentifier array;
    private final AstRange range;

    public AstSubarray(SourcePosition sourcePosition, AstIdentifier array, AstRange range) {
        super(sourcePosition, children(array, range));
        this.processor = null;
        this.array = array;
        this.range = range;
    }

    public AstSubarray(SourcePosition sourcePosition, AstIdentifier processor, AstIdentifier array, AstRange range) {
        super(sourcePosition);
        this.processor = Objects.requireNonNull(processor);
        this.array = array;
        this.range = range;
    }

    public @Nullable AstIdentifier getProcessor() {
        return processor;
    }

    public AstIdentifier getArray() {
        return array;
    }

    public AstRange getRange() {
        return range;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstSubarray that = (AstSubarray) o;
        return array.equals(that.array) && range.equals(that.range);
    }

    @Override
    public int hashCode() {
        int result = array.hashCode();
        result = 31 * result + range.hashCode();
        return result;
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.ARRAY_ACCESS;
    }
}
