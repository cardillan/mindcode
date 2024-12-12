package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
@AstNode
public class AstRangedForLoopStatement extends AstLabeledStatement {
    private final AstIdentifier variable;
    private final AstRange range;
    private final List< AstMindcodeNode> body;

    public AstRangedForLoopStatement(InputPosition inputPosition, @Nullable AstIdentifier loopLabel,
            AstIdentifier variable, AstRange range, List< AstMindcodeNode> body) {
        super(inputPosition, loopLabel);
        this.variable = variable;
        this.range = range;
        this.body = body;
    }

    public AstIdentifier getVariable() {
        return variable;
    }

    public AstRange getRange() {
        return range;
    }

    public List< AstMindcodeNode> getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AstRangedForLoopStatement that = (AstRangedForLoopStatement) o;
        return variable.equals(that.variable) && range.equals(that.range) && body.equals(that.body);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + variable.hashCode();
        result = 31 * result + range.hashCode();
        result = 31 * result + body.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AstRangedForLoopStatement{" +
               "variable=" + variable +
               ", range=" + range +
               ", body=" + body +
               ", label=" + label +
               '}';
    }
}
