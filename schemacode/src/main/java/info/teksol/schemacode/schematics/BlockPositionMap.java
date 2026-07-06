package info.teksol.schemacode.schematics;

import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.messages.ToolMessage;
import info.teksol.mc.util.Tuple2;
import info.teksol.schemacode.ast.PlacementMode;
import info.teksol.schemacode.mindustry.Position;
import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.function.Function;

@NullMarked
public record BlockPositionMap<T extends BlockPosition>(Map<Integer, T> blockMap, Map<Integer, Position> positionMap) {

    public T at(Position position) {
        return blockMap.get(position.pack());
    }

    public Position translate(Position position) {
        return positionMap.getOrDefault(position.pack(), position);
    }

    public static <T extends BlockPosition> BlockPositionMap<T> forBuilder(MessageConsumer messageListener, List<T> blocks) {
        return new Builder<>(messageListener, blocks,
                BlockPosition::position,
                b -> b.position().add(b.size() - 1),
                BlockPosition::position
        ).build();
    }

    public static BlockPositionMap<Block> mindustryToBuilder(MessageConsumer messageListener, List<Block> blocks) {
        return new Builder<>(messageListener, blocks,
                b -> b.position().sub((b.size() - 1) / 2),
                b -> b.position().add(b.size() / 2),
                b -> b.position().sub((b.size() - 1) / 2)
        ).build();
    }

    public static BlockPositionMap<Block> builderToMindustry(MessageConsumer messageListener, List<Block> blocks) {
        return new Builder<>(messageListener, blocks,
                Block::position,
                b -> b.position().add(b.size() - 1),
                b -> b.position().add((b.size() - 1) / 2)
        ).build();
    }

    private static final class Builder<T extends BlockPosition> {
        private final MessageConsumer messageListener;
        private final List<T> blocks;
        private final Function<T, Position> lowerLeft;
        private final Function<T, Position> upperRight;
        private final Function<T, Position> anchor;
        private final Map<Integer, T> blockMap = new HashMap<>();
        private final Map<Integer, Position> positionMap = new HashMap<>();
        private final Set<Tuple2<T, T>> collisions = new HashSet<>();

        private Builder(MessageConsumer messageListener, List<T> blocks, Function<T, Position> lowerLeft,
                Function<T, Position> upperRight, Function<T, Position> anchor) {
            this.messageListener = messageListener;
            this.blocks = blocks;
            this.lowerLeft = lowerLeft;
            this.upperRight = upperRight;
            this.anchor = anchor;
        }

        BlockPositionMap<T> build() {
            MainLoop: for (T block : blocks) {
                if (!block.valid() || block.position().invalid()) continue;

                if (block.size() == 1) {
                    // No transformation
                    int key = block.position().pack();
                    checkCollision(key, block, anchor.apply(block));
                } else {
                    Position min = lowerLeft.apply(block);
                    Position max = upperRight.apply(block);
                    Position blockAnchor = anchor.apply(block);

                    for (int x = min.x(); x <= max.x(); x++) {
                        for (int y = min.y(); y <= max.y(); y++) {
                            int key = Position.pack(x, y);
                            if (checkCollision(key, block, blockAnchor)) continue MainLoop;
                        }
                    }
                }
            }

            collisions.forEach(t -> messageListener.accept(ToolMessage.error(
                    "Overlapping blocks: #%d '%s' at %s and #%d '%s' at %s.",
                    t.e1().index(), t.e1().name(), t.e1().area(),
                    t.e2().index(), t.e2().name(), t.e2().area())));

            return new BlockPositionMap<>(blockMap, positionMap);
        }

        // If a collision results in a block invalidation (the current one or the previous one), the invalid block
        // is removed from the maps.
        // Returns true if the block is invalid
        private boolean checkCollision(int key, T block, Position anchor) {
            // Invalid blocks do not count at all
            if (!block.valid()) return true;

            // Invalid blocks shouldn't be included in maps
            T previous = blockMap.get(key);
            if (previous != null && previous != block) {
                if (block.placementMode() == PlacementMode.REPLACE) {
                    invalidate(previous);
                } else if (block.placementMode() == PlacementMode.FILL) {
                    invalidate(block);
                    return true;
                } else {
                    collisions.add(block.index() < previous.index() ? Tuple2.of(block, previous) : Tuple2.of(previous, block));
                }
            }
            blockMap.put(key, block);
            positionMap.put(key, anchor);
            return false;
        }

        private void invalidate(T block) {
            Position min = lowerLeft.apply(block);
            Position max = upperRight.apply(block);

            for (int x = min.x(); x <= max.x(); x++) {
                for (int y = min.y(); y <= max.y(); y++) {
                    int key = Position.pack(x, y);
                    if (blockMap.get(key) == block) {
                        blockMap.remove(key);
                        positionMap.remove(key);
                    }
                }
            }

            block.invalidate();
        }
    }
}
