package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.mindustry.ItemConfiguration;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstItemReference(SourcePosition sourcePosition, String item) implements AstContentsReference {

    @Override
    public String getConfigurationText() {
        return item;
    }

    @Override
    public Configuration getConfiguration() {
        return ItemConfiguration.forName(item);
    }

    @Override
    public AstItemReference withEmptyPosition() {
        return new AstItemReference(SourcePosition.EMPTY, item);
    }
}
