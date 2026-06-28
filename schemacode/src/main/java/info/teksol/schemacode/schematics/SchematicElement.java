package info.teksol.schemacode.schematics;

import info.teksol.mc.mindcode.logic.mimex.BlockType;
import info.teksol.schemacode.SchematicsMetadata;
import info.teksol.schemacode.ast.AstBlock;
import info.teksol.schemacode.ast.AstLabel;
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
    private RegionPosition origin = new RelativePosition();

    /// The parent region of this element, `null` for the top-level region
    private @Nullable SchematicElement parent;

    /// The default anchor for this element: the previously placed element within the current region,
    /// or `null` if this is the first element within a region.
    private @Nullable SchematicElement anchor;

    public static SchematicElement createBlock(AstBlock definition, String type, int index) {
        BlockType blockType = SchematicsMetadata.getMetadata().getBlockByName(type);
        int size = blockType == null ? 1 : blockType.size();            // Must not happen
        Position dimensions = new Position(size, size);
        return new SchematicElement(definition, blockType, index, dimensions, List.of(), Map.of());
    }

    public static SchematicElement createRegion(@Nullable AstBlock definition, Position dimensions, int index, List<SchematicElement> elements,
            Map<String, SchematicElement> currentLabelMap) {
        return new SchematicElement(definition, null, index, dimensions, elements, currentLabelMap);
    }

    private SchematicElement(@Nullable AstBlock definition, @Nullable BlockType blockType, int index, Position dimensions,
            List<SchematicElement> elements, Map<String, SchematicElement> labelMap) {
        this.definition = definition;
        this.blockType = blockType;
        this.index = index;
        this.dimensions = dimensions;
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

    public @Nullable SchematicElement parent() {
        return parent;
    }

    public void setParent(@Nullable SchematicElement parent) {
        this.parent = parent;
    }

    public @Nullable SchematicElement anchor() {
        return anchor;
    }

    public void setAnchor(@Nullable SchematicElement anchor) {
        this.anchor = anchor;
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

    public Position resolvePosition(SchematicsBuilder.ResolverContext context) {
        if (origin instanceof AbsolutePosition(Position pos)) return pos;

        AbsolutePosition absolute = origin.resolve(context);
        origin = absolute;
        return absolute.position();
    }

    public void updateOrigin(Position offset) {
        AbsolutePosition newOrigin = definition == null ? new AbsolutePosition(Position.ORIGIN) : origin.add(offset);
        origin = newOrigin;
        elements.forEach(e -> e.updateOrigin(newOrigin.position()));
    }

    public @Nullable SchematicElement resolveReference(SchematicsBuilder.ResolverContext context, AstLabel reference) {
        // TODO: many more!!!
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "SchematicElement{" +
                "blockType=" + blockType +
                ", origin=" + origin +
                ", dimensions=" + dimensions +
                '}';
    }

    private interface RegionPosition {
        AbsolutePosition add(Position offset);
        AbsolutePosition resolve(SchematicsBuilder.ResolverContext context);
    }

    private record AbsolutePosition(Position position) implements RegionPosition {
        @Override
        public AbsolutePosition add(Position offset) {
            return offset.zero() ? this : new AbsolutePosition(position.add(offset));
        }

        @Override
        public AbsolutePosition resolve(SchematicsBuilder.ResolverContext context) {
            return this;
        }
    }

    // This class in an inner class of the enclosing SchematicElement. It takes all the information it needs from it.
    private class RelativePosition implements RegionPosition {

        @Override
        public AbsolutePosition add(Position offset) {
            //return offset.zero() ? this : new RelativePosition(definition, labelMap, reference, this.offset.add(offset));
            throw new UnsupportedOperationException();
        }

        @Override
        public AbsolutePosition resolve(SchematicsBuilder.ResolverContext context) {
            Objects.requireNonNull(definition);
            if (!definition.anchor().relative()) return new AbsolutePosition(definition.position().anchor().coordinates());

            AstLabel reference = definition.anchor().relativeTo();
            Position offset = definition.anchor().coordinates();

            if (reference != null) {
                SchematicElement region = resolveReference(context, reference);

                if (region == null) {
                    context.error(definition.position().anchor(), "Unknown block name '%s'.", reference);
                } else if (context.visited(region)) {
                    if (context.unreported(region)) {
                        context.error(definition.position().anchor(), "Circular definition of block '%s' position.", reference);
                    }
                } else {
                    return new AbsolutePosition(region.resolvePosition(context).add(offset));
                }
            } else {
                if (anchor != null) {
                    return new AbsolutePosition(anchor.resolvePosition(context).add(offset));
                }
                context.error(definition, "No anchor defined for block.");
            }

            return new AbsolutePosition(Position.INVALID);
        }
    }
}
