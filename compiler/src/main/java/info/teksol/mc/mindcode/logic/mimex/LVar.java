package info.teksol.mc.mindcode.logic.mimex;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;

@NullMarked
public record LVar(
        String contentName,
        String name,
        boolean global,
        boolean object,
        boolean constant,
        Double numericValue
) implements MindustryContent {

    @Override
    public ContentType contentType() {
        return ContentType.LVAR;
    }

    @Override
    public int id() {
        return -1;
    }

    public static @Nullable LVar forName(String name) {
        return MindustryContents.LVAR_MAP.get(name);
    }

    public static Collection<LVar> allVars() {
        return MindustryContents.LVAR_MAP.values();
    }
}
