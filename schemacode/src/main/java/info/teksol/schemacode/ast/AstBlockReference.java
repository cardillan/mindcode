package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.mindustry.BlockConfiguration;

public record AstBlockReference(InputPosition inputPosition, String block) implements AstContentsReference {

    @Override
    public String getConfigurationText() {
        return block;
    }

    @Override
    public Configuration getConfiguration() {
        return BlockConfiguration.forName(block);
    }
}
