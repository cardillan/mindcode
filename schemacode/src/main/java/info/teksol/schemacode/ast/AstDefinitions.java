package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record AstDefinitions(SourcePosition sourcePosition, List<AstDefinition> definitions) implements AstSchemaItem {

    public AstDefinitions(List<AstDefinition> definitions) {
        this(SourcePosition.EMPTY, definitions);
    }

    @Override
    public AstDefinitions withEmptyPosition() {
        return new AstDefinitions(SourcePosition.EMPTY, erasePositions(definitions));
    }
}
