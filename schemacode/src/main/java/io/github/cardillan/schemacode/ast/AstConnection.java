package io.github.cardillan.schemacode.ast;

import io.github.cardillan.mc.common.SourcePosition;
import io.github.cardillan.schemacode.SchematicsInternalError;
import io.github.cardillan.schemacode.mindustry.Position;
import io.github.cardillan.schemacode.schematics.SchematicElement;
import io.github.cardillan.schemacode.schematics.SchematicsBuilder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

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
    public AstConnection(int x, int y) {
        this(SourcePosition.EMPTY, new AstCoordinates(x, y), null);
    }

    // Test only
    public AstConnection(int x, int y, boolean relative) {
        this(SourcePosition.EMPTY, new AstCoordinates(x, y, relative), null);
    }

    // Test only
    public AstConnection(String label) {
        this(SourcePosition.EMPTY, new AstLabel(List.of(new AstLabelSegment(label))));
    }

    public Position evaluate(SchematicsBuilder.ResolverContext context, SchematicElement element) {
        if (position != null) {
            return position.evaluate(context, element);
        } else if (id != null) {
            SchematicElement target = element.resolveReference(context, id, true);
            return target == null ? Position.INVALID : target.position();
        } else {
            throw new SchematicsInternalError("Connection has neither position nor reference.");
        }
    }

    public void evaluateMultiple(Consumer<Position> consumer, SchematicsBuilder.ResolverContext context, SchematicElement element) {
        if (position != null) {
            consumer.accept(position.evaluate(context, element));
        } else if (id != null) {
            element.resolveReferences((e, _) -> consumer.accept(e.position()), context, id, true);
        } else {
            throw new SchematicsInternalError("Connection has neither position nor reference.");
        }
    }

    @Override
    public AstConnection withEmptyPosition() {
        return new AstConnection(SourcePosition.EMPTY, eraseNullablePosition(position),
                eraseNullablePosition(id));
    }
}
