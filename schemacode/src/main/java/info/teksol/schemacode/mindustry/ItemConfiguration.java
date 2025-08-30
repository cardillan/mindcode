package info.teksol.schemacode.mindustry;

import info.teksol.mc.mindcode.logic.mimex.ContentType;
import info.teksol.mc.mindcode.logic.mimex.Item;
import info.teksol.schemacode.SchematicsMetadata;

import java.util.Objects;

public final class ItemConfiguration implements ContentConfiguration {
    private final Item item;

    private ItemConfiguration(Item item) {
        this.item = Objects.requireNonNull(item);
    }

    @Override
    public ContentType getContentType() {
        return ContentType.ITEM;
    }

    @Override
    public int getId() {
        return item.id();
    }

    @Override
    public String getContentName() {
        return item.contentName();
    }

    public String getName() {
        return item.name();
    }

    public static ItemConfiguration forItem(Item item) {
        return item == null ? null : new ItemConfiguration(item);
    }

    public static ItemConfiguration forId(int id) {
        return forItem(SchematicsMetadata.metadata.getItemById(id));
    }

    public static ItemConfiguration forName(String name) {
        return forItem(SchematicsMetadata.metadata.getItemByName(name));
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
