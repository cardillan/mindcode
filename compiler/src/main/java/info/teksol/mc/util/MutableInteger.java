package info.teksol.mc.util;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class MutableInteger extends Number implements Comparable<MutableInteger> {
    private int value;

    // --- Private constructors ---
    private MutableInteger() {
        this(0);
    }

    private MutableInteger(int value) {
        this.value = value;
    }

    // --- Static factory methods ---

    /** Creates a MutableDouble with the given value. */
    public static MutableInteger of(int value) {
        return new MutableInteger(value);
    }

    /** Creates a MutableDouble with the given Number's int value. */
    public static MutableInteger of(Number number) {
        return new MutableInteger(number.intValue());
    }

    /** Returns a new MutableDouble initialized to 0.0. */
    public static MutableInteger zero() {
        return new MutableInteger(0);
    }

    /** Returns a new MutableDouble initialized to 1.0. */
    public static MutableInteger one() {
        return new MutableInteger(1);
    }

    // --- Accessors ---

    public int get() {
        return value;
    }

    public void set(int value) {
        this.value = value;
    }

    // --- Arithmetic operations ---

    public MutableInteger add(int other) {
        this.value += other;
        return this;
    }

    public MutableInteger subtract(int other) {
        this.value -= other;
        return this;
    }

    public MutableInteger multiply(int other) {
        this.value *= other;
        return this;
    }

    public MutableInteger divide(int other) {
        this.value /= other;
        return this;
    }

    public MutableInteger negate() {
        this.value = -this.value;
        return this;
    }

    public int incrementAndGet() {
        return ++value;
    }

    public int decrementAndGet() {
        return --value;
    }

    public int getAndIncrement() {
        return value++;
    }

    public int getAndDecrement() {
        return value--;
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
        return value;
    }

    @Override
    public long longValue() {
        return value;
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
    public int compareTo(MutableInteger other) {
        return Double.compare(this.value, other.value);
    }

    // --- Object methods ---

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MutableInteger other)) return false;
        return this.value == other.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
