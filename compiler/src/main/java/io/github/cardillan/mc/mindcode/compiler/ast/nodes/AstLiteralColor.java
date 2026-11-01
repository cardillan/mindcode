package io.github.cardillan.mc.mindcode.compiler.ast.nodes;

import io.github.cardillan.annotations.AstNode;
import io.github.cardillan.mc.common.SourcePosition;
import io.github.cardillan.mc.evaluator.Color;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstLiteralColor extends AstLiteralNumeric {

    public AstLiteralColor(SourcePosition sourcePosition, String literal) {
        super(sourcePosition, literal, false);
    }

    @Override
    public double getDoubleValue() {
        return Color.parseColor(literal);
    }

    @Override
    public long getLongValue() {
        return (long) getDoubleValue();
    }

    @Override
    public AstLiteralColor withSourcePosition(SourcePosition sourcePosition) {
        return new AstLiteralColor(sourcePosition, literal);
    }
}
