package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstBoolean(SourcePosition sourcePosition, boolean value) implements AstConfiguration {

    @Override
    public AstBoolean withEmptyPosition() {
        return new AstBoolean(SourcePosition.EMPTY, value);
    }
}
