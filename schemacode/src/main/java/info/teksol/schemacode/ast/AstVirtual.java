package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstVirtual(SourcePosition sourcePosition) implements AstConfiguration {

    @Override
    public AstVirtual withEmptyPosition() {
        return new AstVirtual(SourcePosition.EMPTY);
    }
}
