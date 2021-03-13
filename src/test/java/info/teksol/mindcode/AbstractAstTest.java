package info.teksol.mindcode;

import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.ast.AstNodeBuilder;
import info.teksol.mindcode.grammar.AbstractParserTest;

public class AbstractAstTest extends AbstractParserTest {
    public AstNode translateToAst(String program) {
        return AstNodeBuilder.generate(parse(program));
    }
}
