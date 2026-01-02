package info.teksol.mc.emulator;

import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class AbstractMindustryObject implements MindustryObject {
    protected final String name;
    protected final int id;
    protected final MindustryContent type;

    public AbstractMindustryObject(String name, int id, MindustryContent type) {
        this.name = Objects.requireNonNull(name);
        this.id = id;
        this.type = type;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String format() {
        return name();
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public MindustryContent type() {
        return type;
    }
}
