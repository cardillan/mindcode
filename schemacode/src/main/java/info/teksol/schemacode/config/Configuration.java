package info.teksol.schemacode.config;

import info.teksol.schemacode.SchematicsInternalError;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.schema.Block;

import java.util.function.UnaryOperator;

public interface Configuration {

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
}
