package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.mindustry.UnitConfiguration;

public record AstUnitReference(InputPosition inputPosition, String unit) implements AstContentsReference {

    @Override
    public String getConfigurationText() {
        return unit;
    }

    @Override
    public Configuration getConfiguration() {
        return UnitConfiguration.forName(unit);
    }
}
