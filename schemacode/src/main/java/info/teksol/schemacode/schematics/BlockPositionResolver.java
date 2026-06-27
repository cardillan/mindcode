package info.teksol.schemacode.schematics;

import info.teksol.mc.common.SourceElement;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.compiler.PositionalMessage;
import info.teksol.mc.mindcode.logic.mimex.BlockType;
import info.teksol.mc.util.CollectionUtils;
import info.teksol.mc.util.MutableInteger;
import info.teksol.schemacode.SchematicsInternalError;
import info.teksol.schemacode.SchematicsMetadata;
import info.teksol.schemacode.ast.AstBlock;
import info.teksol.schemacode.ast.AstSchemaBlock;
import info.teksol.schemacode.ast.AstSchemaRegion;
import info.teksol.schemacode.ast.AstSchemaRegionRef;
import info.teksol.schemacode.mindustry.Position;
import org.intellij.lang.annotations.PrintFormat;

import java.util.*;

public class BlockPositionResolver {
    public static final char LABEL_ARRAY_CHAR = '#';

    private final MessageConsumer messageListener;

    private final Set<String> circularBlocks = new HashSet<>();

    private final Map<String, MutableInteger> arrayLabels = new HashMap<>();

    public BlockPositionResolver(MessageConsumer messageListener) {
        this.messageListener = messageListener;
    }

    public record Region(List<AstBlockWithPosition> definitions, Map<String, BlockPosition> labelMap, Position size) {}

    private String resolveLabel(String label) {
        if (label.charAt(label.length() - 1) == LABEL_ARRAY_CHAR) {
            MutableInteger current = arrayLabels.computeIfAbsent(label, k -> MutableInteger.zero());
            return label.substring(0, label.length() - 1) + current.incrementAndGet();
        } else {
            return label;
        }
    }

    private void error(SourceElement node, @PrintFormat String format, Object... args) {
        messageListener.accept(PositionalMessage.error(node.sourcePosition(), format, args));
    }

    private AstBlockWithPosition resolve(Map<String, RelativeBlockPosition> blocks, RelativeBlockPosition block) {
        return resolve(blocks, new HashSet<>(), block).toBlockPosition();
    }

