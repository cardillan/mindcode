package info.teksol.mc.mindcode.logic.mimex;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record UnitCommand(
        String contentName,
        String name,
        int id
) implements MindustryContent {

    @Override
    public ContentType contentType() {
        return ContentType.UNIT_COMMAND;
    }

    public static UnitCommand forId(int id) {
        return MindustryContents.UNITCOMMAND_ID_MAP.get(id);
    }

    public static UnitCommand forName(String name) {
        return MindustryContents.UNITCOMMAND_MAP.get(name);
    }
}
