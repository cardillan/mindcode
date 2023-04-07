package info.teksol.mindcode.processor;

/**
 * Possible types of Mindustry Logic expressions
 */
public enum ValueType {
    NULL(false),
    OBJECT(false),
    BOOLEAN(true),
    LONG(true),
    DOUBLE(true),
    ;

    private final boolean numeric;

    ValueType(boolean numeric) {
        this.numeric = numeric;
    }

    public boolean isNumeric() {
        return numeric;
    }
}
