package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;

import java.util.List;

@AstNode
public class AstModule extends AstStatementList {

    public AstModule(InputPosition inputPosition, List<AstMindcodeNode> expressions) {
        super(inputPosition, expressions);
    }
}