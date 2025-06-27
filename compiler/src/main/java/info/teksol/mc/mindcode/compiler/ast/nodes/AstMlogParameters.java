package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstMlogParameters extends AstFragment {
    private final AstLiteralString name;

    public AstMlogParameters(SourcePosition sourcePosition, AstLiteralString name) {
        super(sourcePosition, children(name));
        this.name = name;
    }

    public AstLiteralString getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstMlogParameters that = (AstMlogParameters) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
