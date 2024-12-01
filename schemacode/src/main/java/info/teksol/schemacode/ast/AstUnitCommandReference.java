package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.mindustry.UnitCommandConfiguration;

public record AstUnitCommandReference(InputPosition inputPosition, String command) implements AstContentsReference {

    @Override
    public String getConfigurationText() {
        return command;
    }

    @Override
    public Configuration getConfiguration() {
        return UnitCommandConfiguration.forName(command);
    }
}
