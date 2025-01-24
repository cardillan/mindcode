package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.mindustry.UnitCommandConfiguration;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstUnitCommandReference(SourcePosition sourcePosition, String command) implements AstContentsReference {

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
