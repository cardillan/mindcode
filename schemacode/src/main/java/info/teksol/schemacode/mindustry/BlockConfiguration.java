package info.teksol.schemacode.mindustry;

import info.teksol.mindcode.mimex.BlockType;
import info.teksol.schemacode.config.UnitOrBlockConfiguration;

import java.util.Objects;

public final class BlockConfiguration implements UnitOrBlockConfiguration {
    private final BlockType blockType;

    private BlockConfiguration(BlockType blockType) {
        this.blockType = Objects.requireNonNull(blockType);
    }

    public int getId() {
        return blockType.id();
    }

    public String getName() {
        return blockType.varName();
    }

    private static BlockConfiguration forBlockType(BlockType blockType) {
        return blockType == null ? null : new BlockConfiguration(blockType);
    }

    public static BlockConfiguration forId(int id) {
        return forBlockType(BlockType.forId(id));
    }

    public static BlockConfiguration forName(String name) {
        return forBlockType(BlockType.forName(name));
    }

    @Override
    public String toString(){
        return "Block(name=" + getName() + ")";
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof BlockConfiguration c && blockType.equals(c.blockType);
    }

    @Override
    public int hashCode() {
        return blockType.hashCode();
    }
}
