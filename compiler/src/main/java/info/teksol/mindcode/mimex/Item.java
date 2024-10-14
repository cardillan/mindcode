package info.teksol.mindcode.mimex;

public record Item(
        String name,
        String varName,
        int id
) implements MindustryContent {

    public static int count() {
        return MindustryContents.ITEM_MAP.size();
    }

    public static Item forId(int id) {
        return MindustryContents.ITEM_ID_MAP.get(id);
    }

    public static Item forName(String name) {
        return MindustryContents.ITEM_MAP.get(name);
    }
}
