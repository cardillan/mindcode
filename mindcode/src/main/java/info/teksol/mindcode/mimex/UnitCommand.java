package info.teksol.mindcode.mimex;

public record UnitCommand(
        String name,
        String varName,
        int id
) implements MindustryContent {

    public static UnitCommand forId(int id) {
        return MindustryContents.UNITCOMMAND_ID_MAP.get(id);
    }

    public static UnitCommand forName(String name) {
        return MindustryContents.UNITCOMMAND_MAP.get(name);
    }
}
