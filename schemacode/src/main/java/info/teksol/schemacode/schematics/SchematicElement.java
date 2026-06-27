package info.teksol.schemacode.schematics;

import info.teksol.mc.mindcode.logic.mimex.BlockType;
import info.teksol.schemacode.SchematicsMetadata;
import info.teksol.schemacode.ast.AstBlock;
import info.teksol.schemacode.ast.AstCoordinates;
import info.teksol.schemacode.mindustry.Position;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@NullMarked
public class SchematicElement implements BlockPosition {
    /// The AST node that defines this region
    /// `null` for the top-level (implicit) region
    private final @Nullable AstBlock definition;

    /// Type of the block (if this is a block and not a region)
    private final @Nullable BlockType blockType;

    /// Unique block index
    private final int index;

    /// The dimensions of this region
    private final Position dimensions;

    /// The contained regions (empty for leaf blocks)
    private final List<SchematicElement> elements;

    /// The label map for blocs in this region
    private final Map<String, SchematicElement> labelMap;

    /// The origin of this region within the enclosing region
    /// Relative positions get resolved to absolute eventually
    private RegionPosition origin;

    public static SchematicElement createBlock(AstBlock definition, String type, int index, Map<String, SchematicElement> enclosingLabelMap, String lastReference) {
        BlockType blockType = SchematicsMetadata.getMetadata().getBlockByName(type);
        int size = blockType == null ? 1 : blockType.size();            // Must not happen
        Position dimensions = new Position(size, size);
        return new SchematicElement(definition, blockType, index, dimensions,
                createPosition(definition, enclosingLabelMap, lastReference), List.of(), Map.of());
    }

    public static SchematicElement create(@Nullable AstBlock definition, Position dimensions,
            List<SchematicElement> elements, Map<String, SchematicElement> currentLabelMap,
            Map<String, SchematicElement> enclosingLabelMap, String lastReference) {
        return new SchematicElement(definition, null, -1, dimensions,
                createPosition(definition, enclosingLabelMap, lastReference), elements, currentLabelMap);
    }

    private SchematicElement(@Nullable AstBlock definition, @Nullable BlockType blockType, int index, Position dimensions,
            RegionPosition origin, List<SchematicElement> elements, Map<String, SchematicElement> labelMap) {
        this.definition = definition;
        this.blockType = blockType;
        this.index = index;
        this.dimensions = dimensions;
        this.origin = origin;
        this.elements = elements;
        this.labelMap = labelMap;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public BlockType blockType() {
        return Objects.requireNonNull(blockType);
    }

    @Override
    public Position position() {
        if (origin instanceof AbsolutePosition(Position pos)) return pos;
        throw new IllegalStateException("Position not resolved yet");
    }

    /// Returns true if this instance represents a block
    public boolean isBlock() {
        return elements.isEmpty();
    }

    /// Returns true if this instance represents a region
    public boolean isRegion() {
        return !elements.isEmpty();
    }

    public AstBlock definition() {
        return Objects.requireNonNull(definition);
    }

    public Position dimensions() {
        return dimensions;
    }

    public Map<String, SchematicElement> getLabelMap() {
        return labelMap;
    }

    public List<SchematicElement> getBlocks() {
        List<SchematicElement> blocks = new ArrayList<>();
        forEachBlock(blocks::add);
        return blocks;
    }

    public void forEachBlock(Consumer<SchematicElement> consumer) {
        for (SchematicElement element : elements) {
            if (element.isBlock()) {
                consumer.accept(element);
            } else {
                element.forEachBlock(consumer);
            }
        }
    }

    public Position resolvePosition(LayoutResolver.Context context) {
        if (origin instanceof AbsolutePosition(Position pos)) return pos;

        AbsolutePosition absolute = origin.resolve(context);
        origin = absolute;
        return absolute.position();
    }

    private static RegionPosition createPosition(@Nullable AstBlock definition, Map<String, SchematicElement> labelMap, String lastReference) {
        if (definition == null) return new AbsolutePosition(Position.ORIGIN);
        AstCoordinates anchor = definition.position().anchor();
        return anchor.relative()
                ? new RelativePosition(definition, labelMap, Objects.requireNonNullElse(anchor.relativeTo(),  lastReference), anchor.coordinates())
                : new AbsolutePosition(anchor.coordinates());
    }

    private interface RegionPosition {
        AbsolutePosition resolve(LayoutResolver.Context context);
    }

    private record AbsolutePosition(Position position) implements RegionPosition {
        @Override
        public AbsolutePosition resolve(LayoutResolver.Context context) {
            return this;
        }
    }

    private record RelativePosition(AstBlock definition, Map<String, SchematicElement> labelMap, String reference, Position offset) implements RegionPosition {
        @Override
        public AbsolutePosition resolve(LayoutResolver.Context context) {
            SchematicElement region = labelMap.get(reference);
            if (region == null) {
                context.error(definition.position().anchor(), "Unknown block name '%s'.", reference);
                return new AbsolutePosition(Position.INVALID);
            } else if (context.visited(reference)) {
                if (context.unreported(reference)) {
                    context.error(definition.position().anchor(), "Circular definition of block '%s' position.", reference);
                }
                return new AbsolutePosition(Position.INVALID);
            } else {
                return new AbsolutePosition(region.resolvePosition(context).add(offset));
            }
        }
    }
}
