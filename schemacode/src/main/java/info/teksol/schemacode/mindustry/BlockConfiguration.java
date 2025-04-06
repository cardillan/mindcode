package info.teksol.schemacode.mindustry;

import info.teksol.mc.mindcode.logic.mimex.BlockType;
import info.teksol.schemacode.SchematicsMetadata;
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
        return blockType.name();
    }

    private static BlockConfiguration forBlockType(BlockType blockType) {
        return blockType == null ? null : new BlockConfiguration(blockType);
    }

    public static BlockConfiguration forId(int id) {
        return forBlockType(SchematicsMetadata.metadata.getBlockById(id));
    }

    public static BlockConfiguration forName(String name) {
        return forBlockType(SchematicsMetadata.metadata.getBlockByName(name));
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
