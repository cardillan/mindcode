package info.teksol.mc.util;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class MutableBoolean implements Comparable<MutableBoolean> {
    private boolean value;

    // --- Private constructors ---
    private MutableBoolean() {
        this(false);
    }

    private MutableBoolean(boolean value) {
        this.value = value;
    }

    // --- Static factory methods ---

    /** Creates a MutableDouble with the given value. */
    public static MutableBoolean of(boolean value) {
        return new MutableBoolean(value);
    }

    /** Returns a new MutableDouble initialized to 0.0. */
    public static MutableBoolean ofTrue() {
        return new MutableBoolean(true);
    }

    /** Returns a new MutableDouble initialized to 1.0. */
    public static MutableBoolean ofFalse() {
        return new MutableBoolean(false);
    }

    // --- Accessors ---

    public boolean get() {
        return value;
    }

    public void set(boolean value) {
        this.value = value;
    }

    // --- Arithmetic operations ---

    public MutableBoolean negate() {
        this.value = !this.value;
        return this;
    }

    // --- Comparable ---

    @Override
    public int compareTo(MutableBoolean other) {
        return Boolean.compare(this.value, other.value);
    }

    // --- Object methods ---

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MutableBoolean other)) return false;
        return this.value == other.value;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(value);
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
