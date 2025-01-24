package info.teksol.mc.mindcode.logic.opcodes;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public enum ProcessorEdition {
    STANDARD_PROCESSOR      ("Micro Processor, Logic Processor and Hyper Processor"),
    WORLD_PROCESSOR         ("World processor"),
    ;

    private final String title;

    ProcessorEdition(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static final ProcessorEdition S = ProcessorEdition.STANDARD_PROCESSOR;
    public static final ProcessorEdition W = ProcessorEdition.WORLD_PROCESSOR;

    public static @Nullable ProcessorEdition byCode(char code) {
        return switch (code) {
            case 's', 'S' -> STANDARD_PROCESSOR;
            case 'w', 'W' -> WORLD_PROCESSOR;
            default -> null;
        };
    }
}
