package info.teksol.mc.mindcode.logic.mimex;

public record UnitCommand(
        String contentName,
        String name,
        int id
) implements MindustryContent {

    public static UnitCommand forId(int id) {
        return MindustryContents.UNITCOMMAND_ID_MAP.get(id);
    }

    public static UnitCommand forName(String name) {
        return MindustryContents.UNITCOMMAND_MAP.get(name);
    }
}
