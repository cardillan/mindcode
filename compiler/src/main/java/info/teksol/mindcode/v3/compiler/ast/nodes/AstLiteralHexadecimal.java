package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode
public class AstLiteralHexadecimal extends AstLiteralNumeric {

    public AstLiteralHexadecimal(InputPosition inputPosition, String literal) {
        super(inputPosition, literal);
        if (!literal.startsWith("0x")) {
            throw new IllegalArgumentException("Binary literal must start with '0x'");
        }
    }

}
