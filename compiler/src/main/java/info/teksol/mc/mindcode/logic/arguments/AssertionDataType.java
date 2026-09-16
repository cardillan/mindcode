package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.emulator.LVar;
import info.teksol.mc.emulator.blocks.LogicBlock;
import info.teksol.mc.emulator.blocks.MemoryBlock;
import info.teksol.mc.emulator.blocks.MessageBlock;
import info.teksol.mc.emulator.blocks.MindustryBuilding;
import info.teksol.mc.emulator.blocks.graphics.LogicDisplay;
import info.teksol.mc.mindcode.logic.mimex.*;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum AssertionDataType {
    // Basic types
    number(0),
    string(0, String.class),

    // General and specific contents
    content(0, MindustryContent.class),
    item(1, Item.class),
    block(1, BlockType.class),
    bulletType(1, null),
    liquid(1, Liquid.class),
    statusEffect(1, null),
    unitType(1, Unit.class),
    weather(1, Weather.class),
    team(1, Team.class),
    unitCommand(1, UnitCommand.class),
    unitStance(1, null),

    // Any unit
    unit(0, Unit.class),

    // General and specific buildings
    building(0, MindustryBuilding.class),
    processor(1, LogicBlock.class),
    memory(1, MemoryBlock.class),
    message(1, MessageBlock.class),
    display(1, LogicDisplay.class),
    canvas(1, null),

    // Other special values
    property(0, LAccess.class),
    readable(0, null),
    writable(0, null),
    senseable(0, null),
    ;

    private final int level;
    private final Class<?> objectClass;

    AssertionDataType(int level, Class<?> objectClass) {
        this.level = level;
        this.objectClass = objectClass;
    }

    AssertionDataType(int level) {
        this(level, null);
    }

    public static final AssertionDataType[] all = values();
    public static final AssertionDataType[] sorted;

    static {
        sorted = values();
        Arrays.sort(sorted, Comparator.comparingInt(a -> -a.level));
    }

    public boolean matches(LVar var) {
        if (this == number) return !var.isobj;
        if (!var.isobj) return false;
        return objectClass == null || objectClass.isInstance(var.objval);
    }

    /** The classification a failure message shows for the actual value, using the same
     * taxonomy as the game's own variable panel. */
    public static String actualType(LVar var) {
        if (!var.isobj) return "number";
        if (var.objval == null) return "null";

        for (AssertionDataType type: sorted) {
            if (type.objectClass != null && type.objectClass.isInstance(var.objval)) return type.name();
        }
        return "unknown";
    }

    private static final Map<String, AssertionDataType> VALUE_MAP = createValueMap();

    private static Map<String, AssertionDataType> createValueMap() {
        return Stream.of(AssertionDataType.values())
                .collect(Collectors.toMap(Enum::name, e -> e));
    }

    public static @Nullable AssertionDataType byName(String value) {
        return VALUE_MAP.get(value);
    }

    public static AssertionDataType byName(String value, AssertionDataType defaultValue) {
        return VALUE_MAP.getOrDefault(value.toLowerCase(), defaultValue);
    }
}
