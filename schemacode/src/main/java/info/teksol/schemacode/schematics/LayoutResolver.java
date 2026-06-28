package info.teksol.schemacode.schematics;

import info.teksol.mc.util.MutableInteger;
import info.teksol.schemacode.SchematicsInternalError;
import info.teksol.schemacode.ast.AstBlock;
import info.teksol.schemacode.ast.AstSchemaBlock;
import info.teksol.schemacode.ast.AstSchemaRegion;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.schematics.SchematicsBuilder.ResolverContext;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NullMarked
public class LayoutResolver {
    public static final char LABEL_ARRAY_CHAR = '#';

    private final SchematicsBuilder builder;

    private final Map<String, SchematicElement> indexedBlocks = new HashMap<>();

    private final Map<String, MutableInteger> arrayLabels = new HashMap<>();

    private int blockIndex = 0;

    public LayoutResolver(SchematicsBuilder builder) {
        this.builder = builder;
    }

    public SchematicElement resolve(List<AstBlock> blocks) {
        SchematicElement schematic = createRegion(null, blocks);
        schematic.updateOrigin(Position.ORIGIN);
        return schematic;
    }

    private SchematicElement createBlock(AstBlock definition, AstSchemaBlock block) {
        return SchematicElement.createBlock(definition, block.type(), blockIndex++);
    }

    private SchematicElement createRegion(@Nullable AstBlock definition, List<AstBlock> blocks) {
        Map<String, SchematicElement> currentLabelMap = new HashMap<>();
        List<SchematicElement> elements = new ArrayList<>();
        SchematicElement last = null;

        // Create the basic structure
        // TODO: no arrays yet
        for (AstBlock block : blocks) {
            SchematicElement element = convert(block);
            elements.add(element);
            block.labels().forEach(label -> currentLabelMap.put(label, element));
            element.setAnchor(last);
            last = element;
        }

        // Region structure complete. Resolve positions/dimensions
        ResolverContext context = builder.trackingResolverContext();
        int width = 0, height = 0;
        for (SchematicElement element : elements) {
            Position position = element.resolvePosition(context);
            width = Math.max(width, position.x() + element.dimensions().x());
            height = Math.max(height, position.y() + element.dimensions().y());
        }

        Position dimensions = new Position(width, height);
        SchematicElement region = SchematicElement.createRegion(definition, dimensions, blockIndex++, elements, currentLabelMap);
        elements.forEach(e -> e.setParent(region));
        return region;
    }

    private SchematicElement convert(AstBlock definition) {
        return switch (definition.element()) {
            case AstSchemaBlock b -> createBlock(definition, b);
            case AstSchemaRegion r -> createRegion(definition, r.blocks());
            //case AstSchemaRegionRef r -> resolve(List.of());
            default -> throw new SchematicsInternalError("Unexpected block type: %s", definition.element());
        };
    }
}
