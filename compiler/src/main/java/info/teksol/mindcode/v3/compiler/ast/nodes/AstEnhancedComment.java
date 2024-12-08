package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AstEnhancedComment extends AstFormattable {

    public AstEnhancedComment(@NotNull InputPosition inputPosition, @NotNull List<@NotNull AstMindcodeNode> parts) {
        super(inputPosition, parts);
    }

    @Override
    public String toString() {
        return "AstEnhancedComment{" +
                "parts=" + parts +
                '}';
    }
}
