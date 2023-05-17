package info.teksol.schemacode.schema;

import info.teksol.schemacode.mindustry.Position;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public record BlockPositionMap(Map<Integer, Block> blockMap, Map<Integer, Position> positionMap) {

    public Block at(int x, int y) {
        return blockMap.get(Position.pack(x, y));
    }

    public Block at(Position position) {
        return blockMap.get(position.pack());
    }

    public Position translate(Position position) {
        return positionMap.getOrDefault(position.pack(), position);
    }

    private static BlockPositionMap build(Schematics schematics, Function<Block, Position> lowerLeft,
            Function<Block, Position> upperRight, Function<Block, Position> anchor) {
        Map<Integer, Block> blockMap = new HashMap<>();
        Map<Integer, Position> positionMap = new HashMap<>();
        for (Block block : schematics.blocks()) {
            if (block.size() == 1) {
                // No transformation
                int key = block.position().pack();
                blockMap.put(key, block);
                positionMap.put(key, block.position());
            } else {
                Position min = lowerLeft.apply(block);
                Position max = upperRight.apply(block);
                Position blockAnchor = anchor.apply(block);

                for (int x = min.x(); x <= max.x(); x++) {
                    for (int y = min.y(); y <= max.y(); y++) {
                        int key = Position.pack(x, y);
                        blockMap.put(key, block);
                        positionMap.put(key, blockAnchor);
                    }
                }
            }
        }

        return new BlockPositionMap(blockMap, positionMap);
    }

    public static BlockPositionMap forBuilder(Schematics schematics) {
        return build(schematics,
                Block::position,
                b -> b.position().add(b.size() - 1),
                Block::position
        );
    }

    public static BlockPositionMap mindustryToBuilder(Schematics schematics) {
        return build(schematics,
                b -> b.position().sub((b.size() - 1) / 2),
                b -> b.position().add(b.size() / 2),
                b -> b.position().sub((b.size() - 1) / 2)
        );
    }

    public static BlockPositionMap builderToMindustry(Schematics schematics) {
        return build(schematics,
                Block::position,
                b -> b.position().add(b.size() - 1),
                b -> b.position().add((b.size() - 1) / 2)
        );
    }
}
