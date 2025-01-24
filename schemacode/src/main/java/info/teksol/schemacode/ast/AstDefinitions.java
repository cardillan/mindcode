package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record AstDefinitions(SourcePosition sourcePosition, List<AstDefinition> definitions) implements AstSchemaItem {

    @Override
    public AstDefinitions withEmptyPosition() {
        return new AstDefinitions(SourcePosition.EMPTY, erasePositions(definitions));
    }
}
