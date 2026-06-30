package info.teksol.schemacode.schematics;

import info.teksol.mc.mindcode.logic.mimex.BlockType;
import info.teksol.schemacode.ast.AstBlock;
import info.teksol.schemacode.ast.AstLabel;
import info.teksol.schemacode.mindustry.Position;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

@NullMarked
public class SchematicElement implements BlockPosition {

    /// The parent region of this element, `null` for the top-level region
    private final @Nullable SchematicElement parent;

    /// The AST node that defines this region
    /// `null` for the top-level (implicit) region
    private final @Nullable AstBlock definition;

    /// Type of the block (if this is a block and not a region)
    private final @Nullable BlockType blockType;

    /// Unique block index
    private final int index;

    /// The contained regions (empty for leaf blocks)
    final List<SchematicElement> elements;

    /// The label map for blocs in this region
    final Map<String, SchematicElement> labelMap;

    /// The dimensions of this region
    private @Nullable Position dimensions;

    /// The default anchor for this element: the previously placed element within the current region,
    /// or `null` if this is the first element within a region.
    private @Nullable SchematicElement anchor;

    /// The position relative to the enclosing region
    private @Nullable Position origin;

    /// The absolute position of this element within the schematic
    private @Nullable Position absolutePosition;

    /// An offset to be added to the computed position, used when constructing region arrays
    private final Position offset;

    public static SchematicElement createBlock(@Nullable SchematicElement parent, AstBlock definition, BlockType blockType, int index) {
        int size = blockType.size();
        Position dimensions = new Position(size, size);
        return new SchematicElement(parent, definition, blockType, index, dimensions, List.of(), Map.of(), Position.ORIGIN);
    }

    public static SchematicElement createRegion(@Nullable SchematicElement parent, @Nullable AstBlock definition, int index) {
        return new SchematicElement(parent, definition, null, index, null, new ArrayList<>(), new HashMap<>(), Position.ORIGIN);
    }

    private SchematicElement(@Nullable SchematicElement parent, @Nullable AstBlock definition, @Nullable BlockType blockType,
            int index, @Nullable Position dimensions, List<SchematicElement> elements, Map<String, SchematicElement> labelMap, Position offset) {
        this.parent = parent;
        this.definition = definition;
        this.blockType = blockType;
        this.index = index;
        this.dimensions = dimensions;
        this.elements = elements;
        this.labelMap = labelMap;
        this.offset = offset;
    }

    public SchematicElement duplicateAt(Position position, IntSupplier indexSupplier) {
        return duplicate(parent, definition, position, indexSupplier);
    }

    public SchematicElement duplicateInto(SchematicElement parent, @Nullable AstBlock definition, IntSupplier indexSupplier) {
        return duplicate(parent, definition, Position.ORIGIN, indexSupplier);
    }

    private SchematicElement duplicate(@Nullable SchematicElement parent, @Nullable AstBlock definition, Position offset, IntSupplier indexSupplier) {
        boolean isRegion = isRegion();
        SchematicElement copy = new SchematicElement(parent, definition, blockType, indexSupplier.getAsInt(), dimensions,
                isRegion ? new ArrayList<>() : List.of(), isRegion ? new HashMap<>() : Map.of(), offset);

        if (isRegion) {
            // Copy elements
            IdentityHashMap<SchematicElement, SchematicElement> copies = new IdentityHashMap<>(elements.size());
            for (SchematicElement element : elements) {
                SchematicElement elementCopy = element.duplicate(copy, element.definition, offset, indexSupplier);
                copy.elements.add(elementCopy);
                copies.put(element, elementCopy);
            }

            // Remap labels
            for (Map.Entry<String, SchematicElement> entry : labelMap.entrySet()) {
                copy.labelMap.put(entry.getKey(), copies.get(entry.getValue()));
            }
        }

        if (origin != null) {
            assert absolutePosition != null;
            copy.origin = origin;
            copy.absolutePosition = absolutePosition.add(offset);
        }
        return copy;
    }

    void addElement(SchematicElement element) {
        elements.add(element);
    }

