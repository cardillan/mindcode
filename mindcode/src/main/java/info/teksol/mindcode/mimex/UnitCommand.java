package info.teksol.mindcode.mimex;

public record UnitCommand(
        String baseName,
        String name,
        int id
) implements NumberedConstant {

    public static UnitCommand forId(int id) {
        return NumberedConstants.UNITCOMMAND_ID_MAP.get(id);
    }

    public static UnitCommand forName(String name) {
        return NumberedConstants.UNITCOMMAND_MAP.get(name);
    }
}
