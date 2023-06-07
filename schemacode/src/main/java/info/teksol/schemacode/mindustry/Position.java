package info.teksol.schemacode.mindustry;

import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.config.PositionArray;
import info.teksol.schemacode.schema.Block;

import java.util.function.UnaryOperator;

public record Position(int x, int y) implements Comparable<Position>, Configuration {

    private static final int INVALID_COORDINATE = -255;

    public static final Position ORIGIN = new Position(0, 0);

    public static final Position INVALID = new Position(INVALID_COORDINATE, INVALID_COORDINATE);

    @Override
    public <T extends Configuration> T as(Class<T> type) {
        return type == PositionArray.class ? type.cast(new PositionArray(this)) : Configuration.super.as(type);
    }

    /**
     * Adds another 2D grid point to this point.
     *
     * @param other The other point
     * @return this 2d grid point for chaining.
     */
    public Position add(Position other) {
        return invalid() || other.zero() ? this : new Position(x + other.x, y + other.y);
    }

    public Position add(int offset) {
        return invalid() || offset == 0 ? this : new Position(x + offset, y + offset);
    }

    /**
     * Subtracts another 2D grid point from this point.
     *
     * @param other The other point
     * @return this 2d grid point for chaining.
     */
    public Position sub(Position other) {
        return invalid() || other.zero() ? this : new Position(x - other.x, y - other.y);
    }

    public Position sub(int offset) {
        return invalid() || offset == 0 ? this : new Position(x - offset, y - offset);
    }

    public Position remap(UnaryOperator<Position> mapping) {
        return mapping.apply(this);
    }

    public boolean nonNegative() {
        return x >= 0 && y >= 0;
    }

    public boolean zero() {
        return x == 0 && y == 0;
    }

    public boolean invalid() {
        return x == INVALID_COORDINATE && y == INVALID_COORDINATE;
    }

    public boolean nonPositive() {
        return x <= 0 && y <= 0 && (x | y) != 0;
    }

    public boolean orthogonal(Position position) {
        return x == position.x || y == position.y;
    }

    /**
     * @return a point unpacked from an integer.
     */
    public static Position unpack(int pos) {
        return new Position((short) (pos >>> 16), (short) (pos & 0xFFFF));
    }

    public static int pack(int x, int y) {
        return (((short) x) << 16) | (((short) y) & 0xFFFF);
    }

    /**
     * @return this point packed into a single int by casting its components to shorts.
     */
    public int pack() {
        return (((short) x) << 16) | (((short) y) & 0xFFFF);
    }

    @Override
    public Configuration encode(Block block) {
        return sub(block.position());
    }

    @Override
    public int compareTo(Position o) {
        return 10 * Integer.compare(x, o.x) + Integer.compare(y, o.y);
    }

    @Override
    public String toString() {
        return "Position(" + x + ", " + y + ")";
    }

    /**
     * Converts to a Schemacode representation, either absolute (if refPos is null), or relative to given
     * reference position.
     *
     * @param refPos reference position, use null if absolute coordinates are desired
     * @return Schemacode representation
     */
    public String toString(Position refPos) {
        return refPos == null ? toStringAbsolute() : sub(refPos).toRelativePositionString();
    }

    /**
     * Converts to a Schemacode representation, either absolute (if refPos is null), or relative to given
     * reference position, if the reference position is "near".
     *
     * @param refPos reference position, use null if absolute coordinates are desired
     * @return Schemacode representation
     */
    public String toStringNear(Position refPos) {
        Position relative = refPos == null ? null : sub(refPos);
        return relative == null || !relative.isNear() ? toStringAbsolute() : relative.toRelativePositionString();
    }

    private boolean isNear() {
        return nonNegative() && (x == 0 || y == 0) && x + y <= 9;
    }

    public String toStringAbsolute() {
        return String.format("(%2d, %2d)", x, y);
    }

    private String toRelativePositionString() {
        return nonPositive()
                ? "-(" + (-x) + ", " + (-y) + ")"
                : "+(" + x + ", " + y + ")";
    }
}
