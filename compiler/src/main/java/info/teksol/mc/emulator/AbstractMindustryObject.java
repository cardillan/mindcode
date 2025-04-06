package info.teksol.mc.emulator;

import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public class AbstractMindustryObject implements MindustryObject {
    private final String name;
    private final int id;
    private @Nullable final MindustryContent type;

    public AbstractMindustryObject(String name, int id, @Nullable MindustryContent type) {
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
    public @Nullable MindustryContent type() {
        return type;
    }
}
