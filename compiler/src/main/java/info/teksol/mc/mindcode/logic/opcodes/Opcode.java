package info.teksol.mc.mindcode.logic.opcodes;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
public enum Opcode {
    // NOOP
    NOOP            (true,  "noop",             "Invalid"),

    // INPUT & OUTPUT
    READ            (true,  "read",             "Read"),
    WRITE           (false, "write",            "Write"),
    DRAW            (false, "draw",             "Draw"),
    PRINT           (false, "print",            "Print"),
    PRINTCHAR       (false, "printchar",        "PrintChar"),
    FORMAT          (false, "format",           "Format"),

    // BLOCK CONTROL
    DRAWFLUSH       (false, "drawflush",        "DrawFlush"),
    PRINTFLUSH      (false, "printflush",       "PrintFlush"),
    GETLINK         (true,  "getlink",          "GetLink"),
    CONTROL         (false, "control",          "Control", 1),
    RADAR           (true,  "radar",            "Radar"),
    SENSOR          (true,  "sensor",           "Sensor"),

    // OPERATIONS
    SET             (true,  "set",              "Set"),
    OP              (true,  "op",               "Operation"),
    SELECT          (true,  "select",           "Select"),
    LOOKUP          (true,  "lookup",           "Lookup"),
    PACKCOLOR       (true,  "packcolor",        "PackColor"),
    UNPACKCOLOR     (true,  "unpackcolor",      "UnpackColor"),

    // FLOW CONTROL
    WAIT            (false, "wait",             "Wait"),
    STOP            (false, "stop",             "Stop"),
    END             (true,  "end",              "End"),
    JUMP            (true,  "jump",             "Jump"),

    // UNIT CONTROL
    UBIND           (false, "ubind",            "UnitBind"),
    UCONTROL        (false, "ucontrol",         "UnitControl"),
    URADAR          (true,  "uradar",           "UnitRadar"),
    ULOCATE         (true,  "ulocate",          "UnitLocate"),

    // WORLD
    GETBLOCK        (true,  "getblock",         "GetBlock"),
    SETBLOCK        (false, "setblock",         "SetBlock"),
    SPAWN           (false, "spawn",            "SpawnUnit"),
    STATUS          (false, "status",           "ApplyStatus"),
    WEATHERSENSE    (true,  "weathersense",     "WeatherSense"),
    WEATHERSET      (false, "weatherset",       "WeatherSet"),
    SPAWNWAVE       (false, "spawnwave",        "SpawnWave"),
    SETRULE         (false, "setrule",          "SetRule"),
    MESSAGE         (false, "message",          "FlushMessage"),
    CUTSCENE        (false, "cutscene",         "Cutscene", 1),
    EFFECT          (false, "effect",           "Effect"),
    EXPLOSION       (false, "explosion",        "Explosion"),
    SETRATE         (false, "setrate",          "SetRate"),
    FETCH           (true,  "fetch",            "Fetch"),
    SYNC            (false, "sync",             "Sync"),
    GETFLAG         (true,  "getflag",          "GetFlag"),
    SETFLAG         (false, "setflag",          "SetFlag"),
    SETPROP         (false, "setprop",          "SetProp"),
    PLAYSOUND       (false, "playsound",        "PlaySound"),
    SETMARKER       (false, "setmarker",        "SetMarker"),
    MAKEMARKER      (false, "makemarker",       "MakeMarker"),
    LOCALEPRINT     (false, "localeprint",      "LocalePrint"),

    // Unit testing support. These instructions are left in the final code for execution by the processor emulator.

    ASSERT_EQUALS   ("assertequals",    1),
    ASSERT_PRINTS   ("assertprints",    1),
    ASSERT_FLUSH    ("assertflush",     1),

    /// Instruction supported by the Mlog Assertions mod.
    ASSERT_BOUNDS   ("assertBounds",    1),
    ERROR           ("error",           1),

    // Virtual instructions - resolved when the final code is generated
    
    /// Empty instruction. When removing instructions, they can be replaced by this opcode to preserve the structure of the code.
    /// They are removed from the final code.
    EMPTY           ("empty",           0),
    
    /// A target for jump, set address or goto instructions. Removed from the final code. 
    LABEL           ("label",           0),
    
    /// Simple call. Replaced by an unconditional jump in the final code.
    CALL            ("call",            1),

