package info.teksol.mindcode;

import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.ast.AstNodeBuilder;
import info.teksol.mindcode.grammar.AbstractParserTest;
import info.teksol.mindcode.mindustry.MOpcode;

import java.util.List;

public class AbstractAstTest extends AbstractParserTest {
    public AstNode translateToAst(String program) {
        return AstNodeBuilder.generate(parse(program));
    }

    protected final String prettyPrint(List<MOpcode> list) {
        return list.stream().map(Object::toString).reduce("", (s, s2) -> s + "\n" + s2);
    }
}
