package info.teksol.mc.mindcode.logic.opcodes;

import info.teksol.mc.mindcode.logic.mimex.BlockType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public enum ProcessorType {
    MICRO_PROCESSOR         ("Micro Processor",  2, false),
    LOGIC_PROCESSOR         ("Logic Processor",  8, false),
    HYPER_PROCESSOR         ("Hyper Processor", 25, false),
    WORLD_PROCESSOR         ("World processor",  8,  true),
    ;

    private final String title;
    private final int ipt;
    private final boolean privileged;

    ProcessorType(String title, int ipt, boolean privileged) {
        this.title = title;
        this.ipt = ipt;
        this.privileged = privileged;
    }

    public String getTitle() {
        return title;
    }

    public int ipt() {
        return ipt;
    }

    public boolean privileged() {
        return privileged;
    }

    public String code() {
        return switch (this) {
            case MICRO_PROCESSOR -> "m";
            case LOGIC_PROCESSOR -> "l";
            case HYPER_PROCESSOR -> "h";
            case WORLD_PROCESSOR -> "w";
        };
    }

    public String typeName() {
        return switch (this) {
            case MICRO_PROCESSOR -> "@micro-processor";
            case LOGIC_PROCESSOR -> "@logic-processor";
            case HYPER_PROCESSOR -> "@hyper-processor";
            case WORLD_PROCESSOR -> "@world-processor";
        };
    }


    /// This processor type is compatible with another processor type if code compiled under this processor type
    /// can also run on the other processor type.
    ///
    /// @return true if this type is compatible with the other type
    public boolean isCompatibleWith(ProcessorType other) {
        return ordinal() <= other.ordinal();
    }

    public static final ProcessorType S = ProcessorType.MICRO_PROCESSOR;
    public static final ProcessorType W = ProcessorType.WORLD_PROCESSOR;

    public static @Nullable ProcessorType byCode(char code) {
        return switch (code) {
            case 's', 'S', 'm', 'M' -> MICRO_PROCESSOR;
            case 'l', 'L' -> LOGIC_PROCESSOR;
            case 'h', 'H' -> HYPER_PROCESSOR;
            case 'w', 'W' -> WORLD_PROCESSOR;
            default -> null;
        };
    }

    public static @Nullable ProcessorType fromBlockType(BlockType blockType) {
        return switch (blockType.contentName()) {
            case "micro-processor" -> MICRO_PROCESSOR;
            case "logic-processor" -> LOGIC_PROCESSOR;
            case "hyper-processor" -> HYPER_PROCESSOR;
            case "world-processor" -> WORLD_PROCESSOR;
            default -> null;
        };
    }
}
