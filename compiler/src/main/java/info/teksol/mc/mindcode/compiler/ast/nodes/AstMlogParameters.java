package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstMlogParameters extends AstFragment {
    private final AstExpression mlog;

    public AstMlogParameters(SourcePosition sourcePosition, AstExpression mlog) {
        super(sourcePosition, children(mlog));
        this.mlog = mlog;
    }

    public AstExpression getMlog() {
        return mlog;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstMlogParameters that = (AstMlogParameters) o;
        return mlog.equals(that.mlog);
    }

    @Override
    public int hashCode() {
        return mlog.hashCode();
    }
}
