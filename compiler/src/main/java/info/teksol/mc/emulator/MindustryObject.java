package info.teksol.mc.emulator;

import info.teksol.mc.mindcode.logic.mimex.MindustryContent;

/**
 * Represents objects that can be handled by Mindustry Logic. Everything that is not a numeric value
 * in the processor emulator is an object implementing this interface.
 */
public interface MindustryObject {
    /**
     * Name of the object. Does not include the `@` prefix.
     *
     * @return name of the object
     */
    String name();

    /**
     * Formats the object for output. Used when printing the object to the text buffer.
     *
     * @return text representation of the object
     */
    String format();

    /**
     * Provides the ID of the object. Used by the sensor @id instruction. -1 means no ID and gets translated to null.
     *
     * @return numeric id of the object
     */
    int id();

    /**
     * Provides the type of the object. Used by the sensor @type instruction.
     *
     * @return type of the object.
     */
    default MindustryContent type() {
        return null;
    }
}
