package info.teksol.mc.util;

public class MutableDouble extends Number implements Comparable<MutableDouble> {
    private double value;

    // --- Private constructors ---
    private MutableDouble() {
        this(0.0);
    }

    private MutableDouble(double value) {
        this.value = value;
    }

    // --- Static factory methods ---

    /** Creates a MutableDouble with the given value. */
    public static MutableDouble of(double value) {
        return new MutableDouble(value);
    }

    /** Creates a MutableDouble with the given Number's double value. */
    public static MutableDouble of(Number number) {
        return new MutableDouble(number.doubleValue());
    }

    /** Returns a new MutableDouble initialized to 0.0. */
    public static MutableDouble zero() {
        return new MutableDouble(0.0);
    }

    /** Returns a new MutableDouble initialized to 1.0. */
    public static MutableDouble one() {
        return new MutableDouble(1.0);
    }

    // --- Accessors ---

    public double get() {
        return value;
    }

    public void set(double value) {
        this.value = value;
    }

    // --- Arithmetic operations ---

    public MutableDouble add(double other) {
        this.value += other;
        return this;
    }

    public MutableDouble subtract(double other) {
        this.value -= other;
        return this;
    }

    public MutableDouble multiply(double other) {
        this.value *= other;
        return this;
    }

    public MutableDouble divide(double other) {
        this.value /= other;
        return this;
    }

    public MutableDouble negate() {
        this.value = -this.value;
        return this;
    }

    // --- Predicates ---

    public boolean isZero() {
        return value == 0.0;
    }

    public boolean isPositive() {
        return value > 0.0;
    }

    public boolean isNegative() {
        return value < 0.0;
    }

    // --- Number methods ---

    @Override
    public int intValue() {
        return (int) value;
    }

    @Override
    public long longValue() {
        return (long) value;
    }

    @Override
    public float floatValue() {
        return (float) value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    // --- Comparable ---

    @Override
    public int compareTo(MutableDouble other) {
        return Double.compare(this.value, other.value);
    }

    // --- Object methods ---

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MutableDouble other)) return false;
        return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(other.value);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(Double.doubleToLongBits(value));
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }
}
