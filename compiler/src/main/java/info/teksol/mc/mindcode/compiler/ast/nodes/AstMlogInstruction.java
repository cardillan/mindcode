package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

@NullMarked
@AstNode
public class AstMlogInstruction extends AstFragment {
    private final AstMlogToken opcode;
    private final List<AstExpression> tokens;

    public AstMlogInstruction(SourcePosition sourcePosition, AstMlogToken opcode, List<AstExpression> tokens) {
        super(sourcePosition, children(list(opcode), tokens));
        this.opcode = opcode;
        this.tokens = tokens;
    }

    public AstMlogToken getOpcode() {
        return opcode;
    }

    public List<AstExpression> getTokens() {
        return tokens;
    }

    public AstExpression getToken(int index) {
        return tokens.get(index);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstMlogInstruction that = (AstMlogInstruction) o;
        return Objects.equals(opcode, that.opcode) && tokens.equals(that.tokens);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(opcode);
        result = 31 * result + tokens.hashCode();
        return result;
    }
}
