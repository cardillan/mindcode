package info.teksol.mc.emulator.blocks;

import info.teksol.mc.emulator.AbstractMindustryObject;
import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class MindustryBuilding extends AbstractMindustryObject {

    public MindustryBuilding(String name, MindustryContent type) {
        super(name, -1, Objects.requireNonNull(type));
    }

    @Override
    public MindustryContent type() {
        return super.type();
    }
}
