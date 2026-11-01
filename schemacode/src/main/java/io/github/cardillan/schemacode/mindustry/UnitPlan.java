package io.github.cardillan.schemacode.mindustry;

import io.github.cardillan.schemacode.config.Configuration;
import io.github.cardillan.schemacode.config.EmptyConfiguration;
import io.github.cardillan.schemacode.config.IntConfiguration;
import io.github.cardillan.schemacode.schematics.Block;

public record UnitPlan(String unitName) implements Configuration {
    @Override
    public Configuration encode(Block block) {
        int index = block.blockType().unitPlans().indexOf(unitName);
        return index < 0 ? EmptyConfiguration.EMPTY : new IntConfiguration(index);
    }
}
