package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
@AstNode
public class AstRangedForLoopStatement extends AstLabeledStatement {
    private final boolean declaration;
    private final AstExpression variable;
    private final AstRange range;
    private final boolean descending;
    private final List<AstMindcodeNode> body;

    public AstRangedForLoopStatement(SourcePosition sourcePosition, @Nullable AstIdentifier loopLabel,
            boolean declaration, AstExpression variable, AstRange range, boolean descending, List<AstMindcodeNode> body) {
        super(sourcePosition, loopLabel, children(list(variable, range), body));
        this.declaration = declaration;
        this.variable = variable;
        this.range = range;
        this.descending = descending;
        this.body = body;
    }

    public boolean hasDeclaration() {
        return declaration;
    }

    public AstExpression getVariable() {
        return variable;
    }

    public AstRange getRange() {
        return range;
    }

    public boolean isDescending() {
        return descending;
    }

    public List<AstMindcodeNode> getBody() {
        return body;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AstRangedForLoopStatement that = (AstRangedForLoopStatement) o;
        return declaration == that.declaration && variable.equals(that.variable) && range.equals(that.range) && body.equals(that.body);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Boolean.hashCode(declaration);
        result = 31 * result + variable.hashCode();
        result = 31 * result + range.hashCode();
        result = 31 * result + body.hashCode();
        return result;
    }

}
