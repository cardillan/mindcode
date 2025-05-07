package info.teksol.mc.mindcode.logic.mimex;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface NamedContent {

    /// @return content name of the content (not including any prefix - e.g. `@` for built-ins)
    String contentName();

    /// @return name of the content (includes the prefix)
    String name();

}
