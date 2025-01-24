package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.mindustry.UnitConfiguration;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstUnitReference(SourcePosition sourcePosition, String unit) implements AstContentsReference {

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
