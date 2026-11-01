package io.github.cardillan.schemacode.ast;

import io.github.cardillan.mc.common.SourcePosition;
import io.github.cardillan.schemacode.config.Configuration;
import io.github.cardillan.schemacode.mindustry.UnitConfiguration;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstUnitReference(SourcePosition sourcePosition, String unit) implements AstContentsReference {

    public AstUnitReference(String unit) {
        this(SourcePosition.EMPTY, unit);
    }

    @Override
    public String getConfigurationText() {
        return unit;
    }

    @Override
    public Configuration getConfiguration() {
        return UnitConfiguration.forName(unit);
    }

    @Override
    public AstUnitReference withEmptyPosition() {
        return new AstUnitReference(SourcePosition.EMPTY, unit);
    }
}
