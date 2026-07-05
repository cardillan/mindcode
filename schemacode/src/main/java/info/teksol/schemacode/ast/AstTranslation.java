package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstTranslation(SourcePosition sourcePosition, boolean horizontal) implements AstSchemaItem {

    public AstTranslation(boolean horizontal) {
        this(SourcePosition.EMPTY, horizontal);
    }

    @Override
    public AstTranslation withEmptyPosition() {
        return new AstTranslation(SourcePosition.EMPTY, horizontal);
    }
}
