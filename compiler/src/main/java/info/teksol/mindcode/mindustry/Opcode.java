package info.teksol.mindcode.mindustry;

import java.util.List;

import static info.teksol.mindcode.mindustry.ArgumentType.*;
import static info.teksol.mindcode.mindustry.MindustryVersion.*;

public enum Opcode {
    READ            (V6, "read",        OUTPUT, BLOCK, INPUT),
    WRITE           (V6, "write",       INPUT, BLOCK, INPUT),
    DRAW            (V6, "draw",        ArgumentType.DRAW, INPUT, INPUT, INPUT, INPUT, INPUT, INPUT),
    PRINT           (V6, "print",       INPUT),
    DRAWFLUSH       (V6, "drawflush",   BLOCK),
    PRINTFLUSH      (V6, "printflush",  BLOCK),
    GETLINK         (V6, "getlink",     OUTPUT, INPUT),
    CONTROL         (V6, "control",     BLOCK_CONTROL, BLOCK, INPUT, INPUT, INPUT, ZERO),
    RADAR           (V6, "radar",       ArgumentType.RADAR, ArgumentType.RADAR, ArgumentType.RADAR, SORT, BLOCK, INPUT, OUTPUT),
    SENSOR          (V6, "sensor",      OUTPUT, BLOCK, ArgumentType.SENSOR),
    SET             (V6, "set",         OUTPUT, INPUT),
    END             (V6, "end"),
    OP              (V6, "op",          OPERATION, OUTPUT, INPUT, INPUT),
    JUMP            (V6, "jump",        ArgumentType.LABEL, CONDITION, INPUT, INPUT),
    UBIND           (V6, "ubind",       UNIT),
    UCONTROL        (V6, "ucontrol",    UNIT_CONTROL, INPUT, INPUT, INPUT, INPUT, INPUT),
    URADAR          (V6, "uradar",      ArgumentType.RADAR, ArgumentType.RADAR, ArgumentType.RADAR, SORT, ZERO, INPUT, OUTPUT),
    ULOCATE         (V6, "ulocate",     LOCATE, GROUP, INPUT, ORE, OUTPUT, OUTPUT, OUTPUT, OUTPUT),
    WAIT            (V7, "wait",        INPUT),
    LABEL           (NONE, "label",     ArgumentType.LABEL),
    ;
    
    private final String opcode;
    private final List<ArgumentType> argumentTypes;
    private final MindustryVersion mindustryVersion;

    private Opcode(MindustryVersion mindustryVersion, String code, ArgumentType... argumentTypes) {
        this.mindustryVersion = mindustryVersion;
        this.opcode = code;
        this.argumentTypes = List.of(argumentTypes);
    }

    public String getOpcode() {
        return opcode;
    }
    
    public int getArgumentCount() {
        return argumentTypes.size();
    }

    public List<ArgumentType> getArgumentTypes() {
        return argumentTypes;
    }

    @Override
    public String toString() {
        return opcode;
    }
}
