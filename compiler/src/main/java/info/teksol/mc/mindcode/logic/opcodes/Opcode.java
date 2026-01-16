package info.teksol.mc.mindcode.logic.opcodes;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static info.teksol.mc.mindcode.logic.opcodes.WorldAction.*;

@NullMarked
public enum Opcode {
    // NOOP
    NOOP            (NONE,   "noop",             "Invalid"),

    // INPUT & OUTPUT
    READ            (READS,  "read",             "Read"),
    WRITE           (WRITES, "write",            "Write"),
    DRAW            (THIS,   "draw",             "Draw"),
    PRINT           (THIS,   "print",            "Print"),
    PRINTCHAR       (THIS,   "printchar",        "PrintChar"),
    FORMAT          (THIS,   "format",           "Format"),

    // BLOCK CONTROL
    DRAWFLUSH       (WRITES, "drawflush",        "DrawFlush"),
    PRINTFLUSH      (WRITES, "printflush",       "PrintFlush"),
    GETLINK         (READS,  "getlink",          "GetLink"),
    CONTROL         (WRITES, "control",          "Control", 1),
    RADAR           (READS,  "radar",            "Radar"),
    SENSOR          (READS,  "sensor",           "Sensor"),

    // OPERATIONS
    SET             (NONE,   "set",              "Set"),
    OP              (NONE,   "op",               "Operation"),
    SELECT          (NONE,   "select",           "Select"),
    LOOKUP          (NONE,   "lookup",           "Lookup"),
    PACKCOLOR       (NONE,   "packcolor",        "PackColor"),
    UNPACKCOLOR     (NONE,   "unpackcolor",      "UnpackColor"),

    // FLOW CONTROL
    WAIT            (THIS,   "wait",             "Wait"),
    STOP            (THIS,   "stop",             "Stop"),
    END             (NONE,   "end",              "End"),
    JUMP            (NONE,   "jump",             "Jump"),

    // UNIT CONTROL
    UBIND           (WRITES, "ubind",            "UnitBind"),
    UCONTROL        (WRITES, "ucontrol",         "UnitControl"),
    URADAR          (READS,  "uradar",           "UnitRadar"),
    ULOCATE         (READS,  "ulocate",          "UnitLocate"),

    // WORLD
    GETBLOCK        (READS,  "getblock",         "GetBlock"),
    SETBLOCK        (WRITES, "setblock",         "SetBlock"),
    SPAWN           (WRITES, "spawn",            "SpawnUnit"),
    STATUS          (WRITES, "status",           "ApplyStatus"),
    WEATHERSENSE    (READS,  "weathersense",     "WeatherSense"),
    WEATHERSET      (WRITES, "weatherset",       "WeatherSet"),
    SPAWNWAVE       (WRITES, "spawnwave",        "SpawnWave"),
    SETRULE         (WRITES, "setrule",          "SetRule"),
    MESSAGE         (WRITES, "message",          "FlushMessage"),
    CUTSCENE        (WRITES, "cutscene",         "Cutscene", 1),
    EFFECT          (WRITES, "effect",           "Effect"),
    EXPLOSION       (WRITES, "explosion",        "Explosion"),
    SETRATE         (THIS,   "setrate",          "SetRate"),
    FETCH           (READS,  "fetch",            "Fetch"),
    SYNC            (BOTH,   "sync",             "Sync"),
    GETFLAG         (READS,  "getflag",          "GetFlag"),
    SETFLAG         (WRITES, "setflag",          "SetFlag"),
    SETPROP         (WRITES, "setprop",          "SetProp"),
    PLAYSOUND       (WRITES, "playsound",        "PlaySound"),
    SETMARKER       (WRITES, "setmarker",        "SetMarker"),
    MAKEMARKER      (WRITES, "makemarker",       "MakeMarker"),
    LOCALEPRINT     (WRITES, "localeprint",      "LocalePrint"),

    // Unit testing support. These instructions are left in the final code for execution by the processor emulator.

