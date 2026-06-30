package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.schematics.SchematicElement;
import info.teksol.schemacode.schematics.SchematicsBuilder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record AstCoordinates(SourcePosition sourcePosition, Position coordinates, boolean relative, @Nullable AstLabel relativeTo) implements AstSchemaItem {

    public AstCoordinates(SourcePosition sourcePosition, int x, int y, AstLabel relativeTo) {
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

    public Position evaluate(SchematicsBuilder.ResolverContext context, SchematicElement element) {
        if (relative) {
            SchematicElement anchor = relativeTo == null ? element : element.resolveReference(context, relativeTo, true);
            if (anchor == null) return Position.INVALID;
            return coordinates.add(anchor.position());
        } else {
            // Translate the position to the proper connection point of the target block
            SchematicElement anchor = context.getElement(coordinates);
            return anchor == null ? coordinates : anchor.position();
        }
    }

    public AstCoordinates relative(boolean negate) {
        return negate ? new AstCoordinates(sourcePosition, -getX(), -getY(), true) : new AstCoordinates(sourcePosition, getX(), getY(), true);
    }

    public AstCoordinates relativeTo(AstLabel id) {
        return new AstCoordinates(sourcePosition, getX(), getY(), id);
    }

    @Override
    public AstCoordinates withEmptyPosition() {
        return new AstCoordinates(SourcePosition.EMPTY, coordinates, relative, eraseNullablePosition(relativeTo));
    }
}
