package info.teksol.mindcode.mimex;

import info.teksol.emulator.MindustryObject;

/**
 * Represents a Mindustry content. There are various kind of contents - building types, unit types,
 * items, liquids.
 */
public interface MindustryContent extends MindustryObject {
    /** @return content name of the content (not including the `@` prefix) */
    String contentName();

    /** @return name of the content (includes the "@" prefix) */
    String name();

    default String format() {
        return contentName();
    }
}
