package info.teksol.mindcode.ast;

import info.teksol.mindcode.compiler.SourceFile;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.logic.LogicVariable;
import org.antlr.v4.runtime.Token;

import java.util.Objects;

public class StackAllocation extends BaseAstNode {
    private final LogicVariable stack;
    private final Range range;

    StackAllocation(Token startToken, SourceFile sourceFile, String stack, Range range) {
        super(startToken, sourceFile);
        // TODO This might not be a block variable actually. Review during refactoring
        this.stack = LogicVariable.block(stack);
        this.range = range;
    }

    StackAllocation(Token startToken, SourceFile sourceFile, String stack, int first, int last) {
        this(startToken, sourceFile, stack, new InclusiveRange(startToken, sourceFile,
                new NumericLiteral(startToken, sourceFile, first),
                new NumericLiteral(startToken, sourceFile, last)));
    }

    StackAllocation(Token startToken, SourceFile sourceFile, String stack) {
        super(startToken, sourceFile);
        // TODO This might not be a block variable actually. Review during refactoring
        this.stack = LogicVariable.block(stack);
        this.range = null;
    }

    public LogicVariable getStack() {
        return stack;
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
                && Objects.equals(stack, that.stack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stack, range);
    }

    @Override
    public String toString() {
        return "StackAllocation{" +
                "stack='" + stack + '\'' +
                ", range=" + range +
                '}';
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.ALLOCATION;
    }
}
