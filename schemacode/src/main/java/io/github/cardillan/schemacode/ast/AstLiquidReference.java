package io.github.cardillan.schemacode.ast;

import io.github.cardillan.mc.common.SourcePosition;
import io.github.cardillan.schemacode.config.Configuration;
import io.github.cardillan.schemacode.mindustry.LiquidConfiguration;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstLiquidReference(SourcePosition sourcePosition, String liquid) implements AstContentsReference {

    public AstLiquidReference(String liquid) {
        this(SourcePosition.EMPTY, liquid);
    }

    @Override
    public String getConfigurationText() {
        return liquid;
    }

    @Override
    public Configuration getConfiguration() {
        return LiquidConfiguration.forName(liquid);
    }

    @Override
    public AstLiquidReference withEmptyPosition() {
        return new AstLiquidReference(SourcePosition.EMPTY, liquid);
    }
}
