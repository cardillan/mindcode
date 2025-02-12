package info.teksol.mc.emulator.blocks;

import info.teksol.mc.emulator.AbstractMindustryObject;
import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class MindustryBlock extends AbstractMindustryObject {

    public MindustryBlock(String name, MindustryContent type) {
        super(name, -1, Objects.requireNonNull(type));
    }
}
