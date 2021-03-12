package info.teksol.mindcode;

import info.teksol.mindcode.grammar.AbstractParserTest;

public class AbstractAstTest extends AbstractParserTest {
    AstNode translateToAst(String program) {
        return AstNodeBuilder.generate(parse(program));
    }
}
