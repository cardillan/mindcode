package info.teksol.mindcode.mimex;

public record Item(
        String baseName,
        String name,
        int id
) implements NumberedConstant {

    public static Item forId(int id) {
        return NumberedConstants.ITEM_ID_MAP.get(id);
    }

    public static Item forName(String name) {
        return NumberedConstants.ITEM_MAP.get(name);
    }
}
