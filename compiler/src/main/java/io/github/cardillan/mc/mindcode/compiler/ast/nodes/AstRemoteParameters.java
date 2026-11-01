package io.github.cardillan.mc.mindcode.compiler.ast.nodes;

import io.github.cardillan.annotations.AstNode;
import io.github.cardillan.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstRemoteParameters extends AstFragment {
    private final AstIdentifier processor;

    public AstRemoteParameters(SourcePosition sourcePosition, AstIdentifier processor) {
        super(sourcePosition, children(processor));
        this.processor = processor;
    }

    public AstIdentifier getProcessor() {
        return processor;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstRemoteParameters that = (AstRemoteParameters) o;
        return processor.equals(that.processor);
    }

    @Override
    public int hashCode() {
        return processor.hashCode();
    }
}
