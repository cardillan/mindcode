package info.teksol.mindcode.logic;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Opcode {
    READ            ("read",            "Read a number from a linked memory cell."),
    WRITE           ("write",           "Write a number to a linked memory cell."),
    DRAW            ("draw",            "Add an operation to the drawing buffer. Does not display anything until drawflush is used."),
    PRINT           ("print",           "Add text to the print buffer. Does not display anything until printflush is used."),
    FORMAT          ("format",          "Replace next placeholder in text buffer with a value. Does not do anything if placeholder pattern is invalid."
            + " Placeholder pattern: \"{number 0-9}\" Example: print \"test {0}\"; format \"example\""),

    DRAWFLUSH       ("drawflush",       "Flush queued Draw operations to a display."),
    PRINTFLUSH      ("printflush",      "Flush queued Print operations to a message block."),
    GETLINK         ("getlink",         "Get a processor link by index. Starts at 0."),
    CONTROL         ("control",         "Control a building.", 1),
    RADAR           ("radar",           "Locate units around a building with range."),
    SENSOR          ("sensor",          "Get data from a building or unit."),

    SET             ("set",             "Set a variable."),
    OP              ("op",              "Perform an operation on 1-2 variables."),
    LOOKUP          ("lookup",          "Look up an item/liquid/unit/block type by ID. Total counts of each type can be accessed with @unitCount, @itemCount, @liquidCount, @blockCount."),
    PACKCOLOR       ("packcolor",       "Pack [0, 1] RGBA components into a single number for drawing or rule-setting."),

    WAIT            ("wait",            "Wait a certain number of seconds."),
    STOP            ("stop",            "Halt execution of this processor."),
    END             ("end",             "Jump to the top of the instruction stack."),
    JUMP            ("jump",            "Conditionally jump to another statement."),

    UBIND           ("ubind",           "Bind to the next unit of a type, and store it in @unit."),
    UCONTROL        ("ucontrol",        "Control the currently bound unit."),
    URADAR          ("uradar",          "Locate units around the currently bound unit."),
    ULOCATE         ("ulocate",         "Locate a specific type of position/building anywhere on the map. Requires a bound unit."),

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

    // Unit testing support
    ASSERT_EQUALS   ("assertequals",    1),
    ASSERT_PRINTS   ("assertprints",    1),
    ASSERT_FLUSH    ("assertflush",     1),

    // Virtual instructions - resolved when the final code is generated
    NOOP            ("noop",            0),
    LABEL           ("label",           0),
    GOTOLABEL       ("gotolabel",       0),
    PUSH            ("push",            2),
    POP             ("pop",             2),
    CALL            ("call",            1),
    CALLREC         ("callrec",         3),
    RETURN          ("return",          3),
    GOTO            ("goto",            1),
    GOTOOFFSET      ("gotooffset",      1),
    SETADDR         ("setaddr",         1),
    REMARK          ("remark",          2),

    // For custom-made instructions - size is always 1
    CUSTOM          ("",1),
    ;

    /**
     * Safe instructions are instructions which do not have any output parameters (and therefore are assumed
     * to interact with the outside world), or have output parameters and do not interact with the outside world.
     * Instructions which do have output variables, but do interact with the outside world, are unsafe.
     */
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

    public static Opcode fromOpcode(String opcode) {
        return MAP.get(opcode);
    }
}
