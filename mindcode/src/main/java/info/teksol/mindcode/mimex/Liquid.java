package info.teksol.mindcode.mimex;

public record Liquid(
        String baseName,
        String name,
        int id
) implements NumberedConstant {

    public static Liquid forId(int id) {
        return NumberedConstants.LIQUID_ID_MAP.get(id);
    }

    public static Liquid forName(String name) {
        return NumberedConstants.LIQUID_MAP.get(name);
    }
}
