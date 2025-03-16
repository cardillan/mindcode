package info.teksol.mc.mindcode.logic.mimex;

import info.teksol.mc.emulator.MindustryObject;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/// Represents a Mindustry content. There are various kind of contents - building types, unit types,
/// items, liquids.
@NullMarked
public interface MindustryContent extends MindustryObject {

    /// @return the type of this content
    ContentType contentType();

    /// @return content name of the content (not including the `@` prefix)
    String contentName();

    /// @return name of the content (includes the "@" prefix)
    String name();

    /// Provides the ID of the object. Used by the sensor @id instruction. -1 means no ID and gets translated to null.
    ///
    /// @return numeric id of the object
    int id();

    /// Formats the object for output. Used when printing the object to the text buffer.
    ///
    /// @return text representation of the object
    default String format() {
        return contentName();
    }

    /// @return icon string literal for this object
    default @Nullable String iconString() {
        return Icons.getContentIcon(contentType(), contentName());
    }
}
