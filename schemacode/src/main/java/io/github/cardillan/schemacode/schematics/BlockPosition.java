package io.github.cardillan.schemacode.schematics;

import io.github.cardillan.mindcode.logic.mimex.BlockType;
import io.github.cardillan.schemacode.ast.PlacementMode;
import io.github.cardillan.schemacode.config.Configuration;
import io.github.cardillan.schemacode.mindustry.ConfigurationType;
import io.github.cardillan.schemacode.mindustry.Implementation;
import io.github.cardillan.schemacode.mindustry.Position;

public interface BlockPosition {
    int index();
    BlockType blockType();
    Position position();

    default String name() {
        return blockType().name();
    }

    default int size() {
        return blockType().size();
    }

    default Implementation implementation() {
        return Implementation.fromBlockType(blockType());
    }

    default Class<? extends Configuration> configurationClass() {
        return implementation().configurationClass();
    }

    default ConfigurationType configurationType() {
        return ConfigurationType.fromBlockType(blockType());
    }

    default int x() {
        return position().x();
    }

    default int y() {
        return position().y();
    }

    default int xMax() {
        return position().x() + size()  - 1;
    }

    default int yMax() {
        return position().y() + size() - 1;
    }

    default String area() {
        return size() == 1 ? "(%d, %d)".formatted(x(), y()) : "(%d, %d) - (%d, %d)".formatted(x(), y(), xMax(), yMax());
    }

    default PlacementMode placementMode() {
        return PlacementMode.DEFAULT;
    }

    default boolean valid() {
        return true;
    }

    /// Called when this block is invalidated due to its placement mode
    default void invalidate() {
        throw new UnsupportedOperationException();
    }
}
