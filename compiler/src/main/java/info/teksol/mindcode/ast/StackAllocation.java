package info.teksol.mindcode.ast;

import java.util.Objects;

public class StackAllocation extends BaseAstNode {
    private final String name;
    private final Range range;

    StackAllocation(String name, Range range) {
        this.name = name;
        this.range = range;
    }

    StackAllocation(String name, int first, int last) {
        this(name, new InclusiveRange(new NumericLiteral(first), new NumericLiteral(last)));
    }

    StackAllocation(String name) {
        this.name = name;
        this.range = null;
    }

    public String getName() {
        return name;
    }

    public Range getRange() {
        return range;
    }

    public boolean hasRange() {
        return range != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StackAllocation that = (StackAllocation) o;
        return Objects.equals(range, that.range)
                && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, range);
    }

    @Override
    public String toString() {
        return "StackAllocation{" +
                "name='" + name + '\'' +
                ", range=" + range +
                '}';
    }
}
