package info.teksol.schemacode.schematics;

import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.Tuple2;
import info.teksol.schemacode.SchemacodeCompilerMessage;
import info.teksol.schemacode.mindustry.Position;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public record BlockPositionMap<T extends BlockPosition>(Map<Integer, T> blockMap, Map<Integer, Position> positionMap) {

    public T at(Position position) {
        return blockMap.get(position.pack());
    }

    public Position translate(Position position) {
        return positionMap.getOrDefault(position.pack(), position);
    }

    private static <T extends BlockPosition> BlockPositionMap<T> build(Consumer<MindcodeMessage> messageListener,
            List<T> blocks, Function<T, Position> lowerLeft, Function<T, Position> upperRight, Function<T, Position> anchor) {
        Set<Tuple2<T, T>> collisions = new HashSet<>();
        Map<Integer, T> blockMap = new HashMap<>();
        Map<Integer, Position> positionMap = new HashMap<>();
        for (T block : blocks) {
            if (block.size() == 1) {
                // No transformation
                int key = block.position().pack();
                checkCollision(block, blockMap.put(key, block), collisions);
                positionMap.put(key, block.position());
            } else {
                Position min = lowerLeft.apply(block);
                Position max = upperRight.apply(block);
                Position blockAnchor = anchor.apply(block);

                for (int x = min.x(); x <= max.x(); x++) {
                    for (int y = min.y(); y <= max.y(); y++) {
                        int key = Position.pack(x, y);
                        checkCollision(block, blockMap.put(key, block), collisions);
                        positionMap.put(key, blockAnchor);

                    }
                }
            }
        }

        if (!collisions.isEmpty()) {
            collisions.forEach(t -> messageListener.accept(SchemacodeCompilerMessage.error(
                    "Overlapping blocks: #%d '%s' at %s and #%d '%s' at %s.".formatted(
                            t.e1().index(), t.e1().name(), t.e1().area(),
                            t.e2().index(), t.e2().name(), t.e2().area()))));
        }

        return new BlockPositionMap<>(blockMap, positionMap);
    }

    private static  <T extends BlockPosition> void checkCollision(T block, T previous, Set<Tuple2<T, T>> collisions) {
        if (previous != null && previous != block) {
            collisions.add(block.index() < previous.index() ? Tuple2.of(block, previous) : Tuple2.of(previous, block));
        }
    }

    public static <T extends BlockPosition> BlockPositionMap<T> forBuilder(Consumer<MindcodeMessage> messageListener, List<T> blocks) {
        return build(messageListener, blocks,
                BlockPosition::position,
                b -> b.position().add(b.size() - 1),
                BlockPosition::position
        );
    }

    public static BlockPositionMap<Block> mindustryToBuilder(Consumer<MindcodeMessage> messageListener, List<Block> blocks) {
        return build(messageListener, blocks,
                b -> b.position().sub((b.size() - 1) / 2),
                b -> b.position().add(b.size() / 2),
                b -> b.position().sub((b.size() - 1) / 2)
        );
    }

    public static BlockPositionMap<Block> builderToMindustry(Consumer<MindcodeMessage> messageListener, List<Block> blocks) {
        return build(messageListener, blocks,
                Block::position,
                b -> b.position().add(b.size() - 1),
                b -> b.position().add((b.size() - 1) / 2)
        );
    }
}
