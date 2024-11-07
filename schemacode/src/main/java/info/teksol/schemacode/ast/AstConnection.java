package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.schematics.SchematicsBuilder;

public record AstConnection(InputPosition inputPosition, AstCoordinates position, String id) implements AstConfiguration {

    public AstConnection(InputPosition inputPosition, AstCoordinates position) {
        this(inputPosition, position, null);
    }

    public AstConnection(InputPosition inputPosition, int x, int y) {
        this(inputPosition, new AstCoordinates(inputPosition, x, y), null);
    }

    public AstConnection(InputPosition inputPosition, int x, int y, boolean relative) {
        this(inputPosition, new AstCoordinates(
                inputPosition.withColumn(inputPosition.column() + 1),
                x, y, relative), null);
    }

    public AstConnection(InputPosition inputPosition, String id) {
        this(inputPosition, null, id);
    }

    public Position evaluate(SchematicsBuilder builder, Position lastPosition) {
        if (position != null) {
            return position.evaluate(builder, lastPosition);
        } else {
            return builder.getBlockPosition(this, id).position();
        }
    }
}
