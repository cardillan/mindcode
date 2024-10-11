package info.teksol.mindcode.ast;


import info.teksol.mindcode.InputFile;
import info.teksol.mindcode.compiler.generator.AstContextType;
import org.antlr.v4.runtime.Token;

import java.util.Objects;

public class HeapAllocation extends BaseAstNode {
    private final String name;
    private final Range range;

    HeapAllocation(Token startToken, InputFile inputFile, String name, Range range) {
        super(startToken, inputFile, range);
        this.name = name;
        this.range = range;
    }

    public HeapAllocation(Token startToken, InputFile inputFile, String name, int first, int last) {
        this(startToken, inputFile, name, new InclusiveRange(startToken, inputFile,
                new NumericLiteral(startToken, inputFile, first),
                new NumericLiteral(startToken, inputFile, last)));
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
        HeapAllocation that = (HeapAllocation) o;
        return Objects.equals(name, that.name)
                && Objects.equals(range, that.range);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, range);
    }

    @Override
    public String toString() {
        return "HeapAllocation{" +
                "name='" + name + '\'' +
                ", range=" + range +
                '}';
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.ALLOCATION;
    }
}
