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

    public static Direction convert(int code) {
        return switch (code) {
            case 0 -> EAST;
            case 1 -> NORTH;
            case 2 -> WEST;
            case 3 -> SOUTH;
            default -> EAST; //throw new UnsupportedOperationException("Unknown rotation " + code);
        };
    }

    public Direction rotate(Direction direction) {
        return convert((this.ordinal() + direction.ordinal()) % 4);
    }

    public String toSchemacode() {
        return schemacode;
    }
}
