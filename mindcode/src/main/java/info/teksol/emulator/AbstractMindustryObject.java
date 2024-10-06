package info.teksol.emulator;

import info.teksol.mindcode.mimex.MindustryContent;

public class AbstractMindustryObject implements MindustryObject {
    private final String name;
    private final int id;
    private final MindustryContent type;

    public AbstractMindustryObject(String name, int id, MindustryContent type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

    @Override
    public String name() {
        return name;
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
