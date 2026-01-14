package info.teksol.mc.emulator;

import info.teksol.mc.emulator.blocks.MindustryBuilding;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class EmulatorSchematic {
    private final List<MindustryBuilding> blocks;

    public EmulatorSchematic() {
        this.blocks = List.of();
    }

    public EmulatorSchematic(List<MindustryBuilding> blocks) {
        this.blocks = blocks;
    }

    public List<MindustryBuilding> getBlocks() {
        return blocks;
    }
}
