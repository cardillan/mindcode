package info.teksol.schemacode.mindustry;

import info.teksol.mindcode.mimex.UnitCommand;
import info.teksol.schemacode.config.Configuration;

import java.util.Objects;

public final class UnitCommandConfiguration implements Configuration {
    private final UnitCommand unitCommand;

    private UnitCommandConfiguration(UnitCommand unitCommand) {
        this.unitCommand = Objects.requireNonNull(unitCommand);
    }

    public int getId() {
        return unitCommand.id();
    }

    public String getName() {
        return unitCommand.varName();
    }

    private static UnitCommandConfiguration forUnitCommand(UnitCommand unitCommand) {
        return unitCommand == null ? null : new UnitCommandConfiguration(unitCommand);
    }

    public static UnitCommandConfiguration forId(int id) {
        return forUnitCommand(UnitCommand.forId(id));
    }

    public static UnitCommandConfiguration forName(String name) {
        return forUnitCommand(UnitCommand.forName(name));
    }

    @Override
    public String toString(){
        return "UnitCommand(name=" + getName() + ")";
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof UnitCommandConfiguration c && unitCommand.equals(c.unitCommand);
    }

    @Override
    public int hashCode() {
        return unitCommand.hashCode();
    }
}
