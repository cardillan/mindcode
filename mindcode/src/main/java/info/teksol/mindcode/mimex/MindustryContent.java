package info.teksol.mindcode.mimex;

import info.teksol.emulator.MindustryObject;

/**
 * Represents a Mindustry content. There are various kind of contents - building types, unit types,
 * items, liquids.
 */
public interface MindustryContent extends MindustryObject {
    /**
     * @return name of the variable representing the type ("@" + name)
     */
    String varName();
}
