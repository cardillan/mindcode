package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.schematics.SchematicsBuilder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record AstCoordinates(SourcePosition sourcePosition, Position coordinates, boolean relative, @Nullable String relativeTo) implements AstSchemaItem {

    public AstCoordinates(SourcePosition sourcePosition, int x, int y, String relativeTo) {
        this(sourcePosition,new Position(x, y), true, relativeTo);
    }

    public AstCoordinates(SourcePosition sourcePosition, int x, int y) {
        this(sourcePosition,new Position(x, y), false, null);
    }

    public AstCoordinates(SourcePosition sourcePosition, int x, int y, boolean relative) {
        this(sourcePosition,new Position(x, y), relative, null);
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

    public @Nullable String getRelativeTo() {
        return relativeTo;
    }

    public AstCoordinates relative(boolean negate) {
        return negate ? new AstCoordinates(sourcePosition, -getX(), -getY(), true) : new AstCoordinates(sourcePosition, getX(), getY(), true);
    }

    public AstCoordinates relativeTo(String id) {
        return new AstCoordinates(sourcePosition, getX(), getY(), id);
    }

    @Override
    public AstCoordinates withEmptyPosition() {
        return new AstCoordinates(SourcePosition.EMPTY, coordinates, relative, relativeTo);
    }
}
