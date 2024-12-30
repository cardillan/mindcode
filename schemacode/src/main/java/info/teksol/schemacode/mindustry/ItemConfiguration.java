package info.teksol.schemacode.mindustry;

import info.teksol.mc.mindcode.logic.mimex.Item;
import info.teksol.schemacode.config.Configuration;

import java.util.Objects;

public final class ItemConfiguration implements Configuration {
    private final Item item;

    private ItemConfiguration(Item item) {
        this.item = Objects.requireNonNull(item);
    }

    public int getId() {
        return item.id();
    }

    public String getName() {
        return item.name();
    }

    private static ItemConfiguration forItem(Item item) {
        return item == null ? null : new ItemConfiguration(item);
    }

    public static ItemConfiguration forId(int id) {
        return forItem(Item.forId(id));
    }

    public static ItemConfiguration forName(String name) {
        return forItem(Item.forName(name));
    }

    @Override
    public String toString(){
        return "Item(name=" + getName() + ")";
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof ItemConfiguration c && item.equals(c.item);
    }

    @Override
    public int hashCode() {
        return item.hashCode();
    }
}
