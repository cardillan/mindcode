package info.teksol.mc.mindcode.logic.instructions;

public enum ArrayConstruction {
    COMPACT("compact"),
    REGULAR("regular"),
    ;

    private final String name;

    ArrayConstruction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean accessByName() {
        return this == COMPACT;
    }
}
