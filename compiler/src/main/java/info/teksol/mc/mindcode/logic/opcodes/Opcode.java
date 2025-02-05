package info.teksol.mc.mindcode.logic.opcodes;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
public enum Opcode {
    // INPUT & OUTPUT
    READ            ("read",            "Read a number from a linked memory cell."),
    WRITE           ("write",           "Write a number to a linked memory cell."),
    DRAW            ("draw",            "Add an operation to the drawing buffer. Does not display anything until drawflush is used."),
    PRINT           ("print",           "Add text to the print buffer. Does not display anything until printflush is used."),
    FORMAT          ("format",          "Replace next placeholder in text buffer with a value. Does not do anything if placeholder pattern is invalid."
            + " Placeholder pattern: \"{number 0-9}\" Example: print \"test {0}\"; format \"example\""),

    // BLOCK CONTROL
    DRAWFLUSH       ("drawflush",       "Flush queued Draw operations to a display."),
    PRINTFLUSH      ("printflush",      "Flush queued Print operations to a message block."),
    GETLINK         ("getlink",         "Get a processor link by index. Starts at 0."),
    CONTROL         ("control",         "Control a building.", 1),
    RADAR           ("radar",           "Locate units around a building with range."),
    SENSOR          ("sensor",          "Get data from a building or unit."),

    // OPERATIONS
    SET             ("set",             "Set a variable."),
    OP              ("op",              "Perform an operation on 1-2 variables."),
    LOOKUP          ("lookup",          "Look up an item/liquid/unit/block type by ID. Total counts of each type can be accessed with @unitCount, @itemCount, @liquidCount, @blockCount."),
    PACKCOLOR       ("packcolor",       "Pack [0, 1] RGBA components into a single number for drawing or rule-setting."),

    // FLOW CONTROL
    WAIT            ("wait",            "Wait a certain number of seconds."),
    STOP            ("stop",            "Halt execution of this processor."),
    END             ("end",             "Jump to the top of the instruction stack."),
    JUMP            ("jump",            "Conditionally jump to another statement."),

    // UNIT CONTROL
    UBIND           ("ubind",           "Bind to the next unit of a type, and store it in @unit."),
    UCONTROL        ("ucontrol",        "Control the currently bound unit."),
    URADAR          ("uradar",          "Locate units around the currently bound unit."),
    ULOCATE         ("ulocate",         "Locate a specific type of position/building anywhere on the map. Requires a bound unit."),

    // WORLD
    GETBLOCK        ("getblock",        "Get tile data at any location."),
    SETBLOCK        ("setblock",        "Set tile data at any location."),
    SPAWN           ("spawn",           "Spawn unit at a location."),
    STATUS          ("status",          "Apply or clear a status effect from a unit."),
    WEATHERSENSE    ("weathersense",    "Check if a type of weather is active."),
    WEATHERSET      ("weatherset",      "Set the current state of a type of weather."),
    SPAWNWAVE       ("spawnwave",       "Spawn a wave."),
    SETRULE         ("setrule",         "Set a game rule."),
    MESSAGE         (false, "message", "Display a message on the screen from the text buffer. If the success result variable is @wait, will wait until the previous message finishes. Otherwise, outputs whether displaying the message succeeded."),
    CUTSCENE        ("cutscene",        "Manipulate the player camera.", 1),
    EFFECT          ("effect",          "Create a particle effect."),
    EXPLOSION       ("explosion",       "Create an explosion at a location."),
    SETRATE         ("setrate",         "Set processor execution speed in instructions/tick."),
    FETCH           ("fetch",           "Lookup units, cores, players or buildings by index. Indices start at 0 and end at their returned count."),
    SYNC            ("sync",            "Sync a variable across the network. Limited to 20 times a second per variable."),
    GETFLAG         ("getflag",         "Check if a global flag is set."),
    SETFLAG         ("setflag",         "Set a global flag that can be read by all processors."),
    SETPROP         ("setprop",         "Sets a property of a unit or building."),
    PLAYSOUND       ("playsound",       "Plays a sound. Volume and pan can be a global value, or calculated based on position."),
    SETMARKER       ("setmarker",       "Set a property for a marker. The ID used must be the same as in the Make Marker instruction. null values are ignored."),
    MAKEMARKER      ("makemarker",      "Create a new logic marker in the world. An ID to identify this marker must be provided. Markers currently limited to 20,000 per world."),
    LOCALEPRINT     ("localeprint",     "Add map locale property value to the text buffer. To set map locale bundles in map editor, check Map Info > Locale Bundles. If client is a mobile device, tries to print a property ending in \".mobile\" first."),

