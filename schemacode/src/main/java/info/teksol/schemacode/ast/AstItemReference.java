package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.mindustry.ItemConfiguration;

public record AstItemReference(SourcePosition sourcePosition, String item) implements AstContentsReference {

    @Override
    public String getConfigurationText() {
        return item;
    }

    @Override
    public Configuration getConfiguration() {
        return ItemConfiguration.forName(item);
    }
}
