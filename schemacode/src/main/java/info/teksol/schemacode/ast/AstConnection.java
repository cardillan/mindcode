package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.schemacode.SchematicsInternalError;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.schematics.SchematicElement;
import info.teksol.schemacode.schematics.SchematicsBuilder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public record AstConnection(SourcePosition sourcePosition, @Nullable AstCoordinates position, @Nullable AstLabel id) implements AstConfiguration {

    public AstConnection {
        if ((position == null) == (id == null)) {
            throw new IllegalArgumentException("Connection must have either position or reference.");
        }
    }

    public AstConnection(SourcePosition sourcePosition, AstCoordinates position) {
        this(sourcePosition, position, null);
    }

    public AstConnection(SourcePosition sourcePosition, AstLabel id) {
        this(sourcePosition, null, id);
    }

    // Test only
    public AstConnection(SourcePosition sourcePosition, int x, int y) {
        this(sourcePosition, new AstCoordinates(sourcePosition, x, y), null);
    }

    // Test only
    public AstConnection(SourcePosition sourcePosition, int x, int y, boolean relative) {
        this(sourcePosition, new AstCoordinates(
                sourcePosition.withColumn(sourcePosition.column() + 1),
                x, y, relative), null);
    }

    // Test only
    public AstConnection(SourcePosition pos, String label) {
        this(pos, null, new AstLabel(pos, List.of(new AstLabelSegment(pos, label))));
    }

    public Position evaluate(SchematicsBuilder.ResolverContext context, SchematicElement element) {
        if (position != null) {
            return position.evaluate(context, element);
        } else if (id != null) {
            SchematicElement target = element.resolveReference(context, id);
            return target == null ? Position.INVALID : target.position();
        } else {
            throw new SchematicsInternalError("Connection has neither position nor reference.");
        }
    }


    @Override
    public AstConnection withEmptyPosition() {
        return new AstConnection(SourcePosition.EMPTY, eraseNullablePosition(position), id);
    }
}
