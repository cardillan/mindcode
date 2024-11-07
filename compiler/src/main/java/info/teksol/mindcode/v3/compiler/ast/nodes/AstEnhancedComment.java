package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;

import java.util.List;

public class AstEnhancedComment extends AstFormattable {

    public AstEnhancedComment(InputPosition inputPosition, List<AstMindcodeNode> parts) {
        super(inputPosition, parts);
    }

    @Override
    public String toString() {
        return "AstEnhancedComment{" +
                "parts=" + parts +
                '}';
    }
}
