package io.github.cardillan.mc.emulator.blocks;

import io.github.cardillan.mc.emulator.AbstractMindustryObject;
import io.github.cardillan.mc.mindcode.logic.mimex.BlockType;
import io.github.cardillan.mc.mindcode.logic.mimex.MindustryContent;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class MindustryBuilding extends AbstractMindustryObject {
    private final BlockPosition position;
    private final int size;

    public MindustryBuilding(String name, BlockType type, BlockPosition position) {
        super(name, -1, type);
        this.position = position;
        this.size = type.size();
    }

    public double x() {
        return position.x() + (double) size / 2;
    }

    public double y() {
        return position.y() + (double) size / 2;
    }

    @Override
    public String format() {
        return type().format();
    }

    @Override
    public MindustryContent type() {
        return Objects.requireNonNull(super.type());
    }
}
