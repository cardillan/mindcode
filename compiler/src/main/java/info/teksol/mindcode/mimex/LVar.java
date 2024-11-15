package info.teksol.mindcode.mimex;

import java.util.Collection;

public record LVar(
        String contentName,
        String name,
        boolean global,
        boolean object,
        boolean constant,
        Double numericValue
) implements MindustryContent {

    @Override
    public int id() {
        return -1;
    }

    public static LVar forName(String name) {
        return MindustryContents.LVAR_MAP.get(name);
    }

    public static Collection<LVar> allVars() {
        return MindustryContents.LVAR_MAP.values();
    }
}
