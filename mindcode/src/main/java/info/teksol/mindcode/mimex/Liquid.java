package info.teksol.mindcode.mimex;

public record Liquid(
        String name,
        String varName,
        int id
) implements MindustryContent {

    public static int count() {
        return MindustryContents.LIQUID_MAP.size();
    }

    public static Liquid forId(int id) {
        return MindustryContents.LIQUID_ID_MAP.get(id);
    }

    public static Liquid forName(String name) {
        return MindustryContents.LIQUID_MAP.get(name);
    }
}