    // Unit testing support. These instructions are left in final code for execution by the processor emulator.

    ASSERT_EQUALS   ("assertequals",    1),
    ASSERT_PRINTS   ("assertprints",    1),
    ASSERT_FLUSH    ("assertflush",     1),

    /// Instruction supported by the Mlog Assertions mod.
    ASSERT_BOUNDS   ("assertBounds",          1),

    // Virtual instructions - resolved when the final code is generated
    
    /// No operation. When removing instructions, they can be replaced by no-op to preserve the structure of the code.
    /// They are removed from the final code.
    NOOP            ("noop",            0),
    
    /// A target for jump, set address or goto instructions. Removed from the final code. 
    LABEL           ("label",           0),
    
    /// Simple call. Replaced by an unconditional jump in final code.
    CALL            ("call",            1),

    /// Return from a simple call. Replaced by `set @counter return_address`.
    RETURN          ("return",          1),

    /// Stores a variable on stack. Used by recursive functions
    PUSH            ("push",            2),

    /// Restores a variable from stack. Used by recursive functions
    POP             ("pop",             2),

    /// Recursive call. Replaced by instructions which stores the return address on stack and then jumps to the target
    CALLREC         ("callrec",         3),

    /// Return from a recursive function. Replaced by instructions which retrieve the return address from stack
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
    /// Replaced by a `op add @counter ...` operation.
    MULTICALL       ("multicall",       1),

    /// Assigns program address represented by a label to a variable. Replaced by a `set` instruction.
    SETADDR         ("setaddr",         1),

    /// Represents compiled remark. Replaced by `print`, `jump` + `print` or altogether removed from final code
    /// depending on compiler options.
    REMARK          ("remark",          2),

    /// Read array element
    READARR         ("readarr",         4),

    /// Write array element
    WRITEARR        ("writearr",        4),

    /// Represents a custom-made instruction (mlog or mlogSafe).
    CUSTOM          ("",                1),
    ;

    /// Safe instructions are instructions which do not have any output parameters (and therefore are assumed
    /// to interact with the outside world), or have output parameters and do not interact with the outside world.
    /// Instructions which do have output variables, but do interact with the outside world, are unsafe, and are
    /// explicitly marked as such with this attribute.
    private final boolean safe;
    private final String opcode;
    private final String description;
    private final int additionalPrintArguments;
    private final boolean virtual;
    private final int size;

    Opcode(String opcode, String description) {
        this.safe = true;
        this.opcode = opcode;
        this.description = description;
        this.additionalPrintArguments = 0;
        this.virtual = false;
        this.size = 1;
    }
    
    Opcode(boolean safe, String opcode, String description) {
        this.safe = safe;
        this.opcode = opcode;
        this.description = description;
        this.additionalPrintArguments = 0;
        this.virtual = false;
        this.size = 1;
    }

    Opcode(String opcode, String description, int additionalPrintArguments) {
        this.safe = true;
        this.opcode = opcode;
        this.description = description;
        this.additionalPrintArguments = additionalPrintArguments;
        this.virtual = false;
        this.size = 1;
    }

    Opcode(String opcode, int size) {
        this.safe = true;
        this.opcode = opcode;
        this.description = "Virtual instruction.";
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

    public String getDescription() {
        return description;
    }

    public int getSize() {
        return size;
    }

    public boolean isSafe() {
        return safe;
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
