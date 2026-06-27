package info.teksol.schemacode.schematics;

import info.teksol.mc.common.SourceElement;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.compiler.PositionalMessage;
import info.teksol.mc.util.MutableInteger;
import info.teksol.schemacode.SchematicsInternalError;
import info.teksol.schemacode.ast.AstBlock;
import info.teksol.schemacode.ast.AstSchemaBlock;
import info.teksol.schemacode.ast.AstSchemaRegion;
import info.teksol.schemacode.mindustry.Position;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

@NullMarked
public class LayoutResolver {
    public static final char INDEX_KEY_CHAR = '$';
    public static final char LABEL_ARRAY_CHAR = '#';

    private final MessageConsumer messageConsumer;
    private final Set<String> reported = new HashSet<>();
    private final Map<String, MutableInteger> arrayLabels = new HashMap<>();

    private int blockIndex = 0;

    public LayoutResolver(MessageConsumer messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    public SchematicElement resolve(List<AstBlock> blocks) {
        SchematicElement schematic = createRegion(null, blocks, Map.of(), "");
        schematic.updateOrigin(Position.ORIGIN);
        return schematic;
    }

    private SchematicElement createBlock(AstBlock definition, AstSchemaBlock block, Map<String, SchematicElement> labelMap, String lastReference) {
        return SchematicElement.createBlock(definition, block.type(), blockIndex++, labelMap, lastReference);
    }

    private SchematicElement createRegion(@Nullable AstBlock definition, List<AstBlock> blocks,
            Map<String, SchematicElement> enclosingLabelMap, String lastReference) {
        Map<String, SchematicElement> currentLabelMap = new HashMap<>();
        List<SchematicElement> elements = new ArrayList<>();
        int index = 0;

        // Create the basic structure
        // TODO: no arrays yet
        for (AstBlock block : blocks) {
            String lastBlock = "" + INDEX_KEY_CHAR + (index - 1);
            SchematicElement element = convert(block, currentLabelMap, lastBlock);
            elements.add(element);
            block.labels().forEach(label -> currentLabelMap.put(label, element));
            index++;
        }

        // Region structure complete. Resolve positions/dimensions
        Context context = new Context();
        int width = 0, height = 0;
        for (SchematicElement element : elements) {
            Position position = element.resolvePosition(context);
            width = Math.max(width, position.x() + element.dimensions().x());
            height = Math.max(height, position.y() + element.dimensions().y());
        }

        Position dimensions = new Position(width, height);
        SchematicElement region = SchematicElement.create(definition, dimensions, elements, currentLabelMap, enclosingLabelMap, lastReference);
        elements.forEach(e -> e.setParent(region));
        return region;
    }

    private SchematicElement convert(AstBlock definition, Map<String, SchematicElement> labelMap, String lastReference) {
        return switch (definition.element()) {
            case AstSchemaBlock b -> createBlock(definition, b, labelMap, lastReference);
            case AstSchemaRegion r -> createRegion(definition, r.blocks(), labelMap, lastReference);
            //case AstSchemaRegionRef r -> resolve(List.of());
            default -> throw new SchematicsInternalError("Unexpected block type: %s", definition.element());
        };
    }

    public class Context {
        private final Set<String> visited = new HashSet<>();

        public boolean visited(String reference) {
            return !visited.add(reference);
        }

        public boolean unreported(String reference) {
            return reported.add(reference);
        }

        public void error(SourceElement node, @PrintFormat String format, Object... args) {
            messageConsumer.accept(PositionalMessage.error(node.sourcePosition(), format, args));
        }
    }
}
