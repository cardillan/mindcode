package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
@AstNode(printFlat = true)
public class AstMlogParameters extends AstFragment {
    private final List<AstExpression> mlogNames;

    public AstMlogParameters(SourcePosition sourcePosition, List<AstExpression> mlogNames) {
        super(sourcePosition, children(mlogNames));
        this.mlogNames = mlogNames;
    }

    public List<AstExpression> getMlogNames() {
        return mlogNames;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstMlogParameters that = (AstMlogParameters) o;
        return mlogNames.equals(that.mlogNames);
    }

    @Override
    public int hashCode() {
        return mlogNames.hashCode();
    }
}
