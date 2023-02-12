package info.teksol.mindcode.mindustry.logic;

public enum Opcode {
    READ            ("read"),
    WRITE           ("write"),
    DRAW            ("draw"),
    PRINT           ("print"),
    DRAWFLUSH       ("drawflush"),
    PRINTFLUSH      ("printflush"),
    GETLINK         ("getlink"),
    CONTROL         ("control", 1),
    RADAR           ("radar"),
    SENSOR          ("sensor"),
    SET             ("set"),
    END             ("end"),
    OP              ("op"),
    JUMP            ("jump"),
    UBIND           ("ubind"),
    UCONTROL        ("ucontrol"),
    URADAR          ("uradar"),
    ULOCATE         ("ulocate"),
    LOOKUP          ("lookup"),
    PACKCOLOR       ("packcolor"),
    WAIT            ("wait"),
    STOP            ("stop"),
    LABEL           ("label"),
    ;
    
    private final String opcode;
    private final int additionalPrintArguments;

    private Opcode(String opcode) {
        this.opcode = opcode;
        this.additionalPrintArguments = 0;
    }
    
    private Opcode(String opcode, int additionalPrintArguments) {
        this.opcode = opcode;
        this.additionalPrintArguments = additionalPrintArguments;
    }

    public int getAdditionalPrintArguments() {
        return additionalPrintArguments;
    }

    public String getOpcode() {
        return opcode;
    }
    
    @Override
    public String toString() {
        return opcode;
    }
}
