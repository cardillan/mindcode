package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstExpression;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class FormattableContent extends CompoundValueStore  {
    private final List<AstExpression> parts;

    public FormattableContent(SourcePosition sourcePosition, List<AstExpression> parts) {
        super(sourcePosition, ERR.FORMATTABLE_FORBIDDEN);
        this.parts = parts;
    }

    public List<AstExpression> getParts() {
        return parts;
    }

    @Override
    public FormattableContent withSourcePosition(SourcePosition sourcePosition) {
        return new FormattableContent(sourcePosition, parts);
    }
}
