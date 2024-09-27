package info.teksol.schemacode.mindustry;

import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.config.EmptyConfiguration;
import info.teksol.schemacode.config.IntConfiguration;
import info.teksol.schemacode.schematics.Block;

public record UnitPlan(String unitName) implements Configuration {

    public static final UnitPlan EMPTY = new UnitPlan("");

    @Override
    public Configuration encode(Block block) {
        int index = block.blockType().unitPlans().indexOf(unitName);
        return index < 0 ? EmptyConfiguration.EMPTY : new IntConfiguration(index);
    }
}
