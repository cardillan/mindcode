package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.mindustry.UnitCommandConfiguration;

public record AstUnitCommandReference(SourcePosition sourcePosition, String command) implements AstContentsReference {

    @Override
    public String getConfigurationText() {
        return command;
    }

    @Override
    public Configuration getConfiguration() {
        return UnitCommandConfiguration.forName(command);
    }
}
