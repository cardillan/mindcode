package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.mc.common.SourcePosition;

import java.util.List;
import java.util.Objects;

public class AstLoopIteratorGroup extends AstExpression {
    private final boolean declaration;
    private final List<AstLoopIterator> iterators;

    public AstLoopIteratorGroup(SourcePosition sourcePosition, boolean declaration, List<AstLoopIterator> iterators) {
        super(sourcePosition, children(iterators));
        this.declaration = declaration;
        this.iterators = Objects.requireNonNull(iterators);
    }

    public boolean hasDeclaration() {
        return declaration;
    }

    public List<AstLoopIterator> getIterators() {
        return iterators;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstLoopIteratorGroup that = (AstLoopIteratorGroup) o;
        return declaration == that.declaration && Objects.equals(iterators, that.iterators);
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(declaration);
        result = 31 * result + Objects.hashCode(iterators);
        return result;
    }
}
