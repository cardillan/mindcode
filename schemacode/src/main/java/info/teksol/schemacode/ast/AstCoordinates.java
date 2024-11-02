package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.schematics.SchematicsBuilder;

public record AstCoordinates(InputPosition inputPosition, Position coordinates, boolean relative, String relativeTo) implements AstSchemaItem {

    public AstCoordinates(InputPosition inputPosition, int x, int y, String relativeTo) {
        this(inputPosition,new Position(x, y), true, relativeTo);
    }

    public AstCoordinates(InputPosition inputPosition, int x, int y) {
        this(inputPosition,new Position(x, y), false, null);
    }

    public AstCoordinates(InputPosition inputPosition, int x, int y, boolean relative) {
        this(inputPosition,new Position(x, y), relative, null);
    }

    public Position coordinates() {
        return coordinates;
    }

    public int getX() {
        return coordinates.x();
    }

    public int getY() {
        return coordinates.y();
    }

    public Position evaluate(SchematicsBuilder builder, Position lastPosition) {
        if (relative) {
            Position rel = relativeTo == null ? lastPosition : builder.getBlockPosition(this, relativeTo).position();
            return rel.add(coordinates);
        } else {
            return builder.getAnchor(coordinates());
        }
    }

    public String getRelativeTo() {
        return relativeTo;
    }

    public AstCoordinates relative(boolean negate) {
        return negate ? new AstCoordinates(inputPosition, -getX(), -getY(), true) : new AstCoordinates(inputPosition, getX(), getY(), true);
    }

    public AstCoordinates relativeTo(String id) {
        return new AstCoordinates(inputPosition, getX(), getY(), id);
    }
}
