package info.teksol.schemacode.mindustry;

import info.teksol.mc.mindcode.logic.mimex.Liquid;
import info.teksol.schemacode.SchematicsMetadata;
import info.teksol.schemacode.config.Configuration;

import java.util.Objects;

public final class LiquidConfiguration implements Configuration {
    private final Liquid liquid;

    private LiquidConfiguration(Liquid liquid) {
        this.liquid = Objects.requireNonNull(liquid);
    }

    public int getId() {
        return liquid.id();
    }

    public String getName() {
        return liquid.name();
    }

    private static LiquidConfiguration forLiquid(Liquid liquid) {
        return liquid == null ? null : new LiquidConfiguration(liquid);
    }

    public static LiquidConfiguration forId(int id) {
        return forLiquid(SchematicsMetadata.metadata.getLiquidById(id));
    }

    public static LiquidConfiguration forName(String name) {
        return forLiquid(SchematicsMetadata.metadata.getLiquidByName(name));
    }

    @Override
    public String toString(){
        return "Liquid(name=" + getName() + ")";
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof LiquidConfiguration c && liquid.equals(c.liquid);
    }

    @Override
    public int hashCode() {
        return liquid.hashCode();
    }
}
