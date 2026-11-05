package io.github.cardillan.mindcode.compiler.ast.nodes;

import io.github.cardillan.annotations.AstNode;
import io.github.cardillan.mindcode.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstLiteralNull extends AstLiteral {

    public AstLiteralNull(SourcePosition sourcePosition, String literal) {
        super(sourcePosition, literal, false);
        if (!"null".equals(literal)) {
            throw new IllegalArgumentException("Null literal is not 'null'");
        }
    }

    public AstLiteralNull(SourcePosition sourcePosition) {
        this(sourcePosition, "null");
    }

    @Override
    public AstLiteralNull withSourcePosition(SourcePosition sourcePosition) {
        return new AstLiteralNull(sourcePosition);
    }

    @Override
    public double getDoubleValue() {
        return 0.0;
    }

    @Override
    public long getLongValue() {
        return 0;
    }
}
