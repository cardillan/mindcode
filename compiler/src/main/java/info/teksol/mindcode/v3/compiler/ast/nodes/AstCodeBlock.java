package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;

import java.util.List;

public class AstCodeBlock extends AstStatementList {

    public AstCodeBlock(InputPosition inputPosition, List<AstMindcodeNode> expressions) {
        super(inputPosition, expressions);
    }

    public AstCodeBlock(AstStatementList statementList) {
        super(statementList.inputPosition(), statementList.getExpressions());
    }

}
