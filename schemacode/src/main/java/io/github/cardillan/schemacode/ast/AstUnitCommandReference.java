package io.github.cardillan.schemacode.ast;

import io.github.cardillan.mindcode.common.SourcePosition;
import io.github.cardillan.schemacode.config.Configuration;
import io.github.cardillan.schemacode.mindustry.UnitCommandConfiguration;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstUnitCommandReference(SourcePosition sourcePosition, String command) implements AstContentsReference {

    public AstUnitCommandReference(String command) {
        this(SourcePosition.EMPTY, command);
    }

    @Override
    public String getConfigurationText() {
        return command;
    }

    @Override
    public Configuration getConfiguration() {
        return UnitCommandConfiguration.forName(command);
    }

    @Override
    public AstUnitCommandReference withEmptyPosition() {
        return new AstUnitCommandReference(SourcePosition.EMPTY, command);
    }
}
