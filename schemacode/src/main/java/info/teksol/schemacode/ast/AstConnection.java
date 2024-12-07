package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.schematics.SchematicsBuilder;

public record AstConnection(SourcePosition sourcePosition, AstCoordinates position, String id) implements AstConfiguration {

    public AstConnection(SourcePosition sourcePosition, AstCoordinates position) {
        this(sourcePosition, position, null);
    }

    public AstConnection(SourcePosition sourcePosition, int x, int y) {
        this(sourcePosition, new AstCoordinates(sourcePosition, x, y), null);
    }

    public AstConnection(SourcePosition sourcePosition, int x, int y, boolean relative) {
        this(sourcePosition, new AstCoordinates(
                sourcePosition.withColumn(sourcePosition.column() + 1),
                x, y, relative), null);
    }

    public AstConnection(SourcePosition sourcePosition, String id) {
        this(sourcePosition, null, id);
    }

    public Position evaluate(SchematicsBuilder builder, Position lastPosition) {
        if (position != null) {
            return position.evaluate(builder, lastPosition);
        } else {
            return builder.getBlockPosition(this, id).position();
        }
    }
}
