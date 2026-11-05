package io.github.cardillan.mindcode.compiler.ast.nodes;

import io.github.cardillan.annotations.AstNode;
import io.github.cardillan.mindcode.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstLiteralEscape extends AstLiteralString {

    public AstLiteralEscape(SourcePosition sourcePosition, String literal) {
        super(sourcePosition, literal);
    }

    @Override
    public AstLiteralEscape withSourcePosition(SourcePosition sourcePosition) {
        return new AstLiteralEscape(sourcePosition, literal);
    }
}
