package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.mindustry.BlockConfiguration;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstBlockReference(SourcePosition sourcePosition, String block) implements AstContentsReference {

    @Override
    public String getConfigurationText() {
        return block;
    }

    @Override
    public Configuration getConfiguration() {
        return BlockConfiguration.forName(block);
    }

    @Override
    public AstBlockReference withEmptyPosition() {
        return new AstBlockReference(SourcePosition.EMPTY, block);
    }
}