    /// Return from a simple call. Replaced by `set @counter return_address`.
    RETURN          ("return",          1),

    /// Stores a variable on stack. Used by recursive functions
    PUSH            ("push",            2),

    /// Restores a variable from the stack. Used by recursive functions
    POP             ("pop",             2),

    /// Recursive call. Replaced by instructions that store the return address on the stack and then jump to the target
    CALLREC         ("callrec",         3),

    /// Return from a recursive function. Replaced by instructions which retrieve the return address from the stack
    /// and jump there (using `set @counter address`, since the target is dynamic).
    RETURNREC       ("returnrec",       3),

    /// A target for the MULTIJUMP instruction. Removed from the final code.
    /// One MULTIJUMP instruction can target multiple MULTILABELs, choosing the right one at runtime.
    /// MULTILABELs are tied to a matching MULTIJUMP through a marker.
    MULTILABEL      ("multilabel",      0),

    /// Jumps to a dynamic target, potentially using computed offset.
    /// Replaced by a `set @counter ...` or `op add @counter ...` operation.
    MULTIJUMP       ("multijump",       1),

    /// Calls a dynamic target (mechanism for internal arrays), potentially using computed offset.
    /// Replaced by an ` op add @counter ...` operation.
    MULTICALL       ("multicall",       1),

    /// Assigns a program address represented by a label to a variable. Replaced by a `set` instruction.
    SETADDR         ("setaddr",         1),

    /// Represents compiled remark. Replaced by `print`, `jump` + `print` or altogether removed from the final code
    /// depending on compiler options.
    REMARK          (false, "remark",          2),

    /// Read array element
    READARR         ("readarr",         4),

    /// Write array element
    WRITEARR        (false, "writearr",        4),

    /// Represents an mlog comment
    COMMENT         (false, "#",               0),

    /// Represents a custom-made instruction (mlog or mlogSafe).
    CUSTOM          ("",                1),
    ;

    /// Safe instructions are instructions that do not have any output parameters (and therefore are assumed
    /// to interact with the outside world), or have output parameters and do not interact with the outside world.
    /// Instructions which do have output variables but do interact with the outside world are unsafe and are
    /// explicitly marked as such with this attribute.
    private final boolean safe;
    private final String opcode;
    private final String name;
    private final int additionalPrintArguments;
    private final boolean virtual;
    private final int size;

    Opcode(boolean safe, String opcode, String name) {
        this.safe = safe;
        this.opcode = opcode;
        this.name = name;
        this.additionalPrintArguments = 0;
        this.virtual = false;
        this.size = 1;
    }

    Opcode(boolean safe, String opcode, String name, int additionalPrintArguments) {
        this.safe = safe;
        this.opcode = opcode;
        this.name = name;
        this.additionalPrintArguments = additionalPrintArguments;
        this.virtual = false;
        this.size = 1;
    }

    Opcode(String opcode, int size) {
        this.safe = true;
        this.opcode = opcode;
        this.name = opcode;
        this.additionalPrintArguments = 0;
        this.virtual = true;
        this.size = size;
    }

    Opcode(boolean safe, String opcode, int size) {
        this.safe = safe;
        this.opcode = opcode;
        this.name = opcode;
        this.additionalPrintArguments = 0;
        this.virtual = true;
        this.size = size;
    }

    public int getAdditionalPrintArguments() {
        return additionalPrintArguments;
    }

    public String getOpcode() {
        return opcode;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public boolean isSafe() {
        return safe;
    }

    /// Indicates whether the instruction interacts with the world (either by querying it or by modifying it)
    public boolean worldAccess() {
        return switch (this) {
            case NOOP,
                 // GETLINK does read the world, but no other logic can modify what it reads, so it is
                 // indifferent to world access.
                 GETLINK,
                 SET, OP, SELECT, LOOKUP, PACKCOLOR, UNPACKCOLOR,
                 ASSERT_PRINTS, ASSERT_FLUSH -> false;
            case CUSTOM -> true;
            default -> !virtual;
        };
    }

    public boolean isVirtual() {
        return virtual;
    }

    @Override
    public String toString() {
        return opcode;
    }

    private static final Map<String, Opcode> MAP = Stream.of(values())
            .collect(Collectors.toMap(Opcode::getOpcode, o -> o));

    public static @Nullable Opcode fromOpcode(String opcode) {
        return MAP.get(opcode);
    }
}