    void addLabel(String label, SchematicElement element) {
        labelMap.put(label, element);
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public BlockType blockType() {
        return Objects.requireNonNull(blockType);
    }

    public Position origin() {
        if (origin == null) throw new IllegalStateException("Position not resolved yet");
        return origin;
    }

    public Position absolutePosition() {
        if (absolutePosition == null) throw new IllegalStateException("Position not resolved yet");
        return absolutePosition;
    }

    @Override
    public Position position() {
        return absolutePosition();
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
        return Objects.requireNonNull(dimensions);
    }

    public void setDimensions(Position dimensions) {
        this.dimensions = dimensions;
    }

    public Map<String, SchematicElement> getLabelMap() {
        return labelMap;
    }

    public @Nullable SchematicElement parent() {
        return parent;
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
        if (origin != null) return origin;

        origin = definition == null ? Position.ORIGIN :
                definition.anchor().relative() ? resolveRelativePosition(context) :
                        definition.position().anchor().coordinates();
        absolutePosition = origin.add(offset);

        return absolutePosition;
    }

    public void updateOrigin() {
        if (origin == null) throw new IllegalStateException("Position not resolved yet");

        if (!origin.zero()) {
            elements.forEach(element -> element.absolutePosition = element.absolutePosition().add(origin));
        }
    }

    /// Determines a position relative to the given region. The given region must (directly or indirectly) contain
    /// this element.
    public @Nullable Position positionIn(SchematicsBuilder.ResolverContext context, SchematicElement region) {
        if (origin == null) throw new IllegalStateException("Position not resolved yet");

        Position position = origin;
        SchematicElement current = parent;
        while (current != region) {
            if (current == null) return null;
            position = position.add(current.origin());
            current = current.parent;
        }

        return position;
    }

    private Position resolveRelativePosition(SchematicsBuilder.ResolverContext context) {
        assert definition != null;

        // Relative position
        AstLabel reference = definition.anchor().relativeTo();
        Position offset = definition.anchor().coordinates();

        if (reference != null) {
            SchematicElement target = resolveReference(context, reference, false);
            if (target == null) return Position.INVALID;

            if (context.visited(target)) {
                for (SchematicElement element : context.visitedSince(target)) {
                    if (context.unreported(element)) {
                        context.error(definition.position().anchor(), "Circular definition of block '%s' position.",
                                Objects.requireNonNull(element.definition().anchor().relativeTo()).fullName());
                    }
                }
            } else {
                target.resolvePosition(context);
                Position position = target.positionIn(context, Objects.requireNonNull(parent));
                if (position == null) {
                    context.error(definition.position().anchor(), "Block '%s' is not within the current region.", reference.fullName());
                    return Position.INVALID;
                } else {
                    return position.add(offset);
                }
            }
        } else {
            if (anchor != null) {
                return anchor.resolvePosition(context).add(offset);
            }
            context.error(definition, "No anchor defined for block.");
        }

        return Position.INVALID;
    }

    public @Nullable SchematicElement resolveReference(SchematicsBuilder.ResolverContext context, AstLabel reference, boolean blockOnly) {
        String lead = reference.getSegment(0);
        SchematicElement current = parent;
        while (current != null) {
            SchematicElement element = current.labelMap.get(lead);
            if (element != null) {
                // We've matched the lead. The rest needs to be resolved in this namespace
                return element.resolveRemaining(context, reference, blockOnly);
            }
            current = current.parent;
        }

        context.error(reference, "Unknown block name '%s'.", reference.getSegment(0));
        return null;
    }

    private @Nullable SchematicElement resolveRemaining(SchematicsBuilder.ResolverContext context, AstLabel reference, boolean blockOnly) {
        SchematicElement current = this;

        for (int index = 1; index < reference.getSegmentCount(); index++) {
            SchematicElement element = current.labelMap.get(reference.getSegment(index));

            if (element == null) {
                context.error(reference, "Unknown block label '%s'.", reference.fullName(index));
                return null;
            } else if (element.isBlock() && index < reference.getSegmentCount() - 1) {
                context.error(reference, "Cannot resolve block label: '%s' is not a region.",
                        reference.fullName(index));
                return null;
            }

            current = element;
        }

        if (blockOnly && !current.isBlock()) {
            context.error(reference, "Block label '%s' doesn't denote a block.", reference.fullName());
            return null;
        }
        return current;
    }

    @Override
    public String toString() {
        return "SchematicElement{" +
                "blockType=" + blockType +
                ", origin=" + origin +
                ", dimensions=" + dimensions +
                '}';
    }
}
