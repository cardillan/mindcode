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

    static UnitCommand create(String contentName, String name, int id, int logicId) {
        return new UnitCommand(contentName, name, id);
    }
}
