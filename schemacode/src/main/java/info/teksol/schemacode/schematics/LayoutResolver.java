package info.teksol.schemacode.schematics;

import info.teksol.mc.mindcode.logic.mimex.BlockType;
import info.teksol.mc.util.CollectionUtils;
import info.teksol.mc.util.MutableInteger;
import info.teksol.schemacode.SchematicsInternalError;
import info.teksol.schemacode.SchematicsMetadata;
import info.teksol.schemacode.ast.*;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.schematics.SchematicsBuilder.ResolverContext;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.IntSupplier;

@NullMarked
public class LayoutResolver {
    public static final char GLOBAL_LABEL_ARRAY_CHAR = '#';
    public static final char LOCAL_LABEL_ARRAY_CHAR = '$';

    private final SchematicsBuilder builder;

    private final Map<String, SchematicElement> namedRegions = new HashMap<>();
    private final Map<String, MutableInteger> arrayLabels = new HashMap<>();

    private int blockIndex = 0;

    public LayoutResolver(SchematicsBuilder builder) {
        this.builder = builder;
    }

    public SchematicElement resolve(AstSchematic astSchematic) {
        for (AstRegionDefinition def : astSchematic.regions()) {
            AstSchemaRegion astSchemaRegion = def.region();
            SchematicElement region = createRegion(null, null, astSchemaRegion.dimensions(), astSchemaRegion.blocks(), () -> 0);
            namedRegions.put(def.name(), region);
        }

        return createRegion(null, null, null, astSchematic.blocks(), () -> blockIndex++);
    }

    public BiFunction<SchematicElement, String, String> globalLabelResolver() {
        return this::resolveLabel;
    }

    private SchematicElement convert(SchematicElement parent, AstBlock definition, IntSupplier indexSupplier) {
        return switch (definition.element()) {
            case AstSchemaBlock b -> createBlock(parent, definition, b.type(), indexSupplier);
            case AstSchemaRegion r -> createRegion(parent, definition, r.dimensions(), r.blocks(), indexSupplier);
            case AstSchemaRegionRef r -> copyNamedRegion(parent, definition, r);
            default -> throw new SchematicsInternalError("Unexpected block type: %s", definition.element());
        };
    }

    private BlockType defaultBlockType() {
        return Objects.requireNonNull(SchematicsMetadata.getMetadata().getBlockByName("@copper-wall"));
    }

    private SchematicElement copyNamedRegion(SchematicElement parent, AstBlock definition, AstSchemaRegionRef r) {
        SchematicElement region = namedRegions.get(r.regionReference());
        if (region == null) {
            builder.error(definition, "Unknown named region '%s'.", r.regionReference());
            return SchematicElement.createBlock(parent, definition, defaultBlockType(), 0);
        } else {
            SchematicElement copy = region.duplicateInto(parent, definition, () -> blockIndex++);
            copy.translate(SchematicElement.translation(definition));
            copy.rotate(SchematicElement.direction(definition));
            return copy;
        }
    }

    private SchematicElement createBlock(@Nullable SchematicElement parent, AstBlock definition, String type, IntSupplier indexSupplier) {
        BlockType blockType = SchematicsMetadata.getMetadata().getBlockByName(type);
        if (blockType == null) {
            builder.error(definition, "Unknown block type '%s'.", type);
            blockType = defaultBlockType();
        } else if ("hidden".equals(blockType.visibility()) && !blockType.isAir()) {
            builder.error(definition, "Block type '%s' cannot be built.", type);
            blockType = defaultBlockType();
        }

        return SchematicElement.createBlock(parent, definition, blockType, indexSupplier.getAsInt());
    }

