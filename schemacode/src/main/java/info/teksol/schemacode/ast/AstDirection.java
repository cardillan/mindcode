package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstDirection(SourcePosition sourcePosition, String direction) implements AstSchemaItem {

    @Override
    public AstDirection withEmptyPosition() {
        return new AstDirection(SourcePosition.EMPTY, direction);
    }
}
