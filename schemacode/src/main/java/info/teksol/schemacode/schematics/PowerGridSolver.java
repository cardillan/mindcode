package info.teksol.schemacode.schematics;

import info.teksol.schemacode.SchematicsInternalError;
import info.teksol.schemacode.config.PositionArray;
import info.teksol.schemacode.mindustry.Position;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class PowerGridSolver {
    private final SchematicsBuilder builder;
    private final BlockPositionMap<Block> positionMap;

    private PowerGridSolver(SchematicsBuilder builder) {
        this.builder = builder;
        this.positionMap = builder.getPositionMap();
    }

    static void solve(SchematicsBuilder builder, List<Block> blocks) {
        new PowerGridSolver(builder).solve(blocks);
    }

    private void solve(List<Block> blocks) {
        Map<Block, Set<Block>> powerNodes = blocks.stream().filter(this::isPowerNode)
                .collect(Collectors.toMap(b -> b, this::collectLinks));

        powerNodes.forEach((block, links) ->
                links.forEach(linkedBlock -> {
                    if (isPowerNode(linkedBlock)) {
                        Set<Block> linkedBlockLinks = powerNodes.get(linkedBlock);
                        if (linkedBlockLinks == null) {
                            throw new SchematicsInternalError("Linked power node not contained in powerNodes map.");
                        } else {
                            // Ensure the reciprocal link is there
                            linkedBlockLinks.add(block);
                        }
                    }
                }));

        // Report overloaded nodes
        powerNodes.entrySet().stream().filter(e -> e.getKey().blockType().maxNodes() < e.getValue().size())
                .map(Map.Entry::getKey)
                .forEachOrdered(b -> builder.error(b.sourcePosition(), "Block '%s' at %s has more than %d connection(s).",
                        b.name(), b.position().toStringAbsolute(), b.blockType().maxNodes()));

        blocks.replaceAll(block -> replaceLinks(block, powerNodes.get(block)));
    }

    private Block replaceLinks(Block block, Set<Block> links) {
        return links == null ? block
                : block.withConnections(new PositionArray(links.stream().map(Block::position).toList()));
    }

    private Set<Block> collectLinks(Block powerNodeBlock) {
        Set<Block> linkedBlocks = new HashSet<>();
        for (Position pos : powerNodeBlock.configuration().as(PositionArray.class).positions()) {
            Block linkedBlock = positionMap.at(pos);

            if (linkedBlock == null || linkedBlock.blockType().isAir()) {
                builder.warn(powerNodeBlock.sourcePosition(), "Block '%s' at %s has a connection to a nonexistent block at %s.",
                        powerNodeBlock.name(), powerNodeBlock.position().toStringAbsolute(), pos.toStringAbsolute());
            } else if (linkedBlock == powerNodeBlock) {
                builder.error(powerNodeBlock.sourcePosition(), "Block '%s' at %s has a connection to self.",
                        powerNodeBlock.name(), powerNodeBlock.position().toStringAbsolute());
            } else if (!linkedBlock.blockType().hasPower()) {
                builder.error(powerNodeBlock.sourcePosition(), "Block '%s' at %s has an invalid connection to a non-powered block '%s' at %s.",
                        powerNodeBlock.name(), powerNodeBlock.position().toStringAbsolute(),
                        linkedBlock.name(), pos.toStringAbsolute());
            } else if (outOfRange(powerNodeBlock, linkedBlock) && outOfRange(linkedBlock, powerNodeBlock)) {
                builder.error(powerNodeBlock.sourcePosition(), "Block '%s' at %s has an out-of-range connection to block '%s' at %s.",
                        powerNodeBlock.name(), powerNodeBlock.position().toStringAbsolute(),
                        linkedBlock.name(), pos.toStringAbsolute());
            } else {
                if (!linkedBlocks.add(linkedBlock)) {
                    builder.warn(powerNodeBlock.sourcePosition(), "Block '%s' at %s has multiple connections to block '%s' at %s.",
                            powerNodeBlock.name(), powerNodeBlock.position().toStringAbsolute(),
                            linkedBlock.name(), pos.toStringAbsolute());
                }
            }
        }

        return linkedBlocks;
    }

    private boolean isPowerNode(Block block) {
        return switch (block.implementation()) {
            case POWERNODE, POWERSOURCE, LONGPOWERNODE -> true;
            default -> false;
        };
    }

    private boolean outOfRange(Block from, Block to) {
        if (!isPowerNode(from)) {
            return true;
        } else {
            double x = from.position().x() + from.size() / 2d;
            double y = from.position().y() + from.size() / 2d;
            double distX = (x < to.x() ? to.x() : to.x() + to.size()) - x;
            double distY = (y < to.y() ? to.y() : to.y() + to.size()) - y;

            return distX * distX + distY * distY >= from.blockType().range() * from.blockType().range();
        }
    }
}
