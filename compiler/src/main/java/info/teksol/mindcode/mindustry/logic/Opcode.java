package info.teksol.mindcode.mindustry.logic;

public enum Opcode {
    READ            ("read",            "Read a number from a linked memory cell."),
    WRITE           ("write",           "Write a number to a linked memory cell."),
    DRAW            ("draw",            "Add an operation to the drawing buffer. Does not display anything until drawflush is used."),
    PRINT           ("print",           "Add text to the print buffer. Does not display anything until printflush is used."),

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
    SPAWNWAVE       ("spawnwave",       "Spawn a wave."),
    SETRULE         ("setrule",         "Set a game rule."),
    MESSAGE         ("message",         "Display a message on the screen from the text buffer. Will wait until the previous message finishes."),
    CUTSCENE        ("cutscene",        "Manipulate the player camera.", 1),
    EXPLOSION       ("explosion",       "Create an explosion at a location."),
    SETRATE         ("setrate",         "Set processor execution speed in instructions/tick."),
    FETCH           ("fetch",           "Lookup units, cores, players or buildings by index. Indices start at 0 and end at their returned count."),
    GETFLAG         ("getflag",         "Set a global flag that can be read by all processors."),
    SETFLAG         ("setflag",         "Check if a global flag is set."),

    LABEL           ("label",           0),
    PUSH            ("push",            2),
    POP             ("pop",             2),
    CALL            ("call",            3),
    RETURN          ("return",          3),
    GOTO            ("goto",            1),
    ;
    
    private final String opcode;
    private final String description;
    private final int additionalPrintArguments;
    private final boolean virtual;
    private final int size;

    Opcode(String opcode, String description) {
        this.opcode = opcode;
        this.description = description;
        this.additionalPrintArguments = 0;
        this.virtual = false;
        this.size = 1;
    }
    
    Opcode(String opcode, String description, int additionalPrintArguments) {
        this.opcode = opcode;
        this.description = description;
        this.additionalPrintArguments = additionalPrintArguments;
        this.virtual = false;
        this.size = 1;
    }

    Opcode(String opcode, int size) {
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

    public boolean isVirtual() {
        return virtual;
    }

    @Override
    public String toString() {
        return opcode;
    }
}
