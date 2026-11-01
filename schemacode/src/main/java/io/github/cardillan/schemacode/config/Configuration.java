package io.github.cardillan.schemacode.config;

import io.github.cardillan.mc.common.SourceElement;
import io.github.cardillan.schemacode.SchematicsInternalError;
import io.github.cardillan.schemacode.mindustry.Position;
import io.github.cardillan.schemacode.schematics.Block;
import io.github.cardillan.schemacode.schematics.SchematicsBuilder;

import java.util.function.UnaryOperator;

public interface Configuration extends UnresolvedConfiguration {

    @Override
    default Configuration resolve() {
        return this;
    }

    default <T extends Configuration> T as (Class<T> type) {
        if (type.isInstance(this)) {
            return type.cast(this);
        }

        // Internal error; wrong configuration in schematics should be caught prior to this
        throw new SchematicsInternalError("Unexpected configuration type, expected %s, got %s.",
                type.getSimpleName(), getClass().getSimpleName());
    }

    default Configuration encode(Block block) {
        return this;
    }

    default Configuration remap(UnaryOperator<Position> mapping) {
        return this;
    }

    default void validate(SchematicsBuilder builder, SourceElement astBlock, Block block) {
    }
}
