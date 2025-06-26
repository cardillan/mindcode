package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
@AstNode
public class AstMlogToken extends AstExpression {
    private final String token;

    public AstMlogToken(SourcePosition sourcePosition, String token) {
        super(sourcePosition);
        this.token = Objects.requireNonNull(token);
    }

    public String getToken() {
        return token;
    }

    public String getPlainToken() {
        return token.startsWith(":") ? token.substring(1) : token;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstMlogToken that = (AstMlogToken) o;
        return token.equals(that.token);
    }

    @Override
    public int hashCode() {
        return token.hashCode();
    }
}
