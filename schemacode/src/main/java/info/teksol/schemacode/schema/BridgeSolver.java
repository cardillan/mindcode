package info.teksol.schemacode.schema;

import info.teksol.mindcode.Tuple2;
import info.teksol.schemacode.config.PositionArray;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class BridgeSolver {
    private final SchematicsBuilder builder;
    private final List<Block> blocks;
    private final BlockPositionMap<Block> positionMap;
    private final Set<Tuple2<Integer, Integer>> linkBacks = new HashSet<>();

    private BridgeSolver(SchematicsBuilder builder, List<Block> blocks) {
        this.builder = builder;
        this.blocks = blocks;
        this.positionMap = builder.getPositionMap();
    }

    static void solve(SchematicsBuilder builder, List<Block> blocks) {
        new BridgeSolver(builder, blocks).solve();
    }

    void solve() {
        blocks.forEach(this::processBlock);
    }

    private void processBlock(Block block) {
        switch (block.implementation()) {
            case LIQUIDBRIDGE, BUFFEREDITEMBRIDGE, ITEMBRIDGE -> processConnectedBlock(block, true);
            case MASSDRIVER, PAYLOADMASSDRIVER -> processConnectedBlock(block, false);
        }
    }

    private void processConnectedBlock(Block block, boolean orthogonal) {
        Block linked = getLinkedBlock(block, true);
        if (linked == null) {
            return;
        }

        if (orthogonal) {
            if (!block.position().orthogonal(linked.position())) {
                builder.error("Block '%s' at %s has a connection leading to %s, which is neither horizontal nor vertical.",
                        block.name(), block.position().toStringAbsolute(), linked.position().toStringAbsolute());
            } else if (!inRange(block, linked)) {
                builder.error("Block '%s' at %s has an out-of-range connection to %s.",
                        block.name(), block.position().toStringAbsolute(), linked.position().toStringAbsolute());
            } else {
                Block next = getLinkedBlock(linked, false);
                if (next == block && linkBacks.add(Tuple2.of(Math.min(block.index(), linked.index()), Math.max(block.index(), linked.index())))) {
                    builder.error("Two '%s' blocks at %s and %s connect to each other.",
                            block.name(), block.position().toStringAbsolute(), linked.position().toStringAbsolute());
                }
            }
        } else {
            if (!inRange(block, linked)) {
                builder.error("Block '%s' at %s has an out-of-range connection to %s.",
                        block.name(), block.position().toStringAbsolute(), linked.position().toStringAbsolute());
            }
        }
    }

    private Block getLinkedBlock(Block block, boolean reportErrors) {
        PositionArray links = block.configuration().as(PositionArray.class);
        if (links.size() == 0) {
            return null;
        } else if (links.size() > 1 && reportErrors) {
            builder.error("Block '%s' at %s has more than one connection.", block.name(), block.position().toStringAbsolute());
        }

        Block linked = positionMap.at(links.get(0));
        if (linked == null) {
            return null;
        }

        if (!linked.blockType().equals(block.blockType())) {
            if (reportErrors) {
                builder.error("Block '%s' at %s has a connection leading to a different block type '%s' at %s.",
                        block.name(), block.position().toStringAbsolute(), linked.name(), linked.position().toStringAbsolute());
            }
            return null;
        } else if (linked == block) {
            if (reportErrors) {
                builder.error("Block '%s' at %s has a connection to self.", block.name(), block.position().toStringAbsolute());
            }
            return null;
        }

        return linked;
    }

    private boolean inRange(Block from, Block to) {
        if (from.x() == to.x()) {
            return Math.abs(from.y() - to.y()) < from.blockType().range() + 0.4f;
        } else if (from.y() == to.y()) {
            return Math.abs(from.x() - to.x()) < from.blockType().range() + 0.4f;
        } else {
            double distX = from.x() - to.x();
            double distY = from.y() - to.y();
            return distX * distX + distY * distY < from.blockType().range() * from.blockType().range();
        }
    }
}
