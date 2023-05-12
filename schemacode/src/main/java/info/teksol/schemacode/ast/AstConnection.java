package info.teksol.schemacode.ast;

import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.schema.SchematicsBuilder;

public record AstConnection(AstCoordinates position, String id) implements AstConfiguration {

    public AstConnection(AstCoordinates position) {
        this(position, null);
    }

    public AstConnection(String id) {
        this(null, id);
    }

    public Position evaluate(SchematicsBuilder builder, Position lastPosition) {
        if (position != null) {
            return position.evaluate(builder, lastPosition);
        } else {
            return builder.getBlock(id).position();
        }
    }
}
