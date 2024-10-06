package info.teksol.emulator.blocks;

import info.teksol.emulator.AbstractMindustryObject;
import info.teksol.mindcode.mimex.MindustryContent;

import java.util.Objects;

public class MindustryBlock extends AbstractMindustryObject {

    public MindustryBlock(String name, MindustryContent type) {
        super(name, -1, Objects.requireNonNull(type));
    }
}
