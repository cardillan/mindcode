package info.teksol.schemacode.mindustry;

import info.teksol.mc.mindcode.logic.mimex.UnitCommand;
import info.teksol.schemacode.SchematicsMetadata;
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
        return unitCommand.name();
    }

    private static UnitCommandConfiguration forUnitCommand(UnitCommand unitCommand) {
        return unitCommand == null ? null : new UnitCommandConfiguration(unitCommand);
    }

    public static UnitCommandConfiguration forId(int id) {
        return forUnitCommand(SchematicsMetadata.metadata.getUnitCommandById(id));
    }

    public static UnitCommandConfiguration forName(String name) {
        return forUnitCommand(SchematicsMetadata.metadata.getUnitCommandByName(name));
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
