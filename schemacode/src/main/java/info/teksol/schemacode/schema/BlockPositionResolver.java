package info.teksol.schemacode.schema;

import info.teksol.mindcode.compiler.CompilerMessage;
import info.teksol.mindcode.mimex.BlockType;
import info.teksol.schemacode.SchemacodeMessage;
import info.teksol.schemacode.SchematicsInternalError;
import info.teksol.schemacode.ast.AstBlock;
import info.teksol.schemacode.mindustry.Position;
import org.intellij.lang.annotations.PrintFormat;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BlockPositionResolver {

    private final Consumer<CompilerMessage> messageListener;

    private final Set<String> circularBlocks = new HashSet<>();

    public BlockPositionResolver(Consumer<CompilerMessage> messageListener) {
        this.messageListener = messageListener;
    }

    public Map<String, BlockPosition> resolveAllBlocks(List<AstBlock> blocks) {
        Map<String, RelativeBlockPosition> relativeBlocks = new HashMap<>();
        int index = 0;
        for (AstBlock astBlock : blocks) {
            RelativeBlockPosition blockPosition = new RelativeBlockPosition(index, astBlock, "#" + (index - 1));
            relativeBlocks.put("#" + index, blockPosition);
            astBlock.labels().forEach(l -> relativeBlocks.put(l, blockPosition));
            index++;
        }

        return relativeBlocks.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> resolve(relativeBlocks, e.getValue())));
    }

    private void error(@PrintFormat String format, Object... args) {
        messageListener.accept(SchemacodeMessage.error(String.format(format, args)));
    }

    private BlockPosition resolve(Map<String, RelativeBlockPosition> blocks, RelativeBlockPosition block) {
        return resolve(blocks, new HashSet<>(), block).toBlockPosition();
    }

    private RelativeBlockPosition resolve(Map<String, RelativeBlockPosition> blocks, Set<String> visited, RelativeBlockPosition block) {
        if (block.reference == null) {
            // This is absolute position
            return block;
        } else {
            RelativeBlockPosition refBlock = blocks.get(block.reference);
            if (refBlock == null) {
                error("Unknown block name '%s'.", block.reference);
                return block.resetPosition();
            } else if (!visited.add(block.reference)) {
                if (circularBlocks.add(block.reference)) {
                    error("Circular definition of block '%s' position.", block.reference);
                }
                return block.resetPosition();
            } else {
                return block.shiftPosition(resolve(blocks, visited, refBlock));
            }
        }
    }

    public record AstBlockPosition(int index, BlockType blockType, Position position) implements BlockPosition {
    }

    private record RelativeBlockPosition(int index, BlockType blockType, String reference, Position position) {
        public RelativeBlockPosition(int index, AstBlock astBlock, String lastBlock) {
            this(index, BlockType.forName(astBlock.type()),
                    astBlock.position().relative()
                            ? astBlock.position().getRelativeTo() == null ? lastBlock : astBlock.position().getRelativeTo()
                            : null,
                    astBlock.position().coordinates());
        }

        private RelativeBlockPosition shiftPosition(RelativeBlockPosition refBlock) {
            return new RelativeBlockPosition(index, blockType, null, position.add(refBlock.position));
        }

        private RelativeBlockPosition resetPosition() {
            return new RelativeBlockPosition(index, blockType, null, Position.ORIGIN);
        }

        private BlockPosition toBlockPosition() {
            if (reference != null) {
                throw new SchematicsInternalError("Cannot convert relative block position %s to absolute.", this);
            }
            return new AstBlockPosition(index, blockType, position);
        }
    }
}