    ASSERT_EQUALS   (NONE,   "assertequals",    1),
    ASSERT_PRINTS   (NONE,   "assertprints",    1),
    ASSERT_FLUSH    (NONE,   "assertflush",     1),

    /// Instruction supported by the Mlog Assertions mod.
    ASSERT_BOUNDS   (NONE,   "assertBounds",    1),
    ERROR           (NONE,   "error",           1),

    // Virtual instructions - resolved when the final code is generated
    
    /// Empty instruction. When removing instructions, they can be replaced by this opcode to preserve the structure of the code.
    /// They are removed from the final code.
    EMPTY           (NONE,   "empty",           0),
    
    /// A target for jump, set address or goto instructions. Removed from the final code. 
    LABEL           (NONE,   "label",           0),
    
    /// Simple call. Replaced by an unconditional jump in the final code.
    CALL            (NONE,   "call",            1),

    /// Return from a simple call. Replaced by `set @counter return_address`.
    RETURN          (NONE,   "return",          1),

    /// Stores a variable on stack. Used by recursive functions
    PUSH            (NONE,   "push",            2),

    /// Restores a variable from the stack. Used by recursive functions
    POP             (NONE,   "pop",             2),

    /// Recursive call. Replaced by instructions that store the return address on the stack and then jump to the target
    CALLREC         (NONE,   "callrec",         3),

    /// Return from a recursive function. Replaced by instructions which retrieve the return address from the stack
    /// and jump there (using `set @counter address`, since the target is dynamic).
    RETURNREC       (NONE,   "returnrec",       3),

    /// A target for the MULTIJUMP instruction. Removed from the final code.
    /// One MULTIJUMP instruction can target multiple MULTILABELs, choosing the right one at runtime.
    /// MULTILABELs are tied to a matching MULTIJUMP through a marker.
    MULTILABEL      (NONE,   "multilabel",      0),

    /// Jumps to a dynamic target, potentially using computed offset.
    /// Replaced by a `set @counter ...` or `op add @counter ...` operation.
    MULTIJUMP       (NONE,   "multijump",       1),

    /// Calls a dynamic target (mechanism for internal arrays), potentially using computed offset.
    /// Replaced by an ` op add @counter ...` operation.
    MULTICALL       (NONE,   "multicall",       1),

    /// Assigns a program address represented by a label to a variable. Replaced by a `set` instruction.
    SETADDR         (NONE,   "setaddr",         1),

    /// Represents compiled remark. Replaced by `print`, `jump` + `print` or altogether removed from the final code
    /// depending on compiler options.
    REMARK          (WRITES, "remark",          2),

    /// Read array element
    READARR         (NONE,   "readarr",         4),

    /// Write array element
    WRITEARR        (NONE,   "writearr",        4),

    /// Represents an mlog comment
    COMMENT         (NONE,   "#",               0),

    /// Represents a custom-made instruction (mlog or mlogSafe).
    CUSTOM          (NONE,   "",                1),
    ;

    private final WorldAction action;
    private final String opcode;
    private final String name;
    private final int additionalPrintArguments;
    private final boolean virtual;
    private final int size;

    Opcode(WorldAction action, String opcode, String name) {
        this.action = action;
        this.opcode = opcode;
        this.name = name;
        this.additionalPrintArguments = 0;
        this.virtual = false;
        this.size = 1;
    }

    Opcode(WorldAction action, String opcode, String name, int additionalPrintArguments) {
        this.action = action;
        this.opcode = opcode;
        this.name = name;
        this.additionalPrintArguments = additionalPrintArguments;
        this.virtual = false;
        this.size = 1;
    }

    Opcode(WorldAction action, String opcode, int size) {
        this.action = action;
        this.opcode = opcode;
        this.name = opcode;
        this.additionalPrintArguments = 0;
        this.virtual = true;
        this.size = size;
    }

    public WorldAction getAction() {
        return action;
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

    private static final Set<Opcode> UNSAFE = EnumSet.of(WAIT, STOP, WRITEARR, COMMENT);

    public boolean isSafe() {
        return !UNSAFE.contains(this) && action == NONE || action == WorldAction.READS;
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
