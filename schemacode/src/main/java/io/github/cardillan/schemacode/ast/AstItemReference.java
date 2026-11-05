package io.github.cardillan.schemacode.ast;

import io.github.cardillan.mindcode.common.SourcePosition;
import io.github.cardillan.schemacode.config.Configuration;
import io.github.cardillan.schemacode.mindustry.ItemConfiguration;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstItemReference(SourcePosition sourcePosition, String item) implements AstContentsReference {

    public AstItemReference(String item) {
        this(SourcePosition.EMPTY, item);
    }

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
