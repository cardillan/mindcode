package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AstLiteralNumeric extends AstLiteral {

    protected AstLiteralNumeric(InputPosition inputPosition, String literal) {
        super(inputPosition, literal);
        if (literal.isEmpty()) {
            throw new IllegalArgumentException("Value cannot be empty");
        }
    }

    // TODO Warn when integer conversion produces precision loss
    //      Determine whether something bad may happen with double literals (infinity?)
    //      or integer literals (larger than Long.MAX_VALUE?)
    public abstract double getDoubleValue();

    public abstract long getLongValue();
}