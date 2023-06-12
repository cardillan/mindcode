package info.teksol.mindcode.processor;

/**
 * Possible types of Mindustry Logic expressions
 */
public enum MindustryValueType {
    NULL(false),
    OBJECT(false),
    BOOLEAN(true),
    LONG(true),
    DOUBLE(true),
    ;

    private final boolean numeric;

    MindustryValueType(boolean numeric) {
        this.numeric = numeric;
    }

    public boolean isNumeric() {
        return numeric;
    }
}
