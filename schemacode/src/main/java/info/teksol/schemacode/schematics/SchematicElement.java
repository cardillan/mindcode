package info.teksol.schemacode.schematics;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.logic.mimex.BlockType;
import info.teksol.schemacode.ast.AstBlock;
import info.teksol.schemacode.ast.AstLabel;
import info.teksol.schemacode.ast.AstSchemaBlock;
import info.teksol.schemacode.ast.PlacementMode;
import info.teksol.schemacode.mindustry.Direction;
import info.teksol.schemacode.mindustry.Position;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.regex.Pattern;

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

    /// The labels of this block/region
    final List<String> labels = new ArrayList<>();

    /// The label map for blocs in this region
    final Map<String, SchematicElement> labelMap;

    /// The direction of the block or region
    private final Direction originalDirection;

    /// The dimensions of this region
    private @Nullable Position dimensions;

    /// The default anchor for this element: the previously placed element within the current region,
    /// or `null` if this is the first element within a region.
    private @Nullable SchematicElement defaultAnchor;

    /// The position relative to the enclosing region
    private @Nullable Position origin;

    /// The absolute position of this element within the schematic
    private @Nullable Position absolutePosition;

    /// An offset to be added to the origin AND computed position; used when constructing region arrays
    private final Position originOffset;

    /// An offset to be added to the computed position only; used when rotating regions
    private Position positionOffset = Position.ORIGIN;

    /// An additional rotation since the block's creation
    private Direction rotation = Direction.EAST;

    /// Once the placement mode is determined, it is cached here
    private @Nullable PlacementMode placementMode;

    /// The block is valid
    private boolean valid = true;

    public static SchematicElement createBlock(@Nullable SchematicElement parent, AstBlock definition, BlockType blockType, int index) {
        int size = blockType.size();
        Position dimensions = new Position(size, size);
        return new SchematicElement(parent, definition, blockType, index, direction(definition), dimensions,
                List.of(), Map.of(), Position.ORIGIN);
    }

    // Note: a region is always created with the 'east' direction and rotated after construction
    public static SchematicElement createRegion(@Nullable SchematicElement parent, @Nullable AstBlock definition, int index) {
        return new SchematicElement(parent, definition, null, index, Direction.EAST, null,
                new ArrayList<>(), new LinkedHashMap<>(), Position.ORIGIN);
    }

    public enum Translation { NONE, HORIZONTAL, VERTICAL }
    public static Translation translation(@Nullable AstBlock definition) {
        if (definition == null || definition.translation() == null) return Translation.NONE;
        return definition.translation().horizontal() ? Translation.HORIZONTAL : Translation.VERTICAL;
    }

    private static final Random random = new Random();
    public static Direction direction(@Nullable AstBlock definition) {
        if (definition == null || definition.direction() == null) return Direction.EAST;
        String d = definition.direction().direction();
        return d.equals("random")
                ? Direction.convert(random.nextInt(4))
                : Direction.valueOf(d.toUpperCase());
    }

    private SchematicElement(@Nullable SchematicElement parent, @Nullable AstBlock definition, @Nullable BlockType blockType,
            int index, Direction originalDirection, @Nullable Position dimensions, List<SchematicElement> elements, Map<String, SchematicElement> labelMap,
            Position originOffset) {
        this.parent = parent;
        this.definition = definition;
        this.blockType = blockType;
        this.index = index;
        this.originalDirection = originalDirection;
        this.dimensions = dimensions;
        this.elements = elements;
        this.labelMap = labelMap;
        this.originOffset = originOffset;
    }

    public SchematicElement duplicateAt(Position position, IntSupplier indexSupplier) {
        return duplicate(parent, definition, position, indexSupplier);
    }

    public SchematicElement duplicateInto(SchematicElement parent, @Nullable AstBlock definition, IntSupplier indexSupplier) {
        return duplicate(parent, definition, Position.ORIGIN, indexSupplier);
    }

    public void resolveLabels(BiFunction<SchematicElement, String, String> labelResolver) {
        for (SchematicElement element : elements) {
            element.resolveLabels(labelResolver);
            if (element.labels.isEmpty()) continue;

            for (int index = 0; index < element.labels.size(); index++) {
                String label = element.labels.get(index);
                String resolvedLabel = labelResolver.apply(element,label);
                if (!label.equals(resolvedLabel)) {
                    labelMap.put(resolvedLabel, element);
                    labelMap.remove(label);
                    element.labels.set(index, resolvedLabel);
                }
            }
        }
    }

    private SchematicElement duplicate(@Nullable SchematicElement parent, @Nullable AstBlock definition,
            Position originOffset, IntSupplier indexSupplier) {
        boolean isRegion = isRegion();
        SchematicElement copy = new SchematicElement(parent, definition, blockType, indexSupplier.getAsInt(), originalDirection, dimensions,
                isRegion ? new ArrayList<>() : List.of(), isRegion ? new LinkedHashMap<>() : Map.of(), originOffset);
        copy.rotation = rotation;

        if (isRegion) {
            // Copy elements
            for (SchematicElement element : elements) {
                SchematicElement elementCopy = element.duplicate(copy, element.definition, originOffset, indexSupplier);
                copy.addElement(elementCopy);
                for (String label : element.labels) {
                    copy.addLabel(label, elementCopy);
                }
            }
        }

        if (origin != null) {
            assert absolutePosition != null;
            copy.origin = origin;
            copy.absolutePosition = absolutePosition;
        }
        return copy;
    }

    void addElement(SchematicElement element) {
        elements.add(element);
    }

    void addLabel(String label, SchematicElement element) {
        labelMap.put(label, element);
        element.labels.add(label);
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
        return definition != null && definition.element() instanceof AstSchemaBlock;
    }

    /// Returns true if this instance represents a region
    public boolean isRegion() {
        return !isBlock();
    }

    public boolean matches(boolean blocks, boolean regions) {
        return valid() && (isBlock() ? blocks : regions);
    }

    public AstBlock definition() {
        return Objects.requireNonNull(definition);
    }

    public Direction direction() {
        return originalDirection.rotate(rotation);
    }

    public Position dimensions() {
        return Objects.requireNonNull(dimensions);
    }

    public void setDimensions(Position dimensions) {
        this.dimensions = dimensions;
    }

    public List<String> labels() {
        return labels;
    }

    public Map<String, SchematicElement> getLabelMap() {
        return labelMap;
    }

    public @Nullable SchematicElement parent() {
        return parent;
    }

    public void setDefaultAnchor(@Nullable SchematicElement defaultAnchor) {
        this.defaultAnchor = defaultAnchor;
    }

    @Override
    public PlacementMode placementMode() {
        if (placementMode == null) {
            placementMode = definition != null && definition.placementMode() != PlacementMode.DEFAULT ? definition.placementMode()
                    : parent != null ? parent.placementMode()
                    : PlacementMode.DEFAULT;
        }
        return placementMode;
    }

    @Override
    public boolean valid() {
        return blockType != null && valid;
    }

    @Override
    public void invalidate() {
        valid = false;
    }

    // Processes only valid blocks
    public void forEachBlock(Consumer<SchematicElement> consumer) {
        for (SchematicElement element : elements) {
            if (element.isBlock()) {
                if (element.valid()) consumer.accept(element);
            } else {
                element.forEachBlock(consumer);
            }
        }
    }

    public void forEachElement(Consumer<SchematicElement> consumer) {
        for (SchematicElement element : elements) {
            if (element.valid()) consumer.accept(element);
            element.forEachElement(consumer);
        }
    }

    public void translate(Translation translation) {
        if (translation == Translation.NONE || isBlock()) return;

        Objects.requireNonNull(dimensions);
        switch (translation) {
            case HORIZONTAL -> {
                int rw = dimensions.x();
                for (SchematicElement element : elements) {
                    element.translate(translation);
                    element.move(rw - 2 * element.origin().x() - element.dimensions().x(), 0);
                }
            }
            case VERTICAL -> {
                int rh = dimensions.y();
                for (SchematicElement element : elements) {
                    element.translate(translation);
                    element.move(0, rh - 2 * element.origin().y() - element.dimensions().y());
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + translation);
        }
    }

    // Rotates the element.
    // For blocks, nothing needs to be made, although we do process the dimensions in case non-square blocks
    // get ever introduced into the game.
    // For regions, the following is done
    // 1. All contained elements are rotated.
    // 2. All contained elements are moved around the region's origin
    // 3. As a result of the rotation, the region's origin needs to be shifted to remain in the lower-left corner.
    //    This is against ensured by moving the elements around, taking the region's new dimensions into account.
    //
    // The origin of this element within the enclosing region remains unaffected.
    public void rotate(Direction rotateDirection) {
        if (rotateDirection == Direction.EAST) return;
        Objects.requireNonNull(dimensions);

        //System.out.println("Rotating " + this + " to " + rotateDirection);
        rotation = rotation.rotate(rotateDirection);
        if (rotateDirection != Direction.WEST) dimensions = dimensions.transpose();

        if (isRegion()) {
            int rw = dimensions.x() - 1, rh = dimensions.y() - 1;

            for (SchematicElement element : elements) {
                element.rotate(rotateDirection);
                int x = element.origin().x();
                int y = element.origin().y();
                int ew = element.dimensions().x() - 1, eh = element.dimensions().y() - 1;

                switch (rotateDirection) {
                    case NORTH -> element.move(rw - y - x - ew, x - y);
                    case WEST -> element.move(rw - 2*x - ew, rh - 2*y - eh);
                    case SOUTH -> element.move(y - x, rh - x - y - eh);
                    default -> throw new IllegalStateException("Unexpected value: " + rotateDirection);
                }
            }
        }
        //System.out.println("Finished rotating " + this + " to " + rotateDirection);
    }

    // Moves the element within the region
    private void move(int offsetX, int offsetY) {
        if (offsetX == 0 && offsetY == 0) return;
        //System.out.println("Moving " + this + " by " + offsetX + ", " + offsetY);

        if (origin != null) {
            assert absolutePosition != null;
            origin = origin.add(offsetX, offsetY);
            absolutePosition = absolutePosition.add(offsetX, offsetY);
        } else {
            positionOffset = positionOffset.add(offsetX, offsetY);
        }

        // For contained elements, we move just the absolute position
        for (SchematicElement e : elements) {
            e.updateAbsolutePosition(offsetX, offsetY);
        }

        //System.out.println("Finished moving " + this);
    }

    public Position resolvePosition(SchematicsBuilder.ResolverContext context) {
        if (origin != null) return origin;

        Position pos = definition == null ? Position.ORIGIN :
                definition.coordinates().relative() ? resolveRelativePosition(context) :
                        definition.position().coordinates().coordinates();
        origin = pos.add(originOffset);
        absolutePosition = origin.add(positionOffset);

        return absolutePosition;
    }

    private void updateAbsolutePosition(int offsetX, int offsetY) {
        //System.out.println("Moving position of " + this + " by " + offsetX + ", " + offsetY);
        assert absolutePosition != null;
        absolutePosition = absolutePosition.add(offsetX, offsetY);
        for (SchematicElement e : elements) {
            e.updateAbsolutePosition(offsetX, offsetY);
        }
    }

    public void updateOrigin() {
        if (origin == null) throw new IllegalStateException("Position not resolved yet");
        //System.out.println("Updating origin of " + this);

        if (!origin.zero()) {
            elements.forEach( e -> e.updateAbsolutePosition(origin.x(), origin.y()));
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
        AstLabel reference = definition.coordinates().relativeTo();
        Position offset = definition.coordinates().coordinates();

        if (reference != null) {
            SchematicElement target = resolveReference(context, reference, false);
            if (target == null) return Position.INVALID;

            if (context.visited(target)) {
                for (SchematicElement element : context.visitedSince(target)) {
                    if (context.unreported(element)) {
                        context.error(definition.position().coordinates(), "Circular definition of block '%s' position.",
                                Objects.requireNonNull(element.definition().coordinates().relativeTo()).fullName());
                    }
                }
            } else {
                target.resolvePosition(context);
                Position position = target.positionIn(context, Objects.requireNonNull(parent));
                if (position == null) {
                    context.error(definition.position().coordinates(), "Block '%s' is not within the current region.", reference.fullName());
                    return Position.INVALID;
                } else {
                    return position.add(offset);
                }
            }
        } else {
            if (defaultAnchor != null) {
                return defaultAnchor.resolvePosition(context).add(offset);
            }
            context.error(definition, "No anchor defined for block.");
        }

        return Position.INVALID;
    }

    public @Nullable SchematicElement resolveReference(SchematicsBuilder.ResolverContext context, AstLabel reference, boolean blockOnly) {
        List<SchematicElement> matches = new ArrayList<>();
        if (parent == null) throw new MindcodeInternalError("No parent");
        parent.resolvePattern((e, _) -> matches.add(e), context, reference, blockOnly, 0, false);

        if (matches.isEmpty()) {
            context.error(reference, "Cannot resolve block reference '%s'.", reference.fullName());
            return null;
        } else if (matches.size() > 1) {
            context.error(reference, "Ambiguous block reference '%s'.", reference.fullName());
        }

        return matches.getFirst();
    }

    public void resolveReferences(Consumer<SchematicElement> blockConsumer, SchematicsBuilder.ResolverContext context, AstLabel reference,
            boolean blockOnly) {
        if (parent == null) throw new MindcodeInternalError("No parent");
        boolean[] found = new boolean[1];
        parent.resolvePattern((e, _) -> {
            found[0] = true;
            blockConsumer.accept(e);
        }, context, reference, blockOnly, 0, false);

        if (!found[0]) {
            context.error(reference, "Cannot resolve block reference '%s'.", reference.fullName());
        }
    }

    public void resolvePattern(BiConsumer<SchematicElement, String> blockConsumer, SchematicsBuilder.ResolverContext context,
            AstLabel astPattern, boolean blockOnly, int index, boolean multiMatch) {
        boolean last = index == astPattern.getSegmentCount() - 1;
        String pattern = astPattern.getSegment(index);

        // Solve special cases
        switch (pattern) {
            case "global" -> {
                if (index > 0) context.error(astPattern, "'global' must be used at the beginning of the pattern.");
                if (last) {
                    context.error(astPattern, "No block matching pattern specified.");
                } else {
                    context.getSchematic().resolvePattern(blockConsumer, context, astPattern, blockOnly, index + 1, false);
                }
            }

            case "local" -> {
                if (index > 0) context.error(astPattern, "'local' must be used at the beginning of the pattern.");
                if (last) {
                    context.error(astPattern, "No block matching pattern specified.");
                } else {
                    resolvePattern(blockConsumer, context, astPattern, blockOnly, index + 1, false);
                }
            }

            case "parent" -> {
                if (parent == null) {
                    context.error(astPattern, "No parent region exists (top of region hierarchy reached).");
                } else if (last) {
                    context.error(astPattern, "No block matching pattern specified.");
                } else {
                    parent.resolvePattern(blockConsumer, context, astPattern, blockOnly, index + 1, false);
                }
            }

            case "*" -> {
                if (last) {
                    labelMap.forEach((l, e) -> {
                        if (e.valid() && (!blockOnly || e.isBlock())) blockConsumer.accept(e, l);
                    });
                } else {
                    for (SchematicElement element : elements) {
                        if (element.isRegion()) {
                            element.resolvePattern(blockConsumer, context, astPattern, blockOnly, index + 1, false);
                        }
                    }
                }
            }

            case "**" -> {
                if (last) {
                    if (blockOnly) {
                        forEachBlock(e -> e.labels.forEach(l -> blockConsumer.accept(e, l)));
                    } else {
                        forEachElement(e -> e.labels.forEach(l -> blockConsumer.accept(e, l)));
                    }
                } else {
                    for (SchematicElement element : elements) {
                        if (element.isRegion()) {
                            element.resolvePattern(blockConsumer, context, astPattern, blockOnly, index + 1, true);
                        }
                    }
                }
            }

            default -> {
                if (last) {
                    findElements(context.resolveGlobalLabel(astPattern.segments().get(index), pattern),
                            true, !blockOnly, blockConsumer);
                } else {
                    if (index == 0 && !pattern.contains("*")) {
                        SchematicElement scope = this;
                        while (scope != null && !scope.labelMap.containsKey(pattern)) {
                            scope = scope.parent;
                        }
                        if (scope == null) {
                            context.error(astPattern, "No region matching '%s' found.", pattern);
                        } else {
                            scope.labelMap.get(pattern).resolvePattern(blockConsumer, context, astPattern, blockOnly, index + 1, false);
                        }
                    } else {
                        findElements(pattern, false, true, (e, _) ->
                                e.resolvePattern(blockConsumer, context, astPattern, blockOnly, index + 1, false));
                    }
                }
            }
        }

        if (multiMatch) {
            for (SchematicElement element : elements) {
                if (element.isRegion()) {
                    element.resolvePattern(blockConsumer, context, astPattern, blockOnly, index, true);
                }
            }
        }
    }

    private void findElements(String strPattern, boolean blocks, boolean regions, BiConsumer<SchematicElement, String> blockConsumer) {
        if (strPattern.contains("*")) {
            Pattern pattern = Pattern.compile(strPattern.replace ("*", ".*"));
            labelMap.entrySet().stream()
                    .filter(e -> pattern.matcher(e.getKey()).matches() && e.getValue().matches(blocks, regions))
                    .forEach(e -> blockConsumer.accept(e.getValue(), e.getKey()));
        } else {
            SchematicElement element = labelMap.get(strPattern);
            if (element != null && element.matches(blocks, regions)) blockConsumer.accept(element, strPattern);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!labels.isEmpty()) sb.append(labels.getFirst()).append(": ");
        sb.append(blockType == null ? "region" : blockType.name());
        if (origin == null || absolutePosition == null) {
            sb.append(": unresolved, origin offset ").append(originOffset.toStringAbsolute()).append(", position offset ").append(positionOffset.toStringAbsolute());
        } else {
            sb.append(": origin ").append(origin.toStringAbsolute()).append(", position ").append(absolutePosition.toStringAbsolute());
        }
        return sb.toString();
    }
}
