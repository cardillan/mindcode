package info.teksol.schemacode.mindustry;

import info.teksol.mc.mindcode.logic.mimex.ContentType;
import info.teksol.mc.mindcode.logic.mimex.Unit;
import info.teksol.schemacode.SchematicsMetadata;

import java.util.Objects;

public final class UnitConfiguration implements UnitOrBlockConfiguration {
    private final Unit unit;

    private UnitConfiguration(Unit unit) {
        this.unit = Objects.requireNonNull(unit);
    }

    @Override
    public ContentType getContentType() {
        return ContentType.UNIT;
    }

    @Override
    public int getId() {
        return unit.id();
    }

    @Override
    public String getContentName() {
        return unit.contentName();
    }

    public String getName() {
        return unit.name();
    }

    public static UnitConfiguration forUnit(Unit unit) {
        return unit == null ? null : new UnitConfiguration(unit);
    }

    public static UnitConfiguration forId(int id) {
        return forUnit(SchematicsMetadata.metadata.getUnitById(id));
    }

    public static UnitConfiguration forName(String name) {
        return forUnit(SchematicsMetadata.metadata.getUnitByName(name));
    }

    @Override
    public String toString(){
        return "Unit(name=" + getName() + ")";
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof UnitConfiguration c && unit.equals(c.unit);
    }

    @Override
    public int hashCode() {
        return unit.hashCode();
    }
}
