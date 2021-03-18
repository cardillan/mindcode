package info.teksol.mindcode.ast;

import info.teksol.mindcode.ParsingException;

import java.util.Objects;

public class HeapAllocation implements AstNode {
    private final String name;
    private final int first;
    private final int last;

    public HeapAllocation(String name, Range range) {
        if (
                !(range.getFirstValue() instanceof NumericLiteral)
                        || !(range.getLastValue() instanceof NumericLiteral)
                ) {
            throw new InvalidHeapAllocationException("Heap declarations must use numeric literals; received " + range);
        }

        int candidate = Integer.valueOf(((NumericLiteral) range.getLastValue()).getLiteral());
        if (range instanceof ExclusiveRange) {
            candidate -= 1;
        } else if (range instanceof InclusiveRange) {
            candidate += 0; // NOP: the last value is already at the proper one
        } else {
            throw new ParsingException("Received unexpected type of Range: " + range);
        }

        this.name = name;
        this.first = Integer.valueOf(((NumericLiteral) range.getFirstValue()).getLiteral());
        this.last = candidate;
    }

    public HeapAllocation(String name, int first, int last) {
        this.name = name;
        this.first = first;
        this.last = last;
    }

    public String getName() {
        return name;
    }

    public int getFirst() {
        return first;
    }

    public int size() {
        return (last - first) + 1;
    }

    public AstNode addressOf(int location) {
        return new NumericLiteral(String.valueOf(getFirst() + location));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeapAllocation that = (HeapAllocation) o;
        return first == that.first &&
                last == that.last &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, first, last);
    }

    @Override
    public String toString() {
        return "HeapAllocation{" +
                "name='" + name + '\'' +
                ", first=" + first +
                ", last=" + last +
                '}';
    }
}
