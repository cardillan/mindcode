package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode(printFlat = true)
public class AstRemoteParameters extends AstFragment {
    private final AstIdentifier processor;
    private final @Nullable AstLiteralString name;

    public AstRemoteParameters(SourcePosition sourcePosition, AstIdentifier processor, @Nullable AstLiteralString name) {
        super(sourcePosition, children(processor, name));
        this.processor = processor;
        this.name = name;
    }

    public AstIdentifier getProcessor() {
        return processor;
    }

    public @Nullable AstLiteralString getName() {
        return name;
    }

    public @Nullable String getStringName() {
        return name == null ? null : name.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstRemoteParameters that = (AstRemoteParameters) o;
        return processor.equals(that.processor) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        int result = processor.hashCode();
        result = 31 * result + Objects.hashCode(name);
        return result;
    }
}
