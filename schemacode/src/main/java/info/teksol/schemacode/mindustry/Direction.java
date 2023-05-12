package info.teksol.schemacode.mindustry;

public enum Direction {
    EAST,
    NORTH,
    WEST,
    SOUTH,
    ;

    private final String schemacode;

    Direction() {
        schemacode = name().toLowerCase().concat(" ").substring(0, 5);
    }

    public static Direction convert(byte code) {
        return switch (code) {
            case 0 -> EAST;
            case 1 -> NORTH;
            case 2 -> WEST;
            case 3 -> SOUTH;
            default -> EAST; //throw new UnsupportedOperationException("Unknown rotation " + code);
        };
    }

    public String toSchemacode() {
        return schemacode;
    }
}
