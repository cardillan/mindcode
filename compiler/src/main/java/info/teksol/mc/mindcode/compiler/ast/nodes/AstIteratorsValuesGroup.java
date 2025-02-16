package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
@AstNode
public class AstIteratorsValuesGroup extends AstFragment {
    private final boolean declaration;
    private final List<AstIterator> iterators;
    private final AstExpressionList values;

    public AstIteratorsValuesGroup(SourcePosition sourcePosition, boolean declaration, List<AstIterator> iterators, AstExpressionList values) {
        super(sourcePosition, children(iterators, List.of(values)));
        this.declaration = declaration;
        this.iterators = iterators;
        this.values = values;
    }

    public boolean hasDeclaration() {
        return declaration;
    }

    public List<AstIterator> getIterators() {
        return iterators;
    }

    public AstExpressionList getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstIteratorsValuesGroup that = (AstIteratorsValuesGroup) o;
        return declaration == that.declaration && iterators.equals(that.iterators) && values.equals(that.values);
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(declaration);
        result = 31 * result + iterators.hashCode();
        result = 31 * result + values.hashCode();
        return result;
    }
}
