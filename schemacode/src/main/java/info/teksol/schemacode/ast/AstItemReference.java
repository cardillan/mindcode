package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.mindustry.ItemConfiguration;

public record AstItemReference(InputPosition inputPosition, String item) implements AstContentsReference {

    @Override
    public String getConfigurationText() {
        return item;
    }

    @Override
    public Configuration getConfiguration() {
        return ItemConfiguration.forName(item);
    }
}
