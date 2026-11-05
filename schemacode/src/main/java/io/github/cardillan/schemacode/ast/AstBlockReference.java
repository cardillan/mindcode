package io.github.cardillan.schemacode.ast;

import io.github.cardillan.mindcode.common.SourcePosition;
import io.github.cardillan.schemacode.config.Configuration;
import io.github.cardillan.schemacode.mindustry.BlockConfiguration;
import org.jspecify.annotations.NullMarked;

// Reference to a block type (e.g., in a factory configuration)
@NullMarked
public record AstBlockReference(SourcePosition sourcePosition, String block) implements AstContentsReference {

    public AstBlockReference(String block) {
        this(SourcePosition.EMPTY, block);
    }

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