    private List<Position> getAreaPositions(AstBlock astBlock, Position dimensions) {
        return switch (astBlock.position().blockArrayType()) {
            case SINGLE -> List.of(astBlock.anchor().coordinates());
            case INCLUSIVE -> computeRange(astBlock, dimensions, true);
            case EXCLUSIVE -> computeRange(astBlock, dimensions, false);
            case AREA -> computeArea(astBlock, dimensions, Objects.requireNonNull(astBlock.position().extension()).coordinates());
        };
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
                    result.add(anchor.add(x * stepX, y * stepY));
                }
            }
        } else {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    result.add(anchor.add(x * stepX, y * stepY));
                }
            }
        }
        return result;
    }

    private RelativeBlockPosition resolve(Map<String, RelativeBlockPosition> blocks, Set<String> visited, RelativeBlockPosition block) {
        if (block.reference == null) {
            // This is an absolute position
            return block;
        } else {
            RelativeBlockPosition refBlock = blocks.get(block.reference);
            if (refBlock == null) {
                error(block.astBlock, "Unknown block name '%s'.", block.reference);
                return block.resetPosition();
            } else if (!visited.add(block.reference)) {
                if (circularBlocks.add(block.reference)) {
                    error(block.astBlock, "Circular definition of block '%s' position.", block.reference);
                }
                return block.resetPosition();
            } else {
                return block.shiftPosition(resolve(blocks, visited, refBlock));
            }
        }
    }

    public record AstBlockWithPosition(AstBlock astBlock, int index, BlockType blockType,
                                       Position position) implements BlockPosition {
        public AstBlockWithPosition {
            if (index < 0) throw new IllegalArgumentException("Index must be greater than or equal to 0.");
        }
    }

    private record RelativeBlockPosition(int index, AstBlock astBlock, BlockType blockType, String reference,
                                         Position position) {
        public RelativeBlockPosition(int index, AstBlock astBlock, String lastBlock, Position position) {
            this(index, astBlock, SchematicsMetadata.getMetadata().getBlockByName(astBlock.type()),
                    astBlock.anchor().relative()
                            ? astBlock.anchor().relativeTo() == null ? lastBlock : astBlock.anchor().relativeTo()
                            : null,
                    position);
        }

        private RelativeBlockPosition shiftPosition(RelativeBlockPosition refBlock) {
            return new RelativeBlockPosition(index, astBlock, blockType, null, position.add(refBlock.position));
        }

        private RelativeBlockPosition resetPosition() {
            return new RelativeBlockPosition(index, astBlock, blockType, null, Position.ORIGIN);
        }

        private AstBlockWithPosition toBlockPosition() {
            if (reference != null) {
                throw new SchematicsInternalError("Cannot convert relative block position %s to absolute.", this);
            }
            return new AstBlockWithPosition(astBlock, index, blockType, position);
        }
    }

    public Region expandBlock(AstBlock block) {
        return switch (block.element()) {
            case AstSchemaBlock b -> resolveSingleBlock(block, b.type());
            case AstSchemaRegion r -> resolveRegion(r.blocks());
            case AstSchemaRegionRef r -> resolveRegion(List.of());  // todo
            default -> throw new SchematicsInternalError("Unexpected block type: %s", block.element());
        };
    }

    public Region resolveSingleBlock(AstBlock block, String type) {
        BlockType blockType = SchematicsMetadata.getMetadata().getBlockByName(type);
        int size = blockType == null ? 1 : blockType.size();            // Must not happen
        AstBlockWithPosition blockPosition = new AstBlockWithPosition(block, 0, blockType, Position.ORIGIN);
        return new Region(List.of(blockPosition), Map.of(), new Position(size, size));
    }

    public Region resolveRegion(List<AstBlock> blocks) {
        Map<String, RelativeBlockPosition> relativeBlocks = new LinkedHashMap<>();
        int index = 0;
        for (AstBlock astBlock : blocks) {
            Region expanded = expandBlock(astBlock);

            String lastBlock = "" + SchematicsBuilder.INDEX_KEY_CHAR + (index - 1);
            List<Position> areaPositions = getAreaPositions(astBlock, expanded.size);
            if (areaPositions.isEmpty()) {
                error(astBlock, "The block array is empty.");
            }
            List<String> labels;
            if (areaPositions.size() > 1) {
                labels = new ArrayList<>(astBlock.labels());
                if (labels.size() > areaPositions.size()) {
                    error(astBlock, "Too many labels defined for block array (array size: %d, assigned labels: %d).",
                            areaPositions.size(), labels.size());
                } else if (labels.size() < areaPositions.size()) {
                    int pos = CollectionUtils.lastIndexOf(labels, l -> l.charAt(l.length() - 1) == LABEL_ARRAY_CHAR);
                    if (pos >= 0) {
                        int count = areaPositions.size() - labels.size();
                        labels.addAll(pos, Collections.nCopies(count, labels.get(pos)));
                    }
                }
            } else {
                labels = List.of();
            }

            for (Position position : areaPositions) {
                for (AstBlockWithPosition positioned : expanded.definitions()) {
                    RelativeBlockPosition blockPosition = new RelativeBlockPosition(index, positioned.astBlock(), lastBlock, position.add(positioned.position()));
                    relativeBlocks.put("" + SchematicsBuilder.INDEX_KEY_CHAR + index, blockPosition);
                    if (areaPositions.size() == 1) {
                        astBlock.labels().forEach(label -> relativeBlocks.put(resolveLabel(label), blockPosition));
                    } else if (!labels.isEmpty()) {
                        relativeBlocks.put(resolveLabel(labels.removeFirst()), blockPosition);
                    }
                    index++;
                }
            }
        }

        int width = 0, height = 0;
        List<AstBlockWithPosition> definitions = new ArrayList<>();
        Map<String, BlockPosition> labelMap = new HashMap<>();
        for (Map.Entry<String, RelativeBlockPosition> entry : relativeBlocks.entrySet()) {
            String label = entry.getKey();
            RelativeBlockPosition position = entry.getValue();
            AstBlockWithPosition blockPosition = resolve(relativeBlocks, position);
            width = Math.max(width, blockPosition.position().x() + blockPosition.blockType().size());
            height = Math.max(height, blockPosition.position().y() + blockPosition.blockType().size());
            if (label.charAt(0) == SchematicsBuilder.INDEX_KEY_CHAR) {
                definitions.add(blockPosition);
            }
            labelMap.put(label, blockPosition);
        }

        return new Region(definitions, labelMap, new Position(width, height));
    }
}
