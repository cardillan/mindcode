package info.teksol.mc.mindcode.logic.mimex;

public record Unit(
        String contentName,
        String name,
        int id
) implements MindustryContent {

    public static int count() {
        return MindustryContents.UNIT_MAP.size();
    }

    public static Unit forId(int id) {
        return MindustryContents.UNIT_ID_MAP.get(id);
    }

    public static Unit forName(String name) {
        return MindustryContents.UNIT_MAP.get(name);
    }
}
