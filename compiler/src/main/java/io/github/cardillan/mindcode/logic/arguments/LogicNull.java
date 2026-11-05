package io.github.cardillan.mindcode.logic.arguments;

import io.github.cardillan.mindcode.common.SourcePosition;
import io.github.cardillan.mindcode.compiler.ast.nodes.AstLiteralNull;
import io.github.cardillan.mindcode.compiler.ast.nodes.AstMindcodeNode;
import io.github.cardillan.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public enum LogicNull implements LogicLiteral {
    NULL;

    @Override
    public SourcePosition sourcePosition() {
        return SourcePosition.EMPTY;
    }

    @Override
    public ArgumentType getType() {
        return ArgumentType.NULL_LITERAL;
    }

    @Override
    public ValueMutability getMutability() {
        return ValueMutability.CONSTANT;
    }

    @Override
    public boolean isNumericLiteral() {
        return true;
    }

    @Override
    public String toMlog() {
        return "null";
    }

    @Override
    public String format(@Nullable InstructionProcessor instructionProcessor) {
        return "null";
    }

    @Override
    public double getDoubleValue() {
        return 0.0;
    }

    @Override
    public long getLongValue() {
        return 0;
    }

    @Override
    public @Nullable Object getObject() {
        return null;
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public AstMindcodeNode asAstNode(SourcePosition position) {
        return new AstLiteralNull(position, "null");
    }
}
