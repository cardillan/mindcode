package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.mindustry.LiquidConfiguration;

public record AstLiquidReference(SourcePosition sourcePosition, String liquid) implements AstContentsReference {

    @Override
    public String getConfigurationText() {
        return liquid;
    }

    @Override
    public Configuration getConfiguration() {
        return LiquidConfiguration.forName(liquid);
    }
}
