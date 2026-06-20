package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstParameter(SourcePosition sourcePosition, AstToken name, AstToken value) implements AstSchemaItem {

    @Override
    public AstSchemaItem withEmptyPosition() {
        return new AstParameter(SourcePosition.EMPTY, name.withEmptyPosition(), value.withEmptyPosition());
    }
}
