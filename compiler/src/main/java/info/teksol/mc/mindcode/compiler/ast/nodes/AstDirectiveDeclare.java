package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
@AstNode
public class AstDirectiveDeclare extends AstDeclaration {
    private final AstIdentifier category;
    private final List<AstMindcodeNode> elements;

    public AstDirectiveDeclare(SourcePosition sourcePosition, AstIdentifier category,
            List<AstMindcodeNode> elements) {
        super(sourcePosition, children(list(category), elements));
        this.category = category;
        this.elements = elements;
    }

    public AstIdentifier getCategory() {
        return category;
    }

    public List<AstMindcodeNode> getElements() {
        return elements;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstDirectiveDeclare that = (AstDirectiveDeclare) o;
        return category.equals(that.category) && elements.equals(that.elements);
    }

    @Override
    public int hashCode() {
        int result = category.hashCode();
        result = 31 * result + elements.hashCode();
        return result;
    }
}
