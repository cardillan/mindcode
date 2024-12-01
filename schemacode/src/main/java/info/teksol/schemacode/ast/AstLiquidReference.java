package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.mindustry.LiquidConfiguration;

public record AstLiquidReference(InputPosition inputPosition, String liquid) implements AstContentsReference {

    @Override
    public String getConfigurationText() {
        return liquid;
    }

    @Override
    public Configuration getConfiguration() {
        return LiquidConfiguration.forName(liquid);
    }
}
