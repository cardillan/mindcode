package info.teksol.schemacode.schematics;

import info.teksol.mc.util.CollectionUtils;
import info.teksol.mc.util.MutableInteger;
import info.teksol.schemacode.SchematicsInternalError;
import info.teksol.schemacode.ast.AstBlock;
import info.teksol.schemacode.ast.AstSchemaBlock;
import info.teksol.schemacode.ast.AstSchemaRegion;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.schematics.SchematicsBuilder.ResolverContext;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

@NullMarked
public class LayoutResolver {
    public static final char LABEL_ARRAY_CHAR = '#';

    private final SchematicsBuilder builder;

    private final Map<String, SchematicElement> indexedBlocks = new HashMap<>();

    private final Map<String, MutableInteger> arrayLabels = new HashMap<>();

    private int blockIndex = 0;

    public static SchematicElement resolve(SchematicsBuilder builder, List<AstBlock> blocks) {
        return new LayoutResolver(builder).createRegion(null, null, blocks);
    }

    private LayoutResolver(SchematicsBuilder builder) {
        this.builder = builder;
    }

    private SchematicElement convert(SchematicElement parent, AstBlock definition) {
        return switch (definition.element()) {
            case AstSchemaBlock b -> createBlock(parent, definition, b);
            case AstSchemaRegion r -> createRegion(parent, definition, r.blocks());
            //case AstSchemaRegionRef r -> resolve(List.of());
            default -> throw new SchematicsInternalError("Unexpected block type: %s", definition.element());
        };
    }

    private SchematicElement createBlock(@Nullable SchematicElement parent, AstBlock definition, AstSchemaBlock block) {
        return SchematicElement.createBlock(parent, definition, block.type(), blockIndex++);
    }

    private SchematicElement createRegion(@Nullable SchematicElement parent, @Nullable AstBlock definition, List<AstBlock> blocks) {
        SchematicElement region = SchematicElement.createRegion(parent, definition, blockIndex++);
        SchematicElement last = null;

        // Create the basic structure
        for (AstBlock block : blocks) {
            SchematicElement masterElement = convert(region, block);

            List<Position> areaPositions = getAreaPositions(block, masterElement.dimensions());
            List<String> labels = generateLabels(block, areaPositions);

            if (areaPositions.size() == 1) {
                region.addElement(masterElement);
                masterElement.setAnchor(last);
                labels.forEach(label -> region.addLabel(resolveLabel(label), masterElement));
            } else {
                boolean first = true;
                for (Position position : areaPositions) {
                    SchematicElement element = first ? masterElement : masterElement.duplicate(position, () -> blockIndex++);
                    first = false;

                    region.addElement(element);
                    element.setAnchor(last);
                    if (!labels.isEmpty()) {
                        region.addLabel(resolveLabel(labels.removeFirst()), masterElement);
                    }
                }
            }

            last = masterElement;
        }

        // Region structure complete. Resolve positions/dimensions
        ResolverContext context = builder.trackingResolverContext();
        int width = 0, height = 0;
        for (SchematicElement element : region.elements) {
            Position position = element.resolvePosition(context);
            width = Math.max(width, position.x() + element.dimensions().x());
            height = Math.max(height, position.y() + element.dimensions().y());
            element.updateOrigin();
        }

        region.setDimensions(new Position(width, height));
        return region;
    }

    private String resolveLabel(String label) {
        if (label.charAt(label.length() - 1) == LABEL_ARRAY_CHAR) {
            MutableInteger current = arrayLabels.computeIfAbsent(label, k -> MutableInteger.zero());
            return label.substring(0, label.length() - 1) + current.incrementAndGet();
        } else {
            return label;
        }
    }

    private List<String> generateLabels(AstBlock block, List<Position> areaPositions) {
        if (areaPositions.size() > 1) {
            List<String> labels = new ArrayList<>(block.labels());
            if (labels.size() > areaPositions.size()) {
                builder.error(block, "Too many labels defined for block array (array size: %d, assigned labels: %d).",
                        areaPositions.size(), labels.size());
            } else if (labels.size() < areaPositions.size()) {
                int pos = CollectionUtils.lastIndexOf(labels, l -> l.charAt(l.length() - 1) == LABEL_ARRAY_CHAR);
                if (pos >= 0) {
                    int count = areaPositions.size() - labels.size();
                    labels.addAll(pos, Collections.nCopies(count, labels.get(pos)));
                }
            }
            return labels;
        } else {
            return block.labels();
        }
    }

    private List<Position> getAreaPositions(AstBlock block, Position dimensions) {
        List<Position> result = switch (block.position().blockArrayType()) {
            case SINGLE -> List.of(block.anchor().coordinates());
            case INCLUSIVE -> computeRange(block, dimensions, true);
            case EXCLUSIVE -> computeRange(block, dimensions, false);
            case AREA -> computeArea(block, dimensions, Objects.requireNonNull(block.position().extension()).coordinates());
        };

        if (result.isEmpty()) {
            builder.error(block, "The block array is empty.");
        }

        return result;
    }

    private List<Position> computeRange(AstBlock astBlock, Position dimensions, boolean inclusive) {
        Position start = Objects.requireNonNull(astBlock.position().anchor()).coordinates();
        Position end = Objects.requireNonNull(astBlock.position().extension()).coordinates();

        int signX = end.x() < start.x() ? -1 : 1;
        int signY = end.y() < start.y() ? -1 : 1;
        int width = (end.x() - start.x()) / dimensions.x() + (inclusive ? signX : 0);
        int height = (end.y() - start.y()) / dimensions.y() + (inclusive ? signY : 0);

        return computeArea(astBlock, dimensions, new Position(width, height));
    }

    private List<Position> computeArea(AstBlock astBlock, Position dimensions, Position area) {
        if (area.emptyArea()) return List.of();

        int width = Math.abs(area.x());
        int height = Math.abs(area.y());
        int stepX = area.x() < 0 ? -dimensions.x() : dimensions.x();
        int stepY = area.y() < 0 ? -dimensions.y() : dimensions.y();
        Position anchor = astBlock.anchor().coordinates();

        List<Position> result = new ArrayList<>();
        if (astBlock.position().horizontal()) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    result.add(new Position(x * stepX, y * stepY));
                }
            }
        } else {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    result.add(new Position(x * stepX, y * stepY));
                }
            }
        }
        return result;
    }
}
