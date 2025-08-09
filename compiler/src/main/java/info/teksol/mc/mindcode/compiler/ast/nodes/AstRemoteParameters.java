package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode(printFlat = true)
public class AstRemoteParameters extends AstFragment {
    private final AstIdentifier processor;
    private final @Nullable AstExpression mlog;

    public AstRemoteParameters(SourcePosition sourcePosition, AstIdentifier processor, @Nullable AstExpression mlog) {
        super(sourcePosition, children(processor, mlog));
        this.processor = processor;
        this.mlog = mlog;
    }

    public AstIdentifier getProcessor() {
        return processor;
    }

    public @Nullable AstExpression getMlog() {
        return mlog;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstRemoteParameters that = (AstRemoteParameters) o;
        return processor.equals(that.processor) && Objects.equals(mlog, that.mlog);
    }

    @Override
    public int hashCode() {
        int result = processor.hashCode();
        result = 31 * result + Objects.hashCode(mlog);
        return result;
    }
}
