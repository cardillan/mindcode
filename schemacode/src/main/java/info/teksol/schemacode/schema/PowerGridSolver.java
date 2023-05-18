package info.teksol.schemacode.schema;

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
    private final Schematic input;
    private final BlockPositionMap<Block> positionMap;

    private PowerGridSolver(SchematicsBuilder builder, Schematic schematic) {
        this.builder = builder;
        this.input = schematic;
        this.positionMap = builder.getPositionMap();
    }

    static Schematic solve(SchematicsBuilder builder, Schematic schematic) {
        return new PowerGridSolver(builder, schematic).solve();
    }

    Schematic solve() {
        Map<Block, Set<Block>> powerNodes = input.blocks().stream().filter(this::isPowerNode)
                .collect(Collectors.toMap(b -> b, this::collectLinks));

        powerNodes.forEach((block, links) ->
                links.forEach(linkedBlock -> {
                    if (isPowerNode(linkedBlock)) {
                        Set<Block> linkedBlockLinks = powerNodes.get(linkedBlock);
                        if (linkedBlockLinks == null) {
                            throw new SchematicsInternalError("Linked power node not contained in powerNodes map.");
                        } else {
                            // Ensure reciprocal link is there
                            linkedBlockLinks.add(block);
                        }
                    }
                }));

        // Report overloaded nodes
        powerNodes.entrySet().stream().filter(e -> e.getKey().blockType().maxNodes() < e.getValue().size())
                .map(Map.Entry::getKey)
                .forEachOrdered(b -> builder.error("Block '%s' at %s has more than %d connection(s).",
                        b.name(), b.position().toStringAbsolute(), b.blockType().maxNodes()));

        // Rebuild block list
        List<Block> blocks = input.blocks().stream().map(block -> replaceLinks(block, powerNodes.get(block))).toList();

        return new Schematic(input.name(), input.description(), input.labels(), input.width(), input.height(), blocks);
    }

    private Block replaceLinks(Block block, Set<Block> links) {
        return links == null ? block
                : block.withConnections(new PositionArray(links.stream().map(Block::position).toList()));
    }

    private Set<Block> collectLinks(Block powerNodeBlock) {
        Set<Block> linkedBlocks = new HashSet<>();
        for (Position pos : powerNodeBlock.configuration().as(PositionArray.class).positions()) {
            Block linkedBlock = positionMap.at(pos);

            if (linkedBlock == null) {
                builder.warn("Block '%s' at %s has a connection to a nonexistent block at %s.",
                        powerNodeBlock.name(), powerNodeBlock.position().toStringAbsolute(), pos.toStringAbsolute());
            } else if (linkedBlock == powerNodeBlock) {
                builder.error("Block '%s' at %s has a connection to self.",
                        powerNodeBlock.name(), powerNodeBlock.position().toStringAbsolute());
            } else if (!linkedBlock.blockType().hasPower()) {
                builder.error("Block '%s' at %s has an invalid connection to a non-powered block '%s' at %s.",
                        powerNodeBlock.name(), powerNodeBlock.position().toStringAbsolute(),
                        linkedBlock.name(), pos.toStringAbsolute());
            } else if (!inRange(powerNodeBlock, linkedBlock) && !inRange(linkedBlock, powerNodeBlock)) {
                builder.error("Block '%s' at %s has an out-of-range connection to block '%s' at %s.",
                        powerNodeBlock.name(), powerNodeBlock.position().toStringAbsolute(),
                        linkedBlock.name(), pos.toStringAbsolute());
            } else {
                if (!linkedBlocks.add(linkedBlock)) {
                    builder.warn("Block '%s' at %s has multiple connections to block '%s' at %s.",
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

    private boolean inRange(Block from, Block to) {
        if (!isPowerNode(from)) {
            return false;
        } else {
            double x = from.position().x() + from.size() / 2d;
            double y = from.position().y() + from.size() / 2d;
            double distX = (x < to.x() ? to.x() : to.x() + to.size()) - x;
            double distY = (y < to.y() ? to.y() : to.y() + to.size()) - y;

            return distX * distX + distY * distY < from.blockType().range() * from.blockType().range();
        }
    }
}
