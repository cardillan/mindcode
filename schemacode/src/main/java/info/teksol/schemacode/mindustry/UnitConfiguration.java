package info.teksol.schemacode.mindustry;

import info.teksol.mindcode.mimex.Unit;
import info.teksol.schemacode.config.UnitOrBlockConfiguration;

import java.util.Objects;

public final class UnitConfiguration implements UnitOrBlockConfiguration {
    private final Unit unit;

    private UnitConfiguration(Unit unit) {
        this.unit = Objects.requireNonNull(unit);
    }

    public int getId() {
        return unit.id();
    }

    public String getName() {
        return unit.name();
    }

    private static UnitConfiguration forUnit(Unit unit) {
        return unit == null ? null : new UnitConfiguration(unit);
    }

    public static UnitConfiguration forId(int id) {
        return forUnit(Unit.forId(id));
    }

    public static UnitConfiguration forName(String name) {
        return forUnit(Unit.forName(name));
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
