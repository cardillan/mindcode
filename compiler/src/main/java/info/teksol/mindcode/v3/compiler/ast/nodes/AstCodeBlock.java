package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AstCodeBlock extends AstStatementList {

    public AstCodeBlock(@NotNull InputPosition inputPosition, @NotNull List<@NotNull AstMindcodeNode> expressions) {
        super(inputPosition, expressions);
    }

    public AstCodeBlock(@NotNull AstStatementList statementList) {
        super(statementList.inputPosition(), statementList.getExpressions());
    }

}