    private SchematicElement createRegion(@Nullable SchematicElement parent, @Nullable AstBlock definition,
            @Nullable AstDimensions dimensions, List<AstBlock> blocks, IntSupplier indexSupplier) {
        SchematicElement region = SchematicElement.createRegion(parent, definition, parent == null ? 0 : blockIndex++);
        SchematicElement last = null;

        Map<String, MutableInteger> localLabels = new HashMap<>();

        // Create the basic structure
        for (AstBlock block : blocks) {
            SchematicElement masterElement = convert(region, block, indexSupplier);

            List<Position> areaPositions = getAreaPositions(block, masterElement.dimensions());
            List<String> labels = generateLabels(block, areaPositions);

            if (areaPositions.size() == 1) {
                region.addElement(masterElement);
                masterElement.setDefaultAnchor(last);
                masterElement.resolveLabels((_, label) -> resolveLocalLabel(localLabels, label));
                labels.forEach(label -> region.addLabel(label, masterElement));
            } else {
                boolean first = true;
                ArrayList<SchematicElement> placedElements = new ArrayList<>();
                for (Position position : areaPositions) {
                    SchematicElement element = first ? masterElement : masterElement.duplicateAt(position, indexSupplier);
                    first = false;

                    region.addElement(element);
                    element.setDefaultAnchor(last);
                    if (!labels.isEmpty()) {
                        region.addLabel(labels.removeFirst(), element);
                    }

                    if (element.isRegion()) {
                        placedElements.add(element);
                    }
                }

                placedElements.forEach(e -> e.resolveLabels((_, label) -> resolveLocalLabel(localLabels, label)));
            }

            last = masterElement;
        }

        region.resolveLabels((_, label) -> resolveLocalLabel(localLabels, label));

        // Region structure complete. Resolve positions/dimensions
        ResolverContext context = builder.trackingResolverContext();
        int width = 0, height = 0;
        for (SchematicElement element : region.elements) {
            Position position = element.resolvePosition(context);
            width = Math.max(width, position.x() + element.dimensions().x());
            height = Math.max(height, position.y() + element.dimensions().y());
            element.updateOrigin();
        }

        region.setDimensions(dimensions == null ? new Position(width, height) : dimensions.toPosition());
        region.translate(SchematicElement.translation(definition));
        region.rotate(SchematicElement.direction(definition));
        return region;
    }

    private String resolveLabel(SchematicElement element, String label) {
        if (label.charAt(label.length() - 1) == GLOBAL_LABEL_ARRAY_CHAR) {
            MutableInteger current = arrayLabels.computeIfAbsent(label, k -> MutableInteger.zero());
            return label.substring(0, label.length() - 1) + (element.valid() ? current.incrementAndGet() : current.get());
        } else {
            return label;
        }
    }

    private String resolveLocalLabel(Map<String, MutableInteger> arrayLabels, String label) {
        if (label.charAt(label.length() - 1) == LOCAL_LABEL_ARRAY_CHAR) {
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
                int pos = CollectionUtils.lastIndexOf(labels,
                        l -> l.charAt(l.length() - 1) == GLOBAL_LABEL_ARRAY_CHAR || l.charAt(l.length() - 1) == LOCAL_LABEL_ARRAY_CHAR);
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
            case SINGLE -> List.of(block.coordinates().coordinates());
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
        Position start = Objects.requireNonNull(astBlock.position().coordinates()).coordinates();
        Position end = Objects.requireNonNull(astBlock.position().extension()).coordinates();

        int corrX = end.x() < start.x() ? dimensions.x() -1 : 0;
        int corrY = end.y() < start.y() ? dimensions.y() -1 : 0;
        int signX = end.x() < start.x() ? -1 : 1;
        int signY = end.y() < start.y() ? -1 : 1;
        int width = (end.x() - start.x() - corrX) / dimensions.x() + (inclusive ? signX : 0);
        int height = (end.y() - start.y() - corrY) / dimensions.y() + (inclusive ? signY : 0);

        return computeArea(astBlock, dimensions, new Position(width, height));
    }

    private List<Position> computeArea(AstBlock astBlock, Position dimensions, Position area) {
        if (area.emptyArea()) return List.of();

        int width = Math.abs(area.x());
        int height = Math.abs(area.y());
        int stepX = area.x() < 0 ? -dimensions.x() : dimensions.x();
        int stepY = area.y() < 0 ? -dimensions.y() : dimensions.y();

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
